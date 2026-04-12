package kg.manas.skincare.repository;

import kg.manas.skincare.model.UserProfile;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UserProfileRepository extends JpaRepository<UserProfile, Long> {

    /**
     * Найти профиль по user_id
     */
    Optional<UserProfile> findByUser_UserId(Long userId);

    /**
     * Проверить существует ли профиль для пользователя
     */
    boolean existsByUser_UserId(Long userId);
}