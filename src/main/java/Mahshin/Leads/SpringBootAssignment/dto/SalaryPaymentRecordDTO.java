// File: src/main/java/Mahshin/Leads/SpringBootAssignment/dto/SalaryPaymentRecordDTO.java
package Mahshin.Leads.SpringBootAssignment.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryPaymentRecordDTO {
    private Long employeeId;
    private String employeeName;
    private Double grossSalary;
    private Double netPaid;
    private LocalDateTime paidAt;
    private String paymentReference;
}
