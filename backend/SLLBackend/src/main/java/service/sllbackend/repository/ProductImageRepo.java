package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import service.sllbackend.entity.Product;
import service.sllbackend.entity.ProductImage;

import java.util.List;

@Repository
public interface ProductImageRepo extends JpaRepository<ProductImage, Integer> {
    List<ProductImage> findByProduct(Product product);
    List<ProductImage> findByProductId(Integer productId);
    void deleteByProduct(Product product);
}
