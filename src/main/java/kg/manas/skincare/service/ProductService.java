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
}