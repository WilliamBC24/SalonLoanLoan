package service.sllbackend.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import service.sllbackend.entity.ServiceCombo;

public interface ServiceComboRepo extends JpaRepository<ServiceCombo, Integer> {

	@Query("select sc from ServiceCombo sc " +
		   "left join fetch sc.combo " +
		   "left join fetch sc.service " +
		   "left join fetch sc.service.serviceCategory " +
		   "where sc.combo.id = :comboId")
	List<ServiceCombo> findByComboIdWithDetails(@Param("comboId") Integer comboId);

	List<ServiceCombo> findByComboId(Integer comboId);
}
