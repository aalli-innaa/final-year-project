package kg.manas.skincare.dto.response;

import kg.manas.skincare.enums.SkinConcern;
import lombok.Builder;
import java.time.LocalDateTime;
import java.util.List;

@Builder
public record AnalysisResponse(
        Long analysisId,
        SkinConcern concern,
        Double confidence,
        String imageUrl,
        List<ProductResponse> recommendedProducts,
        List<String> warnings, // Предупреждения о конфликтах ингредиентов
        LocalDateTime createdAt
) {}