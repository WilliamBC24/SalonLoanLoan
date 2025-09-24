package service.sllbackend.auth.entity;

import jakarta.persistence.*;
import lombok.*;
import service.sllbackend.auth.entity.composite_key.CartId;

@Entity
@Table(name = "cart")
@NoArgsConstructor
@AllArgsConstructor
@Data
@Builder
@IdClass(CartId.class)
public class Cart {
    @Id
    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private UserAccount user;

    @Id
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    private Integer amount;
}
