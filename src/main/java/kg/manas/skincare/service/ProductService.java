package kg.manas.skincare.service;

import kg.manas.skincare.dto.requests.ProductRequest;
import kg.manas.skincare.dto.response.ProductResponse;
import kg.manas.skincare.exceptions.BusinessException;
import kg.manas.skincare.exceptions.ErrorCode;
import kg.manas.skincare.model.Ingredient;
import kg.manas.skincare.model.Product;
import kg.manas.skincare.repository.IngredientRepository;
import kg.manas.skincare.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final IngredientRepository ingredientRepository;

    @Transactional
    public ProductResponse createProduct(ProductRequest request) {
        // Загружаем ингредиенты из базы по списку ID
        Set<Ingredient> ingredients = new HashSet<>(ingredientRepository.findAllById(request.ingredientIds()));

        if (ingredients.isEmpty() && request.ingredientIds() != null && !request.ingredientIds().isEmpty()) {
            throw new BusinessException(ErrorCode.INTERNAL_EXCEPTION, "Ингредиенты не найдены");
        }

        Product product = Product.builder()
                .name(request.name())
                .brand(request.brand())
                .description(request.description())
                .usageInstructions(request.usageInstructions())
                .imageUrl(request.imageUrl())
                .targetGender(request.targetGender())
                .ingredients(ingredients)
                .suitableSkinTypes(request.skinTypes())
                .isActive(true)
                .build();

        return ProductResponse.fromEntity(productRepository.save(product));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAllActiveProducts() {
        return productRepository.findAllByIsActiveTrue().stream()
                .map(ProductResponse::fromEntity)
                .collect(Collectors.toList());
    }



    @Transactional
    public ProductResponse patchProduct(Long id, ProductRequest request) {
        // 1. Находим продукт
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.PRODUCT_NOT_FOUND));

        // 2. Обновляем простые текстовые поля (только если они не null)
        if (request.name() != null) product.setName(request.name());
        if (request.brand() != null) product.setBrand(request.brand());
        if (request.description() != null) product.setDescription(request.description());
        if (request.usageInstructions() != null) product.setUsageInstructions(request.usageInstructions());
        if (request.targetGender() != null) product.setTargetGender(request.targetGender());
        // imageUrl обычно обновляется через ImageController, но если нужно вручную:
        if (request.imageUrl() != null) product.setImageUrl(request.imageUrl());

        // 3. Обновляем типы кожи (если прислали новый список)
        if (request.skinTypes() != null && !request.skinTypes().isEmpty()) {
            product.setSuitableSkinTypes(request.skinTypes());
        }

        // 4. Обновляем состав ингредиентов (если прислали новые ID)
        if (request.ingredientIds() != null && !request.ingredientIds().isEmpty()) {
            Set<Ingredient> newIngredients = new HashSet<>(
                    ingredientRepository.findAllById(request.ingredientIds())
            );
            product.setIngredients(newIngredients);
        }

        // 5. Сохраняем и возвращаем ответ
        return ProductResponse.fromEntity(productRepository.save(product));
    }
}