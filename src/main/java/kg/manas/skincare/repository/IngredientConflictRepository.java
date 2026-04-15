package kg.manas.skincare.repository;

import kg.manas.skincare.model.IngredientConflict;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface IngredientConflictRepository extends JpaRepository<IngredientConflict, Long> {

    @Query("SELECT ic FROM IngredientConflict ic " +
            "WHERE (ic.ingredient1.id IN :ids1 AND ic.ingredient2.id IN :ids2) " +
            "OR (ic.ingredient1.id IN :ids2 AND ic.ingredient2.id IN :ids1)")
    List<IngredientConflict> findConflictsBetweenLists(
            @Param("ids1") List<Long> ids1,
            @Param("ids2") List<Long> ids2
    );
}