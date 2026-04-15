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
        // 1. Сначала пробуем взять ссылку из текстового поля самого продукта
        String url = product.getImageUrl();

        // 2. Если там пусто, пробуем взять из связанной таблицы Image
        if ((url == null || url.isEmpty()) && product.getImage() != null) {
            url = product.getImage().getImageUrl();
        }

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .brand(product.getBrand())
                .description(product.getDescription())
                .usageInstructions(product.getUsageInstructions())
                .imageUrl(url) // Теперь здесь будет либо ссылка из текста, либо из таблицы
                .targetGender(product.getTargetGender())
                .ingredientNames(product.getIngredients() != null
                        ? product.getIngredients().stream().map(Ingredient::getName).toList()
                        : List.of())
                .build();
    }
}