package service.sllbackend.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InvoiceFormDTO {

    private Integer appointmentId;

    private String paymentMethod;         // "CASH" or "BANK_TRANSFER"
    private Integer satisfactionRating;   // 1–5
    private String customerNotes;

    // computed again in POST (we do NOT trust client):
    private int finalTotal;        // optional (not required in form)

    // getters and setters
}
