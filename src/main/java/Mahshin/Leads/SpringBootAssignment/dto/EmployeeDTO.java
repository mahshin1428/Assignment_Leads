package Mahshin.Leads.SpringBootAssignment.dto;


import jakarta.validation.constraints.*;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployeeDTO {

    @Getter
    @NotBlank(message = "Employee ID is required")
    @Pattern(regexp = "\\d{4}", message = "Employee ID must be exactly 4 digits")
    private String employeeId;

    @Getter
    @NotBlank(message = "Name is required")
    private String name;

    @Getter
    @NotNull(message = "Grade is required")
    @Min(value = 1, message = "Grade must be between 1 and 6")
    @Max(value = 6, message = "Grade must be between 1 and 6")
    private Integer grade;

    @Getter
    @NotBlank(message = "Address is required")
    private String address;

    @Getter
    @NotBlank(message = "Mobile number is required")
    @Pattern(regexp = "^[0-9]{10,15}$", message = "Mobile number must be 10-15 digits")
    private String mobileNumber;

    @NotNull(message = "Bank account is required")
    private BankAccountDTO bankAccount;

}