package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RequestedServiceEditDTO {
    private Integer id;                  // requestedServices[i].id (hidden)
    private int serviceId;           // requestedServices[i].serviceId (hidden)
    private int priceAtBooking;// requestedServices[i].priceAtBooking (hidden)
    private int responsibleStaffId;  // requestedServices[i].responsibleStaffId (select)
}