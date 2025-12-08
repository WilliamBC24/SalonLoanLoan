package service.sllbackend.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentServiceLineDTO {

    private String serviceName;         // requested_service.service.name
    private String staffName;           // requested_service.responsible_staff.name

    private Integer durationMinutes;    // optional, default null

    private int unitPrice;       // requested_service.price_at_booking

}
