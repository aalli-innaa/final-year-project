package kg.manas.skincare.repository;

import kg.manas.skincare.model.UserPhoto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserPhotoRepository extends JpaRepository<UserPhoto, Long> {
    List<UserPhoto> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}