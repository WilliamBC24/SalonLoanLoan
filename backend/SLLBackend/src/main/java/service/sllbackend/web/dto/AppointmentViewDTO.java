package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentViewDTO {

    private Integer id;                       // appointment id
    private LocalDateTime startTime;          // from appointment_details.scheduled_start

    private String customerName;              // from appointment_details.user.fullName
    private String customerPhoneNumber;       // from appointment_details.user.phoneNumber

    private String assignedStaffName;         // optional if Appointment stores this
    private String internalNote;              // optional admin/staff note

    // getters and setters
}
