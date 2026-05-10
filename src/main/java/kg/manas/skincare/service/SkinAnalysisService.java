package kg.manas.skincare.service;

import kg.manas.skincare.dto.response.AnalysisHistoryResponse;
import kg.manas.skincare.dto.response.AnalysisResponse;
import kg.manas.skincare.model.User;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface SkinAnalysisService {

    /**
     * Выполняет анализ кожи пользователя по загруженному фото
     */
    AnalysisResponse performAnalysis(User user, MultipartFile photo);

    /**
     * Получает историю анализов пользователя
     */
    List<AnalysisHistoryResponse> getUserHistory(User user);

    /**
     * Удаляет конкретный анализ пользователя
     */
    void deleteAnalysis(Long analysisId, User user);

    AnalysisResponse getAnalysisDetails(Long id, User user);
}
