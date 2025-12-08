package service.sllbackend.web.mvc;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import service.sllbackend.service.ShiftScheduleService;

import java.time.LocalDate;

@Controller
@RequestMapping("/manager/shift")
@RequiredArgsConstructor
public class ManagerShiftController {
    private final ShiftScheduleService shiftScheduleService;

    @PostMapping("/assign-staff")
    public String assignStaffToShift(
            @RequestParam Integer shiftInstanceId,
            @RequestParam Integer staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        System.out.println("Assign staff " + staffId + " to shift " + shiftInstanceId);

        shiftScheduleService.assignStaffToShift(shiftInstanceId, staffId);

        return "redirect:/manager/schedule/day?date=" + date;
    }

    @PostMapping("/remove-staff")
    public String removeStaffFromShift(
            @RequestParam Integer shiftInstanceId,
            @RequestParam Integer staffId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        System.out.println("Remove staff " + staffId + " from shift " + shiftInstanceId);

        shiftScheduleService.removeStaffFromShift(shiftInstanceId, staffId);

        return "redirect:/manager/schedule/day?date=" + date;
    }
}
