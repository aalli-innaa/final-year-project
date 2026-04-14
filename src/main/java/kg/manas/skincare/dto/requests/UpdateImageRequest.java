package kg.manas.skincare.dto.requests;
import kg.manas.skincare.enums.ImageAngle;

public record UpdateImageRequest(

        Long productId,
        ImageAngle angle
) {
}