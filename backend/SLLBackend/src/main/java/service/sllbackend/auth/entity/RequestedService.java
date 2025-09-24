package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.composite_key.RequestedServiceId;

@Entity
@Table(name = "requested_service")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@IdClass(RequestedServiceId.class)
public class RequestedService {
    @Id
    @ManyToOne
    @JoinColumn(name = "appointment_id", nullable = false)
    private Appointment appointment;

    @Id
    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
}
