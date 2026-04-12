package kg.manas.skincare.dto.response;

import kg.manas.skincare.enums.SkinType;
import kg.manas.skincare.model.UserProfile;
import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        Long userId,
        SkinType skinType,
        LocalDateTime updatedAt
) {

    /**
     * Конвертор из Entity в DTO
     */
    public static UserProfileResponse fromEntity(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getUser().getUserId(),
                profile.getSkinType(),
                profile.getUpdatedAt()
        );
    }
}