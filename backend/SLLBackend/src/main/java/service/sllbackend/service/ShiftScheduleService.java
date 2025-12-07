package service.sllbackend.service;

import service.sllbackend.web.dto.CalendarDayViewDTO;
import service.sllbackend.web.dto.DayShiftScheduleViewDTO;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

public interface ShiftScheduleService {
    List<CalendarDayViewDTO> buildMonthSchedule(YearMonth month);
    DayShiftScheduleViewDTO buildDaySchedule(LocalDate date);
    void ensureShiftInstancesForMonth(YearMonth month);
}