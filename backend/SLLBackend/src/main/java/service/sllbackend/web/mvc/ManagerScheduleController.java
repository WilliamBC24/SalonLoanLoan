package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.sllbackend.service.ShiftScheduleService;
import service.sllbackend.web.dto.CalendarDayViewDTO;
import service.sllbackend.web.dto.DayShiftScheduleViewDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/manager/schedule")
@RequiredArgsConstructor
public class ManagerScheduleController {
    private final ShiftScheduleService shiftScheduleService;

    @GetMapping
    public String getMonthSchedule(
            @RequestParam(value = "monthOffset", defaultValue = "0") int monthOffset,
            Model model
    ) {
        // Which month are we showing (current month + offset)?
        YearMonth targetMonth = YearMonth.now().plusMonths(monthOffset);
        shiftScheduleService.ensureShiftInstancesForMonth(targetMonth);

        // Ask service layer to build the “calendar cells” for this month
        List<CalendarDayViewDTO> calendarDays =
                shiftScheduleService.buildMonthSchedule(targetMonth);

        // Label like "March 2025"
        String monthLabel = targetMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.getDefault()) + " " + targetMonth.getYear();

        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("currentMonthLabel", monthLabel);
        model.addAttribute("currentMonthYear", targetMonth);
        model.addAttribute("monthOffset", monthOffset);

        // matches: manager-overall-schedule.html
        return "manager-overall-schedule";
    }

    /**
     * Day detail: shifts + appointments for one date
     * URL: /manager/schedule/day?date=2025-03-15
     */
    @GetMapping("/day")
    public String getDaySchedule(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            Model model
    ) {
        // Let service collect ShiftInstance + ShiftAssignment + Appointment for that day
        DayShiftScheduleViewDTO daySchedule =
                shiftScheduleService.buildDaySchedule(date);

        model.addAttribute("day", daySchedule);
        model.addAttribute("date", date);

        // you will create manager-schedule-day.html later
        return "manager-detailed-schedule";
    }
}
