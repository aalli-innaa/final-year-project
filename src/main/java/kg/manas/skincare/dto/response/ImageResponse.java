package kg.manas.skincare.dto.response;

import kg.manas.skincare.enums.ImageAngle;
import kg.manas.skincare.enums.ImageType;
import lombok.Builder;

import java.time.LocalDateTime;
@Builder
public record ImageResponse(
        Long imageId,
        Long productId,
        String imageUrl,
        ImageType type,
        ImageAngle angle,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
