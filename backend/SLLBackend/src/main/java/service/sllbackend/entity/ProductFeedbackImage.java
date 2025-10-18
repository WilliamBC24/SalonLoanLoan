package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product_feedback_image")
public class ProductFeedbackImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_feedback_id", nullable = false)
    private ProductFeedback productFeedback;

    @Column(name = "image_path", nullable = false, columnDefinition = "TEXT")
    private String imagePath;
}
