package kg.manas.skincare.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;
import kg.manas.skincare.enums.SkinType;
import kg.manas.skincare.model.UserProfile;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record UserProfileResponse(
        Long id,
        Long userId,
        SkinType skinType,
        @JsonFormat(pattern = "yyyy-MM-dd")
        LocalDate birthDate,
        Integer age,           // вычисляется из birthDate — не хранится в БД
        String gender,         // MALE | FEMALE
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
        LocalDateTime updatedAt
) {

    public static UserProfileResponse fromEntity(UserProfile profile) {
        return new UserProfileResponse(
                profile.getId(),
                profile.getUser().getUserId(),
                profile.getSkinType(),
                profile.getBirthDate(),
                profile.getAge(),      // вызываем метод из entity
                profile.getGender(),
                profile.getUpdatedAt()
        );
    }
}