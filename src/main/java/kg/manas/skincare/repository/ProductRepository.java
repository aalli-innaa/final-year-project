package kg.manas.skincare.repository;

import kg.manas.skincare.enums.SkinConcern;
import kg.manas.skincare.enums.SkinType;
import kg.manas.skincare.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findAllByIsActiveTrue();

    // СТАЛО (правильно):
    @Query("SELECT DISTINCT p FROM Product p " +
            "JOIN p.ingredients i " +
            "JOIN i.benefits b " +
            "JOIN p.suitableSkinTypes st " +           // ← JOIN по коллекции
            "WHERE b.benefit = :concern " +
            "AND st = :skinType " +                    // ← сравниваем элемент
            "AND p.isActive = true")
    List<Product> findRecommendedProducts(
            @Param("concern") SkinConcern concern,
            @Param("skinType") SkinType skinType
    );
}