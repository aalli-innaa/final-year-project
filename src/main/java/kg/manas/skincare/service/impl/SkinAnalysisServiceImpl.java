package kg.manas.skincare.service.impl;

import jakarta.transaction.Transactional;
import kg.manas.skincare.dto.response.AnalysisHistoryResponse;
import kg.manas.skincare.dto.response.AnalysisResponse;
import kg.manas.skincare.dto.response.BoundingBoxResponse;
import kg.manas.skincare.dto.response.ProductResponse;
import kg.manas.skincare.dto.response.RecommendationResponse;
import kg.manas.skincare.enums.AcneSeverity;
import kg.manas.skincare.enums.SkinType;
import kg.manas.skincare.exceptions.BusinessException;
import kg.manas.skincare.exceptions.ErrorCode;
import kg.manas.skincare.model.Product;
import kg.manas.skincare.model.SkinAnalysis;
import kg.manas.skincare.model.User;
import kg.manas.skincare.model.UserPhoto;
import kg.manas.skincare.repository.SkinAnalysisRepository;
import kg.manas.skincare.service.RecommendationService;
import kg.manas.skincare.service.SkinAnalysisService;
import kg.manas.skincare.service.UserPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.Period;
import java.util.List;

import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import kg.manas.skincare.dto.response.AiResponseDTO; // Импортируй созданный DTO

@Service
@RequiredArgsConstructor
@Slf4j
public class SkinAnalysisServiceImpl implements SkinAnalysisService {

    private final SkinAnalysisRepository analysisRepository;
    private final UserPhotoService userPhotoService;
    private final RecommendationService recommendationService;
    private final RestTemplate restTemplate; // Добавь инъекцию

    private final String PYTHON_AI_URL = "http://localhost:5000/predict";

    @Transactional
    @Override
    public AnalysisResponse performAnalysis(User user, MultipartFile photo) {

        if (user.getUserProfile() == null || user.getUserProfile().getSkinType() == null) {
            throw new BusinessException(ErrorCode.PROFILE_REQUIRED);
        }

        // 1. Сохраняем селфи локально
        UserPhoto userPhoto = userPhotoService.uploadFacePhoto(user, photo);

        // 2. ОТПРАВКА ФОТО В PYTHON AI
        AcneSeverity aiConcern;
        Double aiConfidence;
        Integer aiCount = null;
        Integer imageWidth = null;
        Integer imageHeight = null;
        List<BoundingBoxResponse> boxes = List.of();

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            // Передаем ресурс файла
            body.add("image", photo.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // Вызов Flask сервера
            AiResponseDTO aiResponse = restTemplate.postForObject(PYTHON_AI_URL, requestEntity, AiResponseDTO.class);

            if (aiResponse == null || aiResponse.getAcneSeverity() == null) {
                throw new BusinessException(ErrorCode.AI_SERVICE_ERROR);
            }

            aiConcern = AcneSeverity.valueOf(aiResponse.getAcneSeverity());
            aiConfidence = aiResponse.getConfidence();
            aiCount = aiResponse.getCount();
            imageWidth = aiResponse.getImageWidth();
            imageHeight = aiResponse.getImageHeight();
            boxes = aiResponse.getBoxes() == null ? List.of() : aiResponse.getBoxes().stream().map(b ->
                    BoundingBoxResponse.builder()
                            .x1(b.getX1())
                            .y1(b.getY1())
                            .x2(b.getX2())
                            .y2(b.getY2())
                            .confidence(b.getConfidence())
                            .build()
            ).toList();

            log.info("AI Analysis success: Severity={}, Score={}", aiConcern, aiResponse.getScore());

        } catch (Exception e) {
            log.error("Error calling AI service: {}", e.getMessage());
            // Если ИИ упал, можно либо выдать ошибку, либо использовать MILD как fallback
            throw new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE);
        }

        // 3. Получаем данные профиля
        SkinType skinType = user.getUserProfile().getSkinType();
        int userAge = Period.between(user.getUserProfile().getBirthDate(), LocalDate.now()).getYears();

        // 4. Вызываем сервис рекомендаций с РЕАЛЬНЫМИ данными от ИИ
        RecommendationResponse recs = recommendationService.getPersonalizedCare(
                aiConcern,
                skinType,
                userAge
        );

        // 5. Сохраняем анализ в БД
        SkinAnalysis analysis = SkinAnalysis.builder()
                .user(user)
                .userPhoto(userPhoto)
                .primaryConcern(aiConcern) // Убедись, что поле в Entity называется так или переименуй
                .confidence(aiConfidence)
                .build();
        analysisRepository.save(analysis);

        // 6. Формируем итоговый Response
        return AnalysisResponse.builder()
                .analysisId(analysis.getAnalysisId())
                .concern(aiConcern)
                .confidence(aiConfidence)
                .imageUrl(userPhoto.getImageUrl())
                .recommendedProducts(mapToProductDto(recs.products()))
                .warnings(recs.warnings())
                .createdAt(analysis.getCreatedAt())
                .acneCount(aiCount)
                .imageWidth(imageWidth)
                .imageHeight(imageHeight)
                .boxes(boxes)
                .build();
    }

    private List<ProductResponse> mapToProductDto(List<Product> products) {
        // Используем готовый метод из ProductResponse
        return products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    public List<AnalysisHistoryResponse> getUserHistory(User user) {
        return analysisRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(a -> new AnalysisHistoryResponse(
                        a.getAnalysisId(),
                        a.getPrimaryConcern(),
                        a.getUserPhoto().getImageUrl(),
                        a.getCreatedAt()
                )).toList();
    }

    @Transactional
    @Override
    public void deleteAnalysis(Long analysisId, User user) {
        SkinAnalysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANALYSIS_NOT_FOUND));

        if (!analysis.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        UserPhoto photoToDelete = analysis.getUserPhoto();
        analysisRepository.delete(analysis);
        userPhotoService.deletePhoto(photoToDelete);
    }
}