package Mahshin.Leads.SpringBootAssignment.dto;


import Mahshin.Leads.SpringBootAssignment.entity.AccountType;
import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccountDTO {

    @NotNull(message = "Account type is required")
    private AccountType accountType;

    @Getter
    @NotBlank(message = "Account name is required")
    private String accountName;

    @Getter
    @NotBlank(message = "Account number is required")
    private String accountNumber;

    @NotNull(message = "Current balance is required")
    @Min(value = 0, message = "Balance cannot be negative")
    private Double currentBalance;

    @NotBlank(message = "Bank name is required")
    private String bankName;

    @NotBlank(message = "Branch name is required")
    private String branchName;


}