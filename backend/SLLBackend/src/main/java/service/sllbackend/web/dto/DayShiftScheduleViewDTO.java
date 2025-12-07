package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.entity.Appointment;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DayShiftScheduleViewDTO {
    private LocalDate date;

    private List<ShiftBlockViewDTO> shifts;         // AM / PM blocks
    private List<AppointmentDTO> appointments;
}
