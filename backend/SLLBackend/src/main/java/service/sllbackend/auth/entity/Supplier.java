package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "supplier")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
public class Supplier {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    private String email;

    private String note;
}
