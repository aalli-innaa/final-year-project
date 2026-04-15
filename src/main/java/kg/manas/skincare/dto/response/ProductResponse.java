package kg.manas.skincare.dto.response;

import kg.manas.skincare.model.Ingredient;
import kg.manas.skincare.model.Product;
import lombok.Builder;
import java.util.List;

@Builder
public record ProductResponse(
        Long id,
        String name,
        String brand,
        String description,
        String usageInstructions,
        String imageUrl,
        String targetGender,
        List<String> ingredientNames
) {
    public static ProductResponse fromEntity(Product product) {
        // Получаем URL из связанной сущности Image (которую мы добавили в Product)
        String url = (product.getImage() != null) ? product.getImage().getImageUrl() : null;

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .usageInstructions(product.getUsageInstructions())
                .imageUrl(url) // Берем из сущности Image
                .targetGender(product.getTargetGender())
                .ingredientNames(product.getIngredients() != null
                        ? product.getIngredients().stream().map(Ingredient::getName).toList()
                        : List.of())
                .build();
    }
}