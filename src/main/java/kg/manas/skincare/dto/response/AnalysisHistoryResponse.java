package kg.manas.skincare.dto.response;

import kg.manas.skincare.enums.SkinConcern;

import java.time.LocalDateTime;

public record AnalysisHistoryResponse(
        Long analysisId,
        SkinConcern concern,
        String imageUrl,
        LocalDateTime createdAt
) {}
