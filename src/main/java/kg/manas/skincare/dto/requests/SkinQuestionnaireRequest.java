package kg.manas.skincare.dto.requests;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import kg.manas.skincare.enums.SkinType;
import lombok.*;

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
}