package kg.manas.skincare.dto.response;

import kg.manas.skincare.enums.AcneSeverity;

import java.time.LocalDateTime;
import java.util.List;

public record AnalysisHistoryResponse(
        Long analysisId,
        AcneSeverity concern,
        String imageUrl,
        LocalDateTime createdAt,
        Double confidence,
        Integer acneCount,
        Integer imageWidth,
        Integer imageHeight,
        List<BoundingBoxResponse> boxes
) {}