package kg.manas.skincare.model;

import jakarta.persistence.*;
import kg.manas.skincare.enums.SkinConcern;
import lombok.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "ingredient_benefits")
public class IngredientBenefit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id", nullable = false)
    private Ingredient ingredient;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SkinConcern benefit;

    @Column(name = "efficiency_score")
    private Double efficiencyScore; // Например, 0.8
}