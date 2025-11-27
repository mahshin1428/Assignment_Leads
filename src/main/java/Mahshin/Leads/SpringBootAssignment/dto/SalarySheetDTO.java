package Mahshin.Leads.SpringBootAssignment.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalarySheetDTO {
    private String employeeId;
    private String employeeName;
    private Integer grade;
    private Double basicSalary;
    private Double houseRent;
    private Double medicalAllowance;
    private Double totalSalary;
}