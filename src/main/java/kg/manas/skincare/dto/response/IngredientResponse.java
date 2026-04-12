// Response используя Record как в твоем примере
package kg.manas.skincare.dto.response;

import kg.manas.skincare.model.Ingredient;

public record IngredientResponse(
        Long id,
        String name,
        String description,
        Integer irritationLevel,
        Integer comedogenicLevel,
        Integer minAge, // Добавлено
        Boolean isActive
) {
    public static IngredientResponse fromEntity(Ingredient ingredient) {
        return new IngredientResponse(
                ingredient.getId(),
                ingredient.getName(),
                ingredient.getDescription(),
                ingredient.getIrritationLevel(),
                ingredient.getComedogenicLevel(),
                ingredient.getMinAge(), // Добавлено
                ingredient.getIsActive()
        );
    }
}