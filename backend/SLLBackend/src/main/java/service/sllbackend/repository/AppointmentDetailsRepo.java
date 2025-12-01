package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import service.sllbackend.entity.AppointmentDetails;
import service.sllbackend.entity.UserAccount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface AppointmentDetailsRepo extends JpaRepository<AppointmentDetails, Long> {
    Optional<AppointmentDetails> findByAppointmentId(Long appointmentId);
    long countByUser(UserAccount user);
    @Query("""
    select ad.appointment.responsibleStaffId.id, count(ad.id)
    from AppointmentDetails ad
    where ad.appointment.responsibleStaffId.id in :staffIds
      and ad.scheduledStart < :requestedEnd
      and ad.scheduledEnd   > :requestedStart
      and ad.appointment.status = service.sllbackend.enumerator.AppointmentStatus.REGISTERED
    group by ad.appointment.responsibleStaffId.id
    """)
    List<Object[]> findStaffBusyCountsInInterval(@Param("staffIds") List<Integer> staffIds,
                                                 @Param("requestedStart") LocalDateTime requestedStart,
                                                 @Param("requestedEnd") LocalDateTime requestedEnd);

}
