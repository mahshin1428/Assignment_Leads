package Mahshin.Leads.SpringBootAssignment.dto;


import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryConfigurationDTO {
    private Double lowestGradeBasicSalary;
    private Double gradeIncrement;
    private Double houseRentPercentage;
    private Double medicalAllowancePercentage;

}