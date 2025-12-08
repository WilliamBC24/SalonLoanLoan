package service.sllbackend.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppointmentInvoiceViewDTO {

    private Integer id;                  // ALWAYS null on GET preview
    private Integer appointmentId;       // appointment id reference

    private int subtotal;         // sum of all line totals
    private int discountAmount;   // default 0 unless you apply discounts
    private int totalAmount;      // subtotal - discount

    // getters and setters
}
