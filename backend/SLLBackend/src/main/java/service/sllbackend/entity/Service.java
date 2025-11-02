package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;
import service.sllbackend.enumerator.ServiceType;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "service")
@Check(constraints = "service_price > 0 AND duration_minutes > 0")
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "service_name", nullable = false, columnDefinition = "TEXT")
    private String serviceName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_category_id", nullable = false)
    private ServiceCategory serviceCategory;

    @Enumerated(EnumType.STRING)
    @Column(name = "service_type", nullable = false, columnDefinition = "TEXT DEFAULT 'SINGLE'")
    @Builder.Default
    private ServiceType serviceType = ServiceType.SINGLE;

    @Column(name = "service_price", nullable = false)
    private Integer servicePrice;

    @Column(name = "duration_minutes", nullable = false)
    private Short durationMinutes;

    @Column(name = "service_description", columnDefinition = "TEXT")
    private String serviceDescription;

    @Column(name = "active_status", nullable = false, columnDefinition = "BOOLEAN DEFAULT FALSE")
    @Builder.Default
    private Boolean activeStatus = false;
}
