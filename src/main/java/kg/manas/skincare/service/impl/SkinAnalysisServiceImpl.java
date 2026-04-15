package kg.manas.skincare.service.impl;

import jakarta.transaction.Transactional;
import kg.manas.skincare.dto.response.AnalysisHistoryResponse;
import kg.manas.skincare.dto.response.AnalysisResponse;
import kg.manas.skincare.dto.response.ProductResponse;
import kg.manas.skincare.dto.response.RecommendationResponse;
import kg.manas.skincare.enums.SkinConcern;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class SkinAnalysisServiceImpl implements SkinAnalysisService {

    private final SkinAnalysisRepository analysisRepository;
    private final UserPhotoService userPhotoService;
    private final RecommendationService recommendationService;

    @Transactional
    @Override
    public AnalysisResponse performAnalysis(User user, MultipartFile photo) {
        // 1. Сохраняем селфи
        UserPhoto userPhoto = userPhotoService.uploadFacePhoto(user, photo);

        // 2. Имитируем работу AI (позже заменим на Python)
        SkinConcern mockConcern = SkinConcern.ACNE;
        Double mockConfidence = 0.96;

        // 3. Получаем данные профиля безопасно
        SkinType skinType = SkinType.NORMAL; // Значение по умолчанию
        int userAge = 0;

        if (user.getUserProfile() != null) {
            skinType = user.getUserProfile().getSkinType();

            if (user.getUserProfile().getBirthDate() != null) {
                userAge = Period.between(user.getUserProfile().getBirthDate(), LocalDate.now()).getYears();
            }
        } else {
            // Если профиля нет, можно либо выдать ошибку, либо использовать значения по умолчанию
            log.warn("User {} has no profile, using default values", user.getUserId());
        }

// 4. Теперь вызываем сервис с проверенными данными
        RecommendationResponse recs = recommendationService.getPersonalizedCare(
                mockConcern,
                skinType,
                userAge
        );

        // 4. Сохраняем анализ в БД
        SkinAnalysis analysis = SkinAnalysis.builder()
                .user(user)
                .userPhoto(userPhoto)
                .primaryConcern(mockConcern)
                .confidence(mockConfidence)
                .build();
        analysisRepository.save(analysis);

        // 5. Формируем итоговый Response
        return AnalysisResponse.builder()
                .analysisId(analysis.getAnalysisId())
                .concern(mockConcern)
                .confidence(mockConfidence)
                .imageUrl(userPhoto.getImageUrl())
                .recommendedProducts(mapToProductDto(recs.products()))
                .warnings(recs.warnings())
                .createdAt(analysis.getCreatedAt())
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