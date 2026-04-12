package kg.manas.skincare.model;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ingredients")
@EntityListeners(AuditingEntityListener.class)
public class Ingredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "irritation_level")
    private Integer irritationLevel; // 0-5

    @Column(name = "comedogenic_level")
    private Integer comedogenicLevel; // 0-5

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
}