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
public class ShiftBlockViewDTO {
    private Integer shiftInstanceId;
    private String shiftName;         // "AM" or "PM"
    private String timeRange;         // "07:30 - 12:00"

    private List<StaffAssignmentViewDTO> assignedStaff;
    private List<StaffSuggestionViewDTO> suggestedStaff;
}
