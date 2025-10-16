package service.sllbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import service.sllbackend.entity.Supplier;

public interface SupplierRepo extends JpaRepository<Supplier, Integer> {

    @Query("select s from Supplier s left join fetch s.supplierCategory")
    List<Supplier> findAllWithCategory();

    @Query("select s from Supplier s left join fetch s.supplierCategory where s.id = :id")
    Optional<Supplier> findByIdWithCategory(@Param("id") Integer id);

    @Query("select s from Supplier s left join fetch s.supplierCategory " +
           "where (:categoryIds is null or s.supplierCategory.id in :categoryIds) " +
           "and (:name is null or lower(s.supplierName) like lower(concat('%', :name, '%')))")
    List<Supplier> searchSuppliers(@Param("categoryIds") List<Integer> categoryIds,
                                    @Param("name") String name);
}
