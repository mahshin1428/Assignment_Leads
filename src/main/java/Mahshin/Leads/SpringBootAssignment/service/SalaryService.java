// java
package Mahshin.Leads.SpringBootAssignment.service;

import Mahshin.Leads.SpringBootAssignment.dto.SalaryPaymentRecordDTO;
import Mahshin.Leads.SpringBootAssignment.dto.SalaryPaymentRequest;
import Mahshin.Leads.SpringBootAssignment.dto.SalaryPaymentResponse;
import Mahshin.Leads.SpringBootAssignment.dto.SalarySheetDTO;
import Mahshin.Leads.SpringBootAssignment.entity.CompanyAccount;
import Mahshin.Leads.SpringBootAssignment.entity.Employee;
import Mahshin.Leads.SpringBootAssignment.entity.SalaryConfiguration;
import Mahshin.Leads.SpringBootAssignment.exception.InsufficientBalanceException;
import Mahshin.Leads.SpringBootAssignment.exception.ResourceNotFoundException;
import Mahshin.Leads.SpringBootAssignment.repository.CompanyAccountRepository;
import Mahshin.Leads.SpringBootAssignment.repository.EmployeeRepository;
import Mahshin.Leads.SpringBootAssignment.repository.SalaryConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final EmployeeRepository employeeRepository;
    private final CompanyAccountRepository companyAccountRepository;
    private final SalaryConfigurationRepository salaryConfigRepository;

    // in-memory payment records (runtime only)
    private final List<SalaryPaymentRecordDTO> paymentRecords = Collections.synchronizedList(new ArrayList<>());

    // CALCULATE SALARY SHEET FOR ALL EMPLOYEES
    public List<SalarySheetDTO> calculateSalaries() {
        SalaryConfiguration config = salaryConfigRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Salary configuration not found"));

        List<Employee> employees = employeeRepository.findAllByOrderByGradeAsc();
        List<SalarySheetDTO> salarySheet = new ArrayList<>();

        for (Employee employee : employees) {
            double basicSalary = calculateBasicSalary(employee.getGrade(), config);
            double houseRent = basicSalary * config.getHouseRentPercentage() / 100;
            double medicalAllowance = basicSalary * config.getMedicalAllowancePercentage() / 100;
            double totalSalary = basicSalary + houseRent + medicalAllowance;

            employee.setBasicSalary(basicSalary);
            employee.setHouseRent(houseRent);
            employee.setMedicalAllowance(medicalAllowance);
            employee.setTotalSalary(totalSalary);

            salarySheet.add(
                    SalarySheetDTO.builder()
                            .employeeId(employee.getEmployeeId())
                            .employeeName(employee.getName())
                            .grade(employee.getGrade())
                            .basicSalary(basicSalary)
                            .houseRent(houseRent)
                            .medicalAllowance(medicalAllowance)
                            .totalSalary(totalSalary)
                            .build()
            );
        }

        return salarySheet;
    }

    // PAY SALARIES TO ALL EMPLOYEES
    @Transactional
    public SalaryPaymentResponse processSalaryPayment(SalaryPaymentRequest request) {
        CompanyAccount companyAccount = getCompanyAccount();

        // Add extra funds if provided
        addAdditionalFunds(companyAccount, request);

        // Calculate total required
        List<SalarySheetDTO> salarySheet = calculateSalaries();
        double totalRequired = salarySheet.stream()
                .mapToDouble(SalarySheetDTO::getTotalSalary)
                .sum();

        // Validate balance
        checkBalance(companyAccount, totalRequired);

        // Transfer money to each employee and record payment
        List<Employee> employees = employeeRepository.findAllByOrderByGradeAsc();
        for (int i = 0; i < employees.size(); i++) {
            Employee emp = employees.get(i);
            SalarySheetDTO sal = salarySheet.get(i);

            emp.getBankAccount().setCurrentBalance(
                    emp.getBankAccount().getCurrentBalance() + sal.getTotalSalary()
            );

            companyAccount.setBalance(companyAccount.getBalance() - sal.getTotalSalary());

            SalaryPaymentRecordDTO record = SalaryPaymentRecordDTO.builder()
                    .employeeId(parseEmployeeId(emp.getEmployeeId()))
                    .employeeName(emp.getName())
                    .grossSalary(sal.getTotalSalary())
                    .netPaid(sal.getTotalSalary())
                    .paidAt(LocalDateTime.now())
                    .paymentReference(UUID.randomUUID().toString())
                    .build();
            paymentRecords.add(record);
        }

        employeeRepository.saveAll(employees);
        companyAccountRepository.save(companyAccount);

        return SalaryPaymentResponse.builder()
                .success(true)
                .totalSalaryPaid(totalRequired)
                .remainingCompanyBalance(companyAccount.getBalance())
                .salarySheet(salarySheet)
                .build();
    }

    // PAY SALARY TO SELECTED EMPLOYEES ONLY
    @Transactional
    public SalaryPaymentResponse processSelectedPayments(SalaryPaymentRequest request) {

        if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
            throw new IllegalArgumentException("Employee list cannot be empty");
        }

        CompanyAccount companyAccount = getCompanyAccount();

        // Add extra funds if provided
        addAdditionalFunds(companyAccount, request);

        // Calculate salaries for all employees
        List<SalarySheetDTO> allSalaries = calculateSalaries();

        // Filter selected employees
        List<SalarySheetDTO> selectedSalaries = allSalaries.stream()
                .filter(s -> request.getEmployeeIds().contains(s.getEmployeeId()))
                .toList();

        double totalRequired = selectedSalaries.stream()
                .mapToDouble(SalarySheetDTO::getTotalSalary)
                .sum();

        checkBalance(companyAccount, totalRequired);

        // Pay selected employees and record payments
        for (SalarySheetDTO sal : selectedSalaries) {
            Employee emp = employeeRepository.findById(sal.getEmployeeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Employee not found"));

            emp.getBankAccount().setCurrentBalance(
                    emp.getBankAccount().getCurrentBalance() + sal.getTotalSalary()
            );

            companyAccount.setBalance(companyAccount.getBalance() - sal.getTotalSalary());

            SalaryPaymentRecordDTO record = SalaryPaymentRecordDTO.builder()
                    .employeeId(parseEmployeeId(emp.getEmployeeId()))
                    .employeeName(emp.getName())
                    .grossSalary(sal.getTotalSalary())
                    .netPaid(sal.getTotalSalary())
                    .paidAt(LocalDateTime.now())
                    .paymentReference(UUID.randomUUID().toString())
                    .build();
            paymentRecords.add(record);

            employeeRepository.save(emp);
        }

        companyAccountRepository.save(companyAccount);

        return SalaryPaymentResponse.builder()
                .success(true)
                .totalSalaryPaid(totalRequired)
                .remainingCompanyBalance(companyAccount.getBalance())
                .salarySheet(selectedSalaries)
                .build();
    }

    // Return copy of recorded payments
    public List<SalaryPaymentRecordDTO> getPaidSalaryReport() {
        synchronized (paymentRecords) {
            return new ArrayList<>(paymentRecords);
        }
    }

    // HELPER METHODS
    private CompanyAccount getCompanyAccount() {
        return companyAccountRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Company account not found"));
    }

    private void addAdditionalFunds(CompanyAccount account, SalaryPaymentRequest request) {
        if (request != null && request.getAdditionalFunds() != null && request.getAdditionalFunds() > 0) {
            account.setBalance(account.getBalance() + request.getAdditionalFunds());
            companyAccountRepository.save(account);
        }
    }

    private void checkBalance(CompanyAccount account, double required) {
        if (account.getBalance() < required) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Required: " + required +
                            ", Available: " + account.getBalance()
            );
        }
    }

    private double calculateBasicSalary(Integer grade, SalaryConfiguration config) {
        return config.getLowestGradeBasicSalary() +
                ((6 - grade) * config.getGradeIncrement());
    }

    // Safely convert various id representations to Long for the DTO
    private Long parseEmployeeId(Object id) {
        if (id == null) return null;
        if (id instanceof Long) return (Long) id;
        if (id instanceof Number) return ((Number) id).longValue();
        String s = id.toString();
        try {
            return Long.valueOf(s);
        } catch (NumberFormatException ex) {
            return null;
        }
    }
}
