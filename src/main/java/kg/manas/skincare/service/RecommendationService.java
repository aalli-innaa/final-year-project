package kg.manas.skincare.service;

import kg.manas.skincare.dto.response.RecommendationResponse;
import kg.manas.skincare.enums.AcneSeverity;
import kg.manas.skincare.enums.SkinType;

public interface RecommendationService {

    /**
     * Подбирает список продуктов на основе проблемы кожи и её типа,
     * а также проверяет их на конфликты при наслоении.
     */
    RecommendationResponse getPersonalizedCare(AcneSeverity concern, SkinType skinType, int userAge);
}