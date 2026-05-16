package kg.manas.skincare.model;

import jakarta.persistence.*;
import kg.manas.skincare.enums.AcneSeverity;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "skin_analyses")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class SkinAnalysis {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long analysisId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "photo_id", nullable = false)
    private UserPhoto userPhoto;

    @Enumerated(EnumType.STRING)
    @Column(name = "acne_severity", nullable = false) // Указываем имя из ТВОЕЙ миграции
    private AcneSeverity primaryConcern; // Имя поля в Java остается СТАРЫМ

    private Double confidence;

    @Column(columnDefinition = "TEXT")
    private String boxes; // Координаты в формате JSON

    private Integer acneCount;
    private Integer imageWidth;
    private Integer imageHeight;
    //

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;
}