package Mahshin.Leads.SpringBootAssignment.dto;


import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyAccountDTO {
    private Double balance;
    private String accountName;
    private String accountNumber;
    private String bankName;
    private String branchName;
}