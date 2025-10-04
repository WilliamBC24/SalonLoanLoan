package service.sllbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import service.sllbackend.entity.Service;

public interface ServiceRepo extends JpaRepository<Service, Integer> {

	@Query("select s from Service s left join fetch s.serviceCategory")
	List<Service> findAllWithCategory();

}
