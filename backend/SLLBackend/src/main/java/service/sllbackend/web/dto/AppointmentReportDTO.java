package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentReportDTO {

    private int totalRevenue;
    private int appointmentCount;

    /**
     * Detailed rows for table.
     */
    private List<AppointmentReportRowDTO> rows;
}
