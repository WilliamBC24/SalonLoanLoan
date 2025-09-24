package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.ServiceImageStateEnum;

@Entity
@Table(name = "service_image", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"service_id", "state"})
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ServiceImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceImageStateEnum state;

    @Column(name = "image_path")
    private String imagePath;
}
