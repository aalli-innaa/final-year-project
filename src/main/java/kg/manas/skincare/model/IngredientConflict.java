package kg.manas.skincare.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ingredient_conflicts",
        uniqueConstraints = @UniqueConstraint(columnNames = {"ingredient_id_1", "ingredient_id_2"}))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IngredientConflict {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Первый ингредиент в паре конфликта
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id_1", nullable = false)
    private Ingredient ingredient1;

    // Второй ингредиент в паре конфликта
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ingredient_id_2", nullable = false)
    private Ingredient ingredient2;

    // Уровень опасности (например: LOW, MEDIUM, HIGH)
    @Column(length = 20)
    private String severity;

    // Описание проблемы (например: "Вызывает сильное раздражение при наслоении")
    @Column(columnDefinition = "TEXT")
    private String reason;
}