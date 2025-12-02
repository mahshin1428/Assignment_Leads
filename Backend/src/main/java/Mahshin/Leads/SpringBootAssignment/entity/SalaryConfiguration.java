package Mahshin.Leads.SpringBootAssignment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity
@Table(name = "salary_configuration")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalaryConfiguration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Double lowestGradeBasicSalary;

    @Column(nullable = false)
    private Double gradeIncrement = 5000.0;

    @Column(nullable = false)
    private Double houseRentPercentage = 20.0;

    @Column(nullable = false)
    private Double medicalAllowancePercentage = 15.0;
}