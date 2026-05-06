package kg.manas.skincare.repository;

import kg.manas.skincare.enums.AcneSeverity;
import kg.manas.skincare.model.IngredientBenefit;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface IngredientBenefitRepository extends JpaRepository<IngredientBenefit, Long> {

    /**
     * Найти все "пользы" для конкретной проблемы.
     * Например: дай мне все ингредиенты от ACNE.
     */
    List<IngredientBenefit> findAllByBenefit(AcneSeverity benefit);

    /**
     * Найти все свойства конкретного ингредиента.
     */
    List<IngredientBenefit> findAllByIngredient_Id(Long ingredientId);
}