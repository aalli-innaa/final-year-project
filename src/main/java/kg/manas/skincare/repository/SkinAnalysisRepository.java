package kg.manas.skincare.repository;

import kg.manas.skincare.model.SkinAnalysis;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SkinAnalysisRepository extends JpaRepository<SkinAnalysis, Long> {
    // Замени Id на UserId, чтобы Spring понял, какое поле искать в классе User
    List<SkinAnalysis> findByUser_UserIdOrderByCreatedAtDesc(Long userId);
}