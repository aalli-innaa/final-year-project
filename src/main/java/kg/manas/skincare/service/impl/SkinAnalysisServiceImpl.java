package kg.manas.skincare.service.impl;

import jakarta.transaction.Transactional;
import kg.manas.skincare.dto.response.AnalysisHistoryResponse;
import kg.manas.skincare.dto.response.AnalysisResponse;
import kg.manas.skincare.enums.SkinConcern;
import kg.manas.skincare.exceptions.BusinessException;
import kg.manas.skincare.exceptions.ErrorCode;
import kg.manas.skincare.model.SkinAnalysis;
import kg.manas.skincare.model.User;
import kg.manas.skincare.model.UserPhoto;
import kg.manas.skincare.repository.SkinAnalysisRepository;
import kg.manas.skincare.service.SkinAnalysisService;
import kg.manas.skincare.service.UserPhotoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Slf4j
public class SkinAnalysisServiceImpl implements SkinAnalysisService {

    private final SkinAnalysisRepository analysisRepository;
    private final UserPhotoService userPhotoService;

    @Transactional
    @Override
    public AnalysisResponse performAnalysis(User user, MultipartFile photo) {
        UserPhoto userPhoto = userPhotoService.uploadFacePhoto(user, photo);

        SkinConcern mockConcern = SkinConcern.values()[new Random().nextInt(SkinConcern.values().length)];

        // Простое число Double
        Double mockConfidence = 0.85 + (0.99 - 0.85) * new Random().nextDouble();

        SkinAnalysis analysis = SkinAnalysis.builder()
                .user(user)
                .userPhoto(userPhoto)
                .primaryConcern(mockConcern)
                .confidence(mockConfidence)
                .build();

        analysisRepository.save(analysis);

        return new AnalysisResponse(
                analysis.getAnalysisId(),
                analysis.getPrimaryConcern(),
                analysis.getConfidence(),
                userPhoto.getImageUrl(),
                analysis.getCreatedAt()
        );
    }



    @Override
    public List<AnalysisHistoryResponse> getUserHistory(User user) {
        // Вызываем метод с правильным именем UserId
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
        // 1. Находим анализ
        SkinAnalysis analysis = analysisRepository.findById(analysisId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ANALYSIS_NOT_FOUND));

        // 2. Проверяем доступ
        if (!analysis.getUser().getUserId().equals(user.getUserId())) {
            throw new BusinessException(ErrorCode.ACCESS_DENIED);
        }

        // 3. ЗАПОМИНАЕМ фото перед тем как удалить анализ
        UserPhoto photoToDelete = analysis.getUserPhoto();

        // 4. СНАЧАЛА удаляем сам анализ из репозитория
        // Это разорвет связь в базе данных
        analysisRepository.delete(analysis);

        // 5. И ТОЛЬКО ТЕПЕРЬ удаляем фото и файл с диска
        userPhotoService.deletePhoto(photoToDelete);

        log.info("Analysis and photo successfully deleted");
    }
}