package Mahshin.Leads.SpringBootAssignment.service;


import Mahshin.Leads.SpringBootAssignment.dto.EmployeeDTO;
import Mahshin.Leads.SpringBootAssignment.entity.BankAccount;
import Mahshin.Leads.SpringBootAssignment.entity.Employee;
import Mahshin.Leads.SpringBootAssignment.exception.ResourceNotFoundException;
import Mahshin.Leads.SpringBootAssignment.exception.ValidationException;
import Mahshin.Leads.SpringBootAssignment.repository.BankAccountRepository;
import Mahshin.Leads.SpringBootAssignment.repository.EmployeeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final BankAccountRepository bankAccountRepository;

    @Transactional
    public Employee createEmployee(EmployeeDTO dto) {
        // Validate employee ID
        if (employeeRepository.existsByEmployeeId(dto.getEmployeeId())) {
            throw new ValidationException("Employee ID already exists");
        }

        // Validate grade constraints
        validateGradeConstraints(dto.getGrade());

        // Validate account number
        if (bankAccountRepository.existsByAccountNumber(dto.getBankAccount().getAccountNumber())) {
            throw new ValidationException("Bank account number already exists");
        }

        BankAccount bankAccount = BankAccount.builder()
                .accountType(dto.getBankAccount().getAccountType())
                .accountName(dto.getBankAccount().getAccountName())
                .accountNumber(dto.getBankAccount().getAccountNumber())
                .currentBalance(dto.getBankAccount().getCurrentBalance())
                .bankName(dto.getBankAccount().getBankName())
                .branchName(dto.getBankAccount().getBranchName())
                .build();

        Employee employee = Employee.builder()
                .employeeId(dto.getEmployeeId())
                .name(dto.getName())
                .grade(dto.getGrade())
                .address(dto.getAddress())
                .mobileNumber(dto.getMobileNumber())
                .bankAccount(bankAccount)
                .build();

        bankAccount.setEmployee(employee);

        return employeeRepository.save(employee);
    }

    public List<Employee> getAllEmployees() {
        return employeeRepository.findAllByOrderByGradeAsc();
    }

    public Employee getEmployeeById(String id) {
        return employeeRepository.findByEmployeeId(id)
                .orElseThrow(() -> new ResourceNotFoundException("Employee not found with ID: " + id));
    }

    @Transactional
    public Employee updateEmployee(String id, EmployeeDTO dto) {
        Employee employee = getEmployeeById(id);

        // If grade is being changed, validate constraints
        if (!employee.getGrade().equals(dto.getGrade())) {
            validateGradeConstraints(dto.getGrade());
        }

        employee.setName(dto.getName());
        employee.setGrade(dto.getGrade());
        employee.setAddress(dto.getAddress());
        employee.setMobileNumber(dto.getMobileNumber());

        BankAccount bankAccount = employee.getBankAccount();
        bankAccount.setAccountType(dto.getBankAccount().getAccountType());
        bankAccount.setAccountName(dto.getBankAccount().getAccountName());
        bankAccount.setCurrentBalance(dto.getBankAccount().getCurrentBalance());
        bankAccount.setBankName(dto.getBankAccount().getBankName());
        bankAccount.setBranchName(dto.getBankAccount().getBranchName());

        return employeeRepository.save(employee);
    }

    @Transactional
    public void deleteEmployee(String id) {
        Employee employee = getEmployeeById(id);
        employeeRepository.delete(employee);
    }

    private void validateGradeConstraints(Integer grade) {
        long count = employeeRepository.countByGrade(grade);

        if (grade >= 1 && grade <= 2 && count >= 1) {
            throw new ValidationException("Grade " + grade + " can only have 1 employee");
        } else if (grade >= 3 && grade <= 6 && count >= 2) {
            throw new ValidationException("Grade " + grade + " can only have 2 employees");
        }
    }
}