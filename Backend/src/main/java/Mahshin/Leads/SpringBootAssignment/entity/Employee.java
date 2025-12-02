package Mahshin.Leads.SpringBootAssignment.entity;
import Mahshin.Leads.SpringBootAssignment.entity.AccountType;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@Entity
@Table(name = "employees")

@NoArgsConstructor //create an employee object by jackson
@AllArgsConstructor
@Builder
public class Employee {

    @Id
    @Column(length = 4, unique = true)
    @Pattern(regexp = "\\d{4}", message = "Employee ID must be exactly 4 digits")
    private String employeeId;

    @NotBlank(message = "Name is required")
    @Column(nullable = false)
    private String name;

    @Min(value = 1, message = "Grade must be between 1 and 6")
    @Max(value = 6, message = "Grade must be between 1 and 6")
    @Column(nullable = false)
    private Integer grade;

    @NotBlank(message = "Address is required")
    @Column(nullable = false)
    private String address;

    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10-15 digits")
    @Column(nullable = false)
    private String mobileNumber;

    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "id", referencedColumnName = "id")
    @JsonManagedReference // This tells Jackson: serialize this side normally
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