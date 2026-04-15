package kg.manas.skincare.service.impl;

import kg.manas.skincare.dto.response.RecommendationResponse;
import kg.manas.skincare.enums.SkinConcern;
import kg.manas.skincare.enums.SkinType;
import kg.manas.skincare.model.Ingredient;
import kg.manas.skincare.model.IngredientConflict;
import kg.manas.skincare.model.Product;
import kg.manas.skincare.repository.IngredientConflictRepository;
import kg.manas.skincare.repository.ProductRepository;
import kg.manas.skincare.service.RecommendationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class RecommendationServiceImpl implements RecommendationService {

    private final ProductRepository productRepository;
    private final IngredientConflictRepository conflictRepository;

    @Override
    public RecommendationResponse getPersonalizedCare(SkinConcern concern, SkinType skinType, int userAge) {
        // 1. Ищем подходящие по проблеме и типу кожи продукты
        List<Product> candidates = productRepository.findRecommendedProducts(concern, skinType);

        // 2. ФИЛЬТР ПО ВОЗРАСТУ (Принцип самого строгого ограничения)
        // Продукт пропускается только если ВСЕ его ингредиенты подходят под возраст юзера
        List<Product> ageSafeProducts = candidates.stream()
                .filter(product -> product.getIngredients().stream()
                        .allMatch(ingredient -> ingredient.getMinAge() <= userAge))
                .collect(Collectors.toList());

        // Берем топ-3 из безопасных продуктов
        List<Product> topProducts = ageSafeProducts.stream().limit(3).toList();

        // 3. Проверяем конфликты при наслоении (layering)
        List<String> warnings = new ArrayList<>();

        for (int i = 0; i < topProducts.size(); i++) {
            for (int j = i + 1; j < topProducts.size(); j++) {
                Product p1 = topProducts.get(i);
                Product p2 = topProducts.get(j);

                List<Long> ids1 = p1.getIngredients().stream().map(Ingredient::getId).toList();
                List<Long> ids2 = p2.getIngredients().stream().map(Ingredient::getId).toList();

                List<IngredientConflict> conflicts = conflictRepository.findConflictsBetweenLists(ids1, ids2);

                for (IngredientConflict c : conflicts) {
                    warnings.add(String.format("⚠️ Не используйте «%s» вместе с «%s»: компоненты %s и %s конфликтуют. Рекомендуем использовать продукты раздельно: один утром, а другой вечером.",
                            p1.getName(), p2.getName(), c.getIngredient1().getName(), c.getIngredient2().getName()));
                }
            }
        }
        return new RecommendationResponse(topProducts, warnings);
    }
}