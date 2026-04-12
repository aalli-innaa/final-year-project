package kg.manas.skincare.repository;

import kg.manas.skincare.model.Ingredient;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface IngredientRepository extends JpaRepository<Ingredient, Long> {
    Optional<Ingredient> findByNameIgnoreCase(String name);
    List<Ingredient> findAllByIsActiveTrue();
}