package Mahshin.Leads.SpringBootAssignment.entity;
import Mahshin.Leads.SpringBootAssignment.entity.AccountType;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "employe")
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @Column(name = "EMPLOYEE_ID", length = 4, unique = true)
    private String employeeId;

    @NotBlank(message = "Name is required")
    @Column(name = "NAME", nullable = false)
    private String name;

    @Min(value = 1, message = "Grade must be between 1 and 6")
    @Max(value = 6, message = "Grade must be between 1 and 6")
    @Column(name = "GRADE", nullable = false)
    private Integer grade;

    @NotBlank(message = "Address is required")
    @Column(name = "ADDRESS", nullable = false)
    private String address;

    @NotBlank(message = "Mobile number is required")
    @Column(name = "MOBILE_NUMBER", nullable = false)
    private String mobileNumber;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "ID", referencedColumnName = "ID")
    @JsonManagedReference
    private BankAccount bankAccount;

    @Transient
    private Double basicSalary;

    @Transient
    private Double houseRent;

    @Transient
    private Double medicalAllowance;

    @Transient
    private Double totalSalary;
}
