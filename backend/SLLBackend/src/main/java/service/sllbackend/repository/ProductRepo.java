package service.sllbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import service.sllbackend.entity.Product;

public interface ProductRepo extends JpaRepository<Product, Integer> {

	@Query("select p from Product p")
	List<Product> findAllProducts();

	@Query("select p from Product p where p.id = :id")
	Optional<Product> findProductById(@Param("id") Integer id);

	@Query("select p from Product p " +
		   "where (:name is null or lower(p.productName) like lower(concat('%', :name, '%'))) " +
		   "and (:activeStatus is null or p.activeStatus = :activeStatus)")
	List<Product> searchProducts(@Param("name") String name, 
								  @Param("activeStatus") Boolean activeStatus);

}
