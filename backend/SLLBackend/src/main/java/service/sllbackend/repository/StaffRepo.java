package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.Staff;

import java.util.List;


public interface StaffRepo extends JpaRepository<Staff, Integer> {
    Boolean existsByNameAndIdNot(String username, Long staffId);
    @Query("""
    select s from Staff s
    where (s.name = :name or s.email = :email or s.socialSecurityNum = :ssn)
    and s.id <> :currentUserId
    """)
    List<Staff> findConflicts(
            @Param("name") String name,
            @Param("email") String email,
            @Param("ssn") String ssn,
            @Param("currentUserId") Long currentUserId);

}
