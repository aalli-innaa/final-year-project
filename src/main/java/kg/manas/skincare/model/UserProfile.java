package kg.manas.skincare.model;

import jakarta.persistence.*;
import kg.manas.skincare.enums.SkinType;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "user_profiles")
@EntityListeners(AuditingEntityListener.class)
public class UserProfile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "skin_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private SkinType skinType;

    /**
     * Дата рождения — лучше чем возраст, потому что возраст считается автоматически
     * и не устаревает со временем.
     * Метод getAge() вычисляет актуальный возраст прямо сейчас.
     */
    @Column(name = "birth_date")
    private LocalDate birthDate;

    @Column(name = "gender")
    private String gender;  // MALE | FEMALE

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Вычисляем возраст на лету — не храним в БД, не устаревает
     */
    public Integer getAge() {
        if (birthDate == null) return null;
        return java.time.Period.between(birthDate, LocalDate.now()).getYears();
    }
}