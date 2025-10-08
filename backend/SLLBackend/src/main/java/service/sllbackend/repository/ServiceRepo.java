package service.sllbackend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import service.sllbackend.entity.Service;
import service.sllbackend.enumerator.ServiceType;

public interface ServiceRepo extends JpaRepository<Service, Integer> {

	@Query("select s from Service s")
	List<Service> findAllServices(Pageable pageable);

	@Query("select s from Service s left join fetch s.serviceCategory")
	List<Service> findAllWithCategory();

	@Query("select s from Service s left join fetch s.serviceCategory where s.id = :id")
	Optional<Service> findByIdWithCategory(@Param("id") Integer id);

	@Query("select s from Service s left join fetch s.serviceCategory " +
			"where (:types is null or s.serviceType in :types) " +
			"and (:categoryIds is null or s.serviceCategory.id in :categoryIds) " +
			"and (:name is null or lower(s.serviceName) like lower(concat('%', :name, '%')))")
	List<Service> searchServices(@Param("types") List<ServiceType> types,
			@Param("categoryIds") List<Integer> categoryIds,
			@Param("name") String name);

}