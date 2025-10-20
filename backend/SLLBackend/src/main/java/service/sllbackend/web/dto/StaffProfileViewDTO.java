package service.sllbackend.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StaffProfileViewDTO {
    private String username;
    private boolean active;
    private String name;
    private String email;
    private LocalDate dateHired;
    private String socialSecurityNum;
    private String staffStatus;
}
