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
public class AppointmentReportRowDTO {

    private Integer invoiceId;
    private String appointmentCode;
    private LocalDateTime scheduledAt;

    private String customerName;
    private String staffName;

    private int totalPrice;   // original price
    private int discount;     // discount amount if you have it (else keep 0)
    private int netPrice;     // totalPrice - discount
}
