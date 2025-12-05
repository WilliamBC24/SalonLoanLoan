package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CalendarDayViewDTO {
    private LocalDate date;
    private boolean inCurrentMonth;
    private boolean today;
    private int totalAppointments;

    private boolean hasMorningShift;
    private int amAssignedStaffCount;
    private int amRecommendedStaffCount;
    private String amLoadLevel;   // "low", "medium", "high"

    private boolean hasAfternoonShift;
    private int pmAssignedStaffCount;
    private int pmRecommendedStaffCount;
    private String pmLoadLevel;   // "low", "medium", "high"
}