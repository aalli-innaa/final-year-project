package kg.manas.skincare.model;

import jakarta.persistence.*;
import kg.manas.skincare.enums.SkinType;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String brand;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "usage_instructions", columnDefinition = "TEXT")
    private String usageInstructions;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "target_gender")
    @Builder.Default
    private String targetGender = "UNISEX";

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // СВЯЗЬ С ИНГРЕДИЕНТАМИ (СОСТАВ)
    @ManyToMany
    @JoinTable(
            name = "product_ingredients",
            joinColumns = @JoinColumn(name = "product_id"),
            inverseJoinColumns = @JoinColumn(name = "ingredient_id")
    )
    @Builder.Default
    private Set<Ingredient> ingredients = new HashSet<>();

    // СВЯЗЬ С ТИПАМИ КОЖИ
    @ElementCollection(targetClass = SkinType.class)
    @CollectionTable(name = "product_skin_types", joinColumns = @JoinColumn(name = "product_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "skin_type")
    @Builder.Default
    private Set<SkinType> suitableSkinTypes = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // В классе Product.java добавь это поле:
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL)
    private Image image;
}