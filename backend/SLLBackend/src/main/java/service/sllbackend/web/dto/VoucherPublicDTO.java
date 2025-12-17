package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.DiscountType;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VoucherPublicDTO {
    private Integer id;
    private String voucherName;
    private String voucherDescription;
    private String voucherCode;
    private DiscountType discountType;
    private Integer discountAmount;
    private LocalDateTime effectiveFrom;
    private LocalDateTime effectiveTo;
}
