package kg.manas.skincare.service;

import kg.manas.skincare.dto.requests.IngredientRequest;
import kg.manas.skincare.dto.response.IngredientResponse;
import kg.manas.skincare.enums.AcneSeverity;
import kg.manas.skincare.exceptions.BusinessException;
import kg.manas.skincare.exceptions.ErrorCode;
import kg.manas.skincare.model.Ingredient;
import kg.manas.skincare.model.IngredientBenefit;
import kg.manas.skincare.repository.IngredientBenefitRepository;
import kg.manas.skincare.repository.IngredientRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class IngredientService {

    private final IngredientRepository ingredientRepository;
    private final IngredientBenefitRepository benefitRepository;

    @Transactional(readOnly = true)
    public List<IngredientResponse> getAllActiveIngredients() { // <--- Должно быть так
        return ingredientRepository.findAllByIsActiveTrue().stream()
                .map(IngredientResponse::fromEntity)
                .collect(Collectors.toList());
    }

    @Transactional
    public IngredientResponse createIngredient(IngredientRequest request) {
        if (ingredientRepository.findByNameIgnoreCase(request.getName()).isPresent()) {
            throw new BusinessException(ErrorCode.INTERNAL_EXCEPTION, "Ингредиент уже существует");
        }

        Ingredient ingredient = Ingredient.builder()
                .name(request.getName())
                .description(request.getDescription())
                .irritationLevel(request.getIrritationLevel())
                .comedogenicLevel(request.getComedogenicLevel())
                .minAge(request.getMinAge() != null ? request.getMinAge() : 0) // Добавлено
                .isActive(request.getIsActive() != null ? request.getIsActive() : true)
                .build();

        return IngredientResponse.fromEntity(ingredientRepository.save(ingredient));
    }

    @Transactional
    public IngredientResponse patchIngredient(Long id, IngredientRequest request) {
        Ingredient ingredient = ingredientRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_EXCEPTION, "Ингредиент не найден"));

        if (request.getName() != null) {
            ingredientRepository.findByNameIgnoreCase(request.getName())
                    .ifPresent(existing -> {
                        if (!existing.getId().equals(id)) {
                            throw new BusinessException(ErrorCode.INTERNAL_EXCEPTION, "Имя уже занято");
                        }
                    });
            ingredient.setName(request.getName());
        }

        if (request.getDescription() != null) ingredient.setDescription(request.getDescription());
        if (request.getIrritationLevel() != null) ingredient.setIrritationLevel(request.getIrritationLevel());
        if (request.getComedogenicLevel() != null) ingredient.setComedogenicLevel(request.getComedogenicLevel());
        if (request.getMinAge() != null) ingredient.setMinAge(request.getMinAge()); // Добавлено
        if (request.getIsActive() != null) ingredient.setIsActive(request.getIsActive());

        return IngredientResponse.fromEntity(ingredientRepository.save(ingredient));
    }



    @Transactional
    public void addBenefitToIngredient(Long ingredientId, AcneSeverity concern, Double score) {
        Ingredient ingredient = ingredientRepository.findById(ingredientId)
                .orElseThrow(() -> new BusinessException(ErrorCode.INTERNAL_EXCEPTION, "Ингредиент не найден"));

        IngredientBenefit benefit = IngredientBenefit.builder()
                .ingredient(ingredient)
                .benefit(concern)
                .efficiencyScore(score)
                .build();

        benefitRepository.save(benefit);
    }
}