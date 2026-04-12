package kg.manas.skincare.model;

import jakarta.persistence.*;
import kg.manas.skincare.enums.SkinType;
import lombok.*;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
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

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}