package kg.manas.skincare.dto.response;

import kg.manas.skincare.model.Ingredient;
import kg.manas.skincare.model.Product;

public record ProductResponse(
        Long id,
        String name,
        String brand,
        String description,
        String usageInstructions,
        String imageUrl,
        String targetGender,
        java.util.List<String> ingredientNames // Просто список названий для красоты
) {
    public static ProductResponse fromEntity(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getDescription(),
                product.getUsageInstructions(),
                product.getImageUrl(),
                product.getTargetGender(),
                product.getIngredients().stream().map(Ingredient::getName).toList()
        );
    }
}