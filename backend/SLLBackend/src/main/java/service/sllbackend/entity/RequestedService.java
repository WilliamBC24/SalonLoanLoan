package service.sllbackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Check;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "requested_service")
@Check(constraints = "price_at_booking >= 0")
public class RequestedService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;

    @Column(name = "price_at_booking", nullable = false, columnDefinition = "INT DEFAULT 0")
    private Integer priceAtBooking = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "responsible_staff_id")
    private Staff responsibleStaff;
}
