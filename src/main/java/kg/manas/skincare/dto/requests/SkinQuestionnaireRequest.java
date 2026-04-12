package kg.manas.skincare.dto.requests;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import kg.manas.skincare.enums.SkinType;
import lombok.*;
import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkinQuestionnaireRequest {

    @NotNull(message = "Ответ на вопрос 1 обязателен")
    @Schema(
            example = "OILY",
            description = "Вопрос 1: Как выглядит твоя кожа обычно? (OILY/DRY/COMBINATION/SENSITIVE)"
    )
    private SkinType question1;

    @NotNull(message = "Ответ на вопрос 2 обязателен")
    @Schema(
            example = "OILY",
            description = "Вопрос 2: Как кожа реагирует после умывания? (OILY/DRY/COMBINATION/SENSITIVE)"
    )
    private SkinType question2;

    @NotNull(message = "Ответ на вопрос 3 обязателен")
    @Schema(
            example = "OILY",
            description = "Вопрос 3: Видны ли расширенные поры? (OILY/DRY/COMBINATION/SENSITIVE)"
    )
    private SkinType question3;

    @NotNull(message = "Ответ на вопрос 4 обязателен")
    @Schema(
            example = "OILY",
            description = "Вопрос 4: Как реагирует на декоративную косметику? (OILY/DRY/COMBINATION/SENSITIVE)"
    )
    private SkinType question4;

    @NotNull(message = "Ответ на вопрос 5 обязателен")
    @Schema(
            example = "OILY",
            description = "Вопрос 5: На что похожа кожа летом? (OILY/DRY/COMBINATION/SENSITIVE)"
    )
    private SkinType question5;

    @NotNull(message = "Дата рождения обязательна")
    @Past(message = "Дата рождения должна быть в прошлом")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @Schema(example = "1998-05-14", description = "Дата рождения (формат: yyyy-MM-dd)")
    private LocalDate birthDate;

    @NotNull(message = "Пол обязателен")
    @Schema(example = "FEMALE", description = "Пол: MALE или FEMALE")
    private String gender;
}