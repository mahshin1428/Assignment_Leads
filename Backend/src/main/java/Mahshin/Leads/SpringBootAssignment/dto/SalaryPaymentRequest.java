package Mahshin.Leads.SpringBootAssignment.dto;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryPaymentRequest {
    private Double additionalFunds;
    private List<Long> employeeIds;
}
