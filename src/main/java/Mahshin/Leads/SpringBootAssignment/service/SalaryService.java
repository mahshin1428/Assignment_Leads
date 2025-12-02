package Mahshin.Leads.SpringBootAssignment.service;


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

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryService {

    private final EmployeeRepository employeeRepository;
    private final CompanyAccountRepository companyAccountRepository;
    private final SalaryConfigurationRepository salaryConfigRepository;

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

            SalarySheetDTO dto = SalarySheetDTO.builder()
                    .employeeId(employee.getEmployeeId())
                    .employeeName(employee.getName())
                    .grade(employee.getGrade())
                    .basicSalary(basicSalary)
                    .houseRent(houseRent)
                    .medicalAllowance(medicalAllowance)
                    .totalSalary(totalSalary)
                    .build();

            salarySheet.add(dto);
        }

        return salarySheet;
    }

    @Transactional
    public SalaryPaymentResponse processSalaryPayment(SalaryPaymentRequest request) {
        CompanyAccount companyAccount = companyAccountRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Company account not found"));

        if (request.getAdditionalFunds() != null && request.getAdditionalFunds() > 0) {
            companyAccount.setBalance(companyAccount.getBalance() + request.getAdditionalFunds());
            companyAccountRepository.save(companyAccount);
        }

        List<SalarySheetDTO> salarySheet = calculateSalaries();
        double totalSalaryRequired = salarySheet.stream()
                .mapToDouble(SalarySheetDTO::getTotalSalary)
                .sum();

        if (companyAccount.getBalance() < totalSalaryRequired) {
            throw new InsufficientBalanceException(
                    "Insufficient balance. Required: " + totalSalaryRequired +
                            ", Available: " + companyAccount.getBalance()
            );
        }

        List<Employee> employees = employeeRepository.findAllByOrderByGradeAsc();
        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            SalarySheetDTO salary = salarySheet.get(i);

            employee.getBankAccount().setCurrentBalance(
                    employee.getBankAccount().getCurrentBalance() + salary.getTotalSalary()
            );

            companyAccount.setBalance(companyAccount.getBalance() - salary.getTotalSalary());
        }

        employeeRepository.saveAll(employees);
        companyAccountRepository.save(companyAccount);

        return SalaryPaymentResponse.builder()
                .success(true)
                .totalSalaryPaid(totalSalaryRequired)
                .remainingCompanyBalance(companyAccount.getBalance())
                .salarySheet(salarySheet)
                .build();
    }

    private double calculateBasicSalary(Integer grade, SalaryConfiguration config) {
        double lowestSalary = config.getLowestGradeBasicSalary();
        double increment = config.getGradeIncrement();

        // Grade 6 is the lowest, so we calculate backwards
        return lowestSalary + ((6 - grade) * increment);
    }
}
