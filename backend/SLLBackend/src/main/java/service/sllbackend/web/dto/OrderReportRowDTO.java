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
public class OrderReportRowDTO {

    private Integer invoiceId;
    private String orderCode;
    private LocalDateTime createdAt;

    private String customerName;
    private String fulfillmentType; // DELIVERY / PICKUP string if you want
    private String statusLabel;     // e.g. "DELIVERED"

    private int totalPrice;
    private int discount;
    private int netPrice;
}
