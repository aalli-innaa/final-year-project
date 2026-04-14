package kg.manas.skincare.dto.response;

import kg.manas.skincare.enums.SkinConcern;

import java.time.LocalDateTime;

public record AnalysisResponse(
        Long analysisId,
        SkinConcern concern,
        Double confidence,
        String imageUrl,
        LocalDateTime createdAt
) {}
