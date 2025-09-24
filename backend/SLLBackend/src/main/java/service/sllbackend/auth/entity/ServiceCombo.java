package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "service_combo", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"combo_id", "service_id"})
})
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class ServiceCombo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "combo_id", nullable = false)
    private Service combo;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private Service service;
}
