package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffSuggestionViewDTO {
    private Integer staffId;
    private String staffName;
    private int preferenceCount;
    private boolean currentlyAssigned;
}
