package kg.manas.skincare.dto.response;

import kg.manas.skincare.model.Product;
import java.util.List;

public record RecommendationResponse(
        List<Product> products,
        List<String> warnings
) {}