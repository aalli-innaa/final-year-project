package kg.manas.skincare.dto.response;

import lombok.Builder;
import java.time.LocalDateTime;

@Builder
public record ImageResponse(
        Long imageId,
        Long productId,
        String imageUrl,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}