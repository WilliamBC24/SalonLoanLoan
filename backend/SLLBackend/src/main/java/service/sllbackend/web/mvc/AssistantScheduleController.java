package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import service.sllbackend.entity.Staff;
import service.sllbackend.entity.StaffAccount;
import service.sllbackend.repository.StaffAccountRepo;
import service.sllbackend.service.ShiftScheduleService;
import service.sllbackend.service.StaffService;
import service.sllbackend.web.dto.CalendarDayViewDTO;

import java.time.YearMonth;
import java.time.format.TextStyle;
import java.util.List;
import java.util.Locale;

@Controller
@RequestMapping("/assistant/schedule")
@RequiredArgsConstructor
public class AssistantScheduleController {

    private final ShiftScheduleService shiftScheduleService;
    private final StaffService staffService;
    private final StaffAccountRepo staffAccountRepo;

    @GetMapping
    public String getMyMonthSchedule(
            @RequestParam(value = "monthOffset", defaultValue = "0") int monthOffset,
            Authentication authentication,
            Model model
    ) {
        // Determine which month to show
        YearMonth targetMonth = YearMonth.now().plusMonths(monthOffset);
        shiftScheduleService.ensureShiftInstancesForMonth(targetMonth);

        // Find the currently logged-in staff
        String username = authentication.getName();
        StaffAccount staffAccount = staffAccountRepo.findByUsername(username).get();
        Staff staff = staffAccount.getStaff();
        Integer staffId = staff.getId();

        // Build calendar only for this staff member's assigned shifts
        List<CalendarDayViewDTO> calendarDays =
                shiftScheduleService.buildMonthScheduleForStaff(targetMonth, staffId);
        // ^ you implement this method in your service (filter by staffId)

        // Label like "March 2025"
        String monthLabel = targetMonth.getMonth()
                .getDisplayName(TextStyle.FULL, Locale.getDefault())
                + " " + targetMonth.getYear();

        model.addAttribute("calendarDays", calendarDays);
        model.addAttribute("currentMonthLabel", monthLabel);
        model.addAttribute("currentMonthYear", targetMonth);
        model.addAttribute("monthOffset", monthOffset);
        model.addAttribute("staffName", staff.getName());

        return "staff-my-schedule";
    }
}
