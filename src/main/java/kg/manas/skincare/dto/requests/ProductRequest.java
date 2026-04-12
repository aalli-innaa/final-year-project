// ProductRequest.java
package kg.manas.skincare.dto.requests;

import kg.manas.skincare.enums.SkinType;
import java.util.Set;

public record ProductRequest(
        String name,
        String brand,
        String description,
        String usageInstructions,
        String imageUrl,
        String targetGender,
        Set<Long> ingredientIds,
        Set<SkinType> skinTypes
) {}