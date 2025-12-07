package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.ShiftInstance;
import service.sllbackend.entity.ShiftTemplate;

import java.time.LocalDate;
import java.util.List;

public interface ShiftInstanceRepo extends JpaRepository<ShiftInstance, Long> {
    List<ShiftInstance> findByShiftDateBetween(LocalDate start, LocalDate end);
    List<ShiftInstance> findByShiftDate(LocalDate date);
    boolean existsByShiftDateAndShiftTemplate(LocalDate shiftDate, ShiftTemplate shiftTemplate);

}
