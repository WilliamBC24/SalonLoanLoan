package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.enums.ServiceTypeEnum;

@Entity
@Table(name = "service")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Service {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "service_name", nullable = false)
    private String serviceName;

    @ManyToOne
    @JoinColumn(name = "service_category_id", nullable = false)
    private ServiceCategory serviceCategory;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceTypeEnum type = ServiceTypeEnum.SINGLE;

    private Double price;

    @Column(name = "duration_minutes", nullable = false)
    private Integer durationMinutes;

    private String description;
}
