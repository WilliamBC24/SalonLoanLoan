package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.entity.Product;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductWithImageDto {
    private Product product;
    private String imagePath;
}

