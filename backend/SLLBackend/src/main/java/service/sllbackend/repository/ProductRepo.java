package service.sllbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import service.sllbackend.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {

	@Query("select p from Product p")
	List<Product> findAllProducts(Pageable pageable);

	@Query("select p from Product p where p.id = :id")
	Optional<Product> findProductById(@Param("id") Integer id);

	@Query(value = "SELECT * FROM product p WHERE (:pattern IS NULL OR LOWER(p.product_name) LIKE LOWER(:pattern)) AND (:activeStatus IS NULL OR p.active_status = :activeStatus)", nativeQuery = true)
	List<Product> searchProducts(@Param("pattern") String pattern, @Param("activeStatus") Boolean activeStatus);
}
