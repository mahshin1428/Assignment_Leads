package Mahshin.Leads.SpringBootAssignment.service;

import Mahshin.Leads.SpringBootAssignment.dto.SalaryPaymentRecordDTO;
import Mahshin.Leads.SpringBootAssignment.dto.SalaryPaymentRequest;
import Mahshin.Leads.SpringBootAssignment.dto.SalaryPaymentResponse;
import Mahshin.Leads.SpringBootAssignment.dto.SalarySheetDTO;
import Mahshin.Leads.SpringBootAssignment.entity.CompanyAccount;
import Mahshin.Leads.SpringBootAssignment.exception.InsufficientBalanceException;
import Mahshin.Leads.SpringBootAssignment.exception.ResourceNotFoundException;
import Mahshin.Leads.SpringBootAssignment.repository.plsql.CompanyAccountProcedureRepository;
import Mahshin.Leads.SpringBootAssignment.repository.plsql.SalaryProcedureRepository;
import Mahshin.Leads.SpringBootAssignment.repository.plsql.SalaryProcedureRepository.SalaryPaymentResult;
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

    private final SalaryProcedureRepository salaryRepository;
    private final CompanyAccountProcedureRepository companyAccountRepository;

    // in-memory payment records log (keeps a copy of payments made during runtime)
    private final List<SalaryPaymentRecordDTO> paymentRecords = Collections.synchronizedList(new ArrayList<>());

    // CALCULATE SALARY SHEET FOR ALL EMPLOYEES
    public List<SalarySheetDTO> calculateSalaries() {
        return salaryRepository.calculateSalarySheet();
    }

    // PAY SALARIES TO ALL EMPLOYEES
    @Transactional
    public SalaryPaymentResponse processSalaryPayment(SalaryPaymentRequest request) {
        Double additionalFunds = (request != null && request.getAdditionalFunds() != null)
                ? request.getAdditionalFunds() : 0.0;

        // Call stored procedure to pay all salaries
        SalaryPaymentResult result = salaryRepository.payAllSalaries(additionalFunds);

        if (!result.isSuccess()) {
            throw new InsufficientBalanceException(result.getMessage());
        }

        // Get salary sheet for response
        List<SalarySheetDTO> salarySheet = calculateSalaries();

        // Record payments in memory
        for (SalarySheetDTO sal : salarySheet) {
            SalaryPaymentRecordDTO record = SalaryPaymentRecordDTO.builder()
                    .employeeId(parseEmployeeId(sal.getEmployeeId()))
                    .employeeName(sal.getEmployeeName())
                    .grossSalary(sal.getTotalSalary())
                    .netPaid(sal.getTotalSalary())
                    .paidAt(LocalDateTime.now())
                    .paymentReference(UUID.randomUUID().toString())
                    .build();
            paymentRecords.add(record);
        }

        return SalaryPaymentResponse.builder()
                .success(true)
                .totalSalaryPaid(result.getTotalPaid())
                .remainingCompanyBalance(result.getRemainingBalance())
                .salarySheet(salarySheet)
                .build();
    }

    // PAY SALARY TO SELECTED EMPLOYEES ONLY
    @Transactional
    public SalaryPaymentResponse processSelectedPayments(SalaryPaymentRequest request) {

        if (request.getEmployeeIds() == null || request.getEmployeeIds().isEmpty()) {
            throw new IllegalArgumentException("Employee list cannot be empty");
        }

        // Convert Long IDs to String IDs for the procedure
        List<String> employeeIdStrings = request.getEmployeeIds().stream()
                .map(String::valueOf)
                .toList();

        Double additionalFunds = (request.getAdditionalFunds() != null)
                ? request.getAdditionalFunds() : 0.0;

        // Call stored procedure to pay selected salaries
        SalaryPaymentResult result = salaryRepository.paySelectedSalaries(employeeIdStrings, additionalFunds);

        if (!result.isSuccess()) {
            throw new InsufficientBalanceException(result.getMessage());
        }

        // Calculate salaries for all employees and filter selected
        List<SalarySheetDTO> allSalaries = calculateSalaries();
        List<SalarySheetDTO> selectedSalaries = allSalaries.stream()
                .filter(s -> request.getEmployeeIds().contains(parseEmployeeId(s.getEmployeeId())))
                .toList();

        // Record payments in memory
        for (SalarySheetDTO sal : selectedSalaries) {
            SalaryPaymentRecordDTO record = SalaryPaymentRecordDTO.builder()
                    .employeeId(parseEmployeeId(sal.getEmployeeId()))
                    .employeeName(sal.getEmployeeName())
                    .grossSalary(sal.getTotalSalary())
                    .netPaid(sal.getTotalSalary())
                    .paidAt(LocalDateTime.now())
                    .paymentReference(UUID.randomUUID().toString())
                    .build();
            paymentRecords.add(record);
        }

        return SalaryPaymentResponse.builder()
                .success(true)
                .totalSalaryPaid(result.getTotalPaid())
                .remainingCompanyBalance(result.getRemainingBalance())
                .salarySheet(selectedSalaries)
                .build();
    }

    // Provide a summary/report of salaries that have been paid by the company
    public List<SalaryPaymentRecordDTO> getPaidSalaryReport() {
        // return a copy to avoid exposing internal list
        synchronized (paymentRecords) {
            return new ArrayList<>(paymentRecords);
        }
    }

    // Helper method to parse employee ID (String to Long)
    private Long parseEmployeeId(String employeeId) {
        try {
            return Long.parseLong(employeeId);
        } catch (NumberFormatException e) {
            return 0L; // Default value if parsing fails
        }
    }
}

