package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.ShiftInstance;
import service.sllbackend.entity.ShiftTemplate;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

public interface ShiftInstanceRepo extends JpaRepository<ShiftInstance, Long> {
    List<ShiftInstance> findByShiftDateBetween(LocalDate start, LocalDate end);

    List<ShiftInstance> findByShiftDate(LocalDate date);

    boolean existsByShiftDateAndShiftTemplate(LocalDate shiftDate, ShiftTemplate shiftTemplate);

    @Query("""
        SELECT si
        FROM ShiftInstance si
        JOIN FETCH si.shiftTemplate st
        WHERE si.shiftDate = :date
          AND :time >= st.shiftStart
          AND :time <  st.shiftEnd
    """)
    Optional<ShiftInstance> findByDateAndTime(
            @Param("date") LocalDate date,
            @Param("time") LocalTime time
    );
}
