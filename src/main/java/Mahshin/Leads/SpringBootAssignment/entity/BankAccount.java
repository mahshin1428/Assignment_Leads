package Mahshin.Leads.SpringBootAssignment.entity;
import Mahshin.Leads.SpringBootAssignment.entity.AccountType;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "BANK_ACCOUNTS")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "ACCOUNT_TYPE", nullable = false)
    private AccountType accountType;

    @NotBlank(message = "Account name is required")
    @Column(name = "ACCOUNT_NAME", nullable = false)
    private String accountName;

    @NotBlank(message = "Account number is required")
    @Column(name = "ACCOUNT_NUMBER", nullable = false, unique = true)
    private String accountNumber;

    @Min(value = 0, message = "Balance cannot be negative")
    @Column(name = "CURRENT_BALANCE", nullable = false)
    private Double currentBalance = 0.0;

    @NotBlank(message = "Bank name is required")
    @Column(name = "BANK_NAME", nullable = false)
    private String bankName;

    @NotBlank(message = "Branch name is required")
    @Column(name = "BRANCH_NAME", nullable = false)
    private String branchName;

    @OneToOne(mappedBy = "bankAccount")
    @JsonBackReference
    private Employee employee;
}
