package service.sllbackend.web.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
public class JobPostingDTO {
    @NotBlank
    private String jobPostingName;

    @NotBlank
    private String jobPostingDescription;

    @NotNull
    @Min(value = 1)
    private Integer maxApplication;

    @NotNull
    private LocalDate effectiveFrom;

    @NotNull
    private LocalDate effectiveTo;

    @NotNull
    private JobPostingStatus status;

    //TODO: make this an annotation
    @AssertTrue(message = "effectiveTo must be later than or equal to effectiveFrom")
    public boolean isEffectiveRangeValid() {
        return effectiveTo == null || !effectiveTo.isBefore(effectiveFrom);
    }
}
