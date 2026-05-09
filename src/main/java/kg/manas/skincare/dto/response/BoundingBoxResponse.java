package kg.manas.skincare.dto.response;

import lombok.Builder;

@Builder
public record BoundingBoxResponse(
        Double x1,
        Double y1,
        Double x2,
        Double y2,
        Double confidence
) {}

