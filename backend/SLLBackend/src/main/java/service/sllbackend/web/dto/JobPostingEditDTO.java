package service.sllbackend.web.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import service.sllbackend.enumerator.JobPostingStatus;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class JobPostingEditDTO {
    private String jobPostingName;

    private String jobPostingDescription;

    @Min(value = 1)
    private Integer maxApplication;

    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    private JobPostingStatus status;

    @AssertTrue(message = "effectiveTo must be later than or equal to effectiveFrom")
    public boolean isEffectiveRangeValid() {
        return effectiveTo == null || !effectiveTo.isBefore(effectiveFrom);
    }
}
