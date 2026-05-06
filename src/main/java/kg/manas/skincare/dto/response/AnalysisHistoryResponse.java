package kg.manas.skincare.dto.response;

import kg.manas.skincare.enums.AcneSeverity;

import java.time.LocalDateTime;

public record AnalysisHistoryResponse(
        Long analysisId,
        AcneSeverity concern,
        String imageUrl,
        LocalDateTime createdAt
) {}
