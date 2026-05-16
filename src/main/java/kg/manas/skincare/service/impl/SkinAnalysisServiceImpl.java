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
import kg.manas.skincare.dto.response.AiResponseDTO;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.type.TypeReference;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkinAnalysisServiceImpl implements SkinAnalysisService {

    private final SkinAnalysisRepository analysisRepository;
    private final UserPhotoService userPhotoService;
    private final RecommendationService recommendationService;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper; // Для конвертации боксов в JSON и обратно

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
            body.add("image", photo.getResource());

            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

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

            log.info("AI Analysis success: Severity={}, Count={}", aiConcern, aiCount);

        } catch (Exception e) {
            log.error("Error calling AI service: {}", e.getMessage());
            throw new BusinessException(ErrorCode.AI_SERVICE_UNAVAILABLE);
        }

        // 3. Получаем данные профиля
        SkinType skinType = user.getUserProfile().getSkinType();
        int userAge = Period.between(user.getUserProfile().getBirthDate(), LocalDate.now()).getYears();

        // 4. Вызываем сервис рекомендаций
        RecommendationResponse recs = recommendationService.getPersonalizedCare(
                aiConcern,
                skinType,
                userAge
        );

        // 5. Сохраняем анализ в БД (ДОБАВЛЕНЫ НОВЫЕ ПОЛЯ)
        String boxesJson = "[]";
        try {
            boxesJson = objectMapper.writeValueAsString(boxes);
        } catch (Exception e) {
            log.error("Failed to serialize boxes to JSON", e);
        }

        SkinAnalysis analysis = SkinAnalysis.builder()
                .user(user)
                .userPhoto(userPhoto)
                .primaryConcern(aiConcern)
                .confidence(aiConfidence)
                .acneCount(aiCount)
                .imageWidth(imageWidth)
                .imageHeight(imageHeight)
                .boxes(boxesJson) // Сохраняем как строку JSON
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

    @Override
    public AnalysisResponse getAnalysisDetails(Long id, User user) {
        // 1. Находим анализ в БД
        SkinAnalysis analysis = analysisRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANALYSIS_NOT_FOUND));

        // 2. Проверяем владельца
        if (!analysis.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 3. Десериализация боксов из JSON (ДОБАВЛЕНО)
        List<BoundingBoxResponse> boxes = List.of();
        try {
            if (analysis.getBoxes() != null) {
                boxes = objectMapper.readValue(analysis.getBoxes(), new TypeReference<List<BoundingBoxResponse>>() {});
            }
        } catch (Exception e) {
            log.error("Failed to parse boxes from database", e);
        }

        // 4. Рекомендации
        SkinType skinType = user.getUserProfile().getSkinType();
        int userAge = Period.between(user.getUserProfile().getBirthDate(), LocalDate.now()).getYears();

        RecommendationResponse recs = recommendationService.getPersonalizedCare(
                analysis.getPrimaryConcern(),
                skinType,
                userAge
        );

        // 5. Возвращаем полный объект (ДОБАВЛЕНЫ ПОЛЯ ИЗ БД)
        return AnalysisResponse.builder()
                .analysisId(analysis.getAnalysisId())
                .concern(analysis.getPrimaryConcern())
                .confidence(analysis.getConfidence())
                .imageUrl(analysis.getUserPhoto().getImageUrl())
                .recommendedProducts(mapToProductDto(recs.products()))
                .warnings(recs.warnings())
                .createdAt(analysis.getCreatedAt())
                .acneCount(analysis.getAcneCount())
                .imageWidth(analysis.getImageWidth())
                .imageHeight(analysis.getImageHeight())
                .boxes(boxes)
                .build();
    }

    private List<ProductResponse> mapToProductDto(List<Product> products) {
        return products.stream()
                .map(ProductResponse::fromEntity)
                .toList();
    }

    @Override
    public List<AnalysisHistoryResponse> getUserHistory(User user) {
        return analysisRepository.findByUser_UserIdOrderByCreatedAtDesc(user.getUserId())
                .stream()
                .map(a -> {
                    // Парсим боксы из строки обратно в список для каждого элемента истории
                    List<BoundingBoxResponse> boxesList = List.of();
                    try {
                        if (a.getBoxes() != null) {
                            boxesList = objectMapper.readValue(a.getBoxes(), new TypeReference<List<BoundingBoxResponse>>() {});
                        }
                    } catch (Exception e) {
                        log.error("Failed to parse boxes for history item {}", a.getAnalysisId());
                    }

                    return new AnalysisHistoryResponse(
                            a.getAnalysisId(),
                            a.getPrimaryConcern(),
                            a.getUserPhoto().getImageUrl(),
                            a.getCreatedAt(),
                            a.getConfidence(),
                            a.getAcneCount(),
                            a.getImageWidth(),
                            a.getImageHeight(),
                            boxesList
                    );
                }).toList();
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