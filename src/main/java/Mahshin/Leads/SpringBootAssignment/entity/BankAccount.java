package Mahshin.Leads.SpringBootAssignment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "bank_accounts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BankAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private AccountType accountType;

    @NotBlank(message = "Account name is required")
    @Column(nullable = false)
    private String accountName;

    @NotBlank(message = "Account number is required")
    @Column(nullable = false, unique = true)
    private String accountNumber;

    @Min(value = 0, message = "Balance cannot be negative")
    @Column(nullable = false)
    private Double currentBalance = 0.0;

    @NotBlank(message = "Bank name is required")
    @Column(nullable = false)
    private String bankName;

    @NotBlank(message = "Branch name is required")
    @Column(nullable = false)
    private String branchName;

    @OneToOne(mappedBy = "bankAccount")
    private Employee employee;
}

enum AccountType {
    SAVINGS,
    CURRENT
}