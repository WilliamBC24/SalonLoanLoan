package service.sllbackend.auth.entity.composite_key;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartId implements Serializable {
    private Long user;
    private Long product;
}