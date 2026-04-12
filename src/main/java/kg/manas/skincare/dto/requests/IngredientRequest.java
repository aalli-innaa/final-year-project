package kg.manas.skincare.dto.requests;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class IngredientRequest {
    @NotBlank(message = "Название обязательно")
    private String name;

    private String description;

    @Min(0) @Max(5)
    private Integer irritationLevel;

    @Min(0) @Max(5)
    private Integer comedogenicLevel;

    private Boolean isActive;
}