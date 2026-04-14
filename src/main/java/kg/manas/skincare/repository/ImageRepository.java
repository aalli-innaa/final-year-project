package kg.manas.skincare.repository;

import kg.manas.skincare.model.Image;
import kg.manas.skincare.enums.ImageType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ImageRepository extends JpaRepository<Image, Long> {
    List<Image> findByProduct_IdAndType(Long productId, ImageType type);
    List<Image> findByProduct_Id(Long productId);
    Optional<Image> findFirstByProduct_IdAndType(Long productId, ImageType type);
}