package kg.manas.skincare.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ChangePasswordRequest {
        @NotBlank(message = "VALIDATION.CHANGE_PASSWORD.CURRENT_PASSWORD.NOT_BLANK")
        @Schema(example = "<PASSWORD>")
        private String currentPassword;

        @NotBlank(message = "VALIDATION.CHANGE_PASSWORD.NEW_PASSWORD.NOT_BLANK")
        @Size(
                min=8,
                max=50,
                message = "VALIDATION.CHANGE_PASSWORD.NEW_PASSWORD.SIZE"
        )
        @Pattern(
                regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*\\W).*$",
                message = "VALIDATION.CHANGE_PASSWORD.NEW_PASSWORD.WEAK"
        )
        @Schema(example = "<PASSWORD>")
        private String newPassword;

        @NotBlank(message = "VALIDATION.CHANGE_PASSWORD.CONFIRM_NEW_PASSWORD.NOT_BLANK")
        @Size(
                min=8,
                max=50,
                message = "VALIDATION.CHANGE_PASSWORD.CONFIRM_NEW_PASSWORD.SIZE"
        )

        @Schema(example = "<PASSWORD>")
        private String confirmNewPassword;


}
