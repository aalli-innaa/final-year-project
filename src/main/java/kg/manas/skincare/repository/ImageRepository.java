package kg.manas.skincare.repository;

import kg.manas.skincare.model.Image;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    Optional<Image> findByProduct_Id(Long productId);
    void deleteByProduct_Id(Long productId);
}