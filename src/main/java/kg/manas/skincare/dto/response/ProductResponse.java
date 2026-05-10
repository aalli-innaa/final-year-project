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
        List<String> ingredientNames,
        List<Long> ingredientIds, // Добавили ID для фронтенда
        List<String> skinTypes     // Добавили типы кожи
) {
    public static ProductResponse fromEntity(Product product) {
        String url = product.getImageUrl();
        if ((url == null || url.isEmpty()) && product.getImage() != null) {
            url = product.getImage().getImageUrl();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .usageInstructions(product.getUsageInstructions())
                .imageUrl(url)
                .targetGender(product.getTargetGender())
                .ingredientNames(product.getIngredients() != null
                        ? product.getIngredients().stream().map(Ingredient::getName).toList()
                        : List.of())
                .ingredientIds(product.getIngredients() != null
                        ? product.getIngredients().stream().map(Ingredient::getId).toList()
                        : List.of())
                .skinTypes(product.getSuitableSkinTypes() != null
                        ? product.getSuitableSkinTypes().stream().map(Enum::name).toList()
                        : List.of())
                .build();
    }
}