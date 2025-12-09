package service.sllbackend.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SatisfactionSummaryDTO {

    /**
     * e.g. "Appointments", "Product Orders"
     */
    private String segmentLabel;

    /**
     * Average rating (e.g. 4.3 out of 5).
     */
    private Double averageRating;

    /**
     * How many responses in this segment.
     */
    private Long responseCount;
}

