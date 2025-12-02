package Mahshin.Leads.SpringBootAssignment.dto;


import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryPaymentResponse {
    private Boolean success;
    private Double totalSalaryPaid;
    private Double remainingCompanyBalance;
    private List<SalarySheetDTO> salarySheet;
}