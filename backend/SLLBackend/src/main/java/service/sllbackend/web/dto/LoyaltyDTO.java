package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.entity.LoyaltyLevel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LoyaltyDTO {
    private Integer point;
    private LoyaltyLevel level;
}
