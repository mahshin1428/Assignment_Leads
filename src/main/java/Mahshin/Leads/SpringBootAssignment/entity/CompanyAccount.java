package Mahshin.Leads.SpringBootAssignment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "company_account")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompanyAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(value = 0, message = "Balance cannot be negative")
    @Column(nullable = false)
    private Double balance;

    @Column(nullable = false)
    private String accountName;

    @Column(nullable = false)
    private String accountNumber;

    @Column(nullable = false)
    private String bankName;

    @Column(nullable = false)
    private String branchName;

    public void getClass(Double balance) {
    }
}