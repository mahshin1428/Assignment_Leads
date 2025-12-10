package Mahshin.Leads.SpringBootAssignment.controller;

import Mahshin.Leads.SpringBootAssignment.dto.*;
import Mahshin.Leads.SpringBootAssignment.service.SalaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/salary")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class SalaryController {

    private final SalaryService salaryService;


    // GET: Salary sheet for all employees (no payment)
    @GetMapping("/sheet")
    public ResponseEntity<List<SalarySheetDTO>> getSalarySheet() {
        return ResponseEntity.ok(salaryService.calculateSalaries());
    }


    // POST: Pay salary to all employees
    @PostMapping("/pay-all")
    public ResponseEntity<SalaryPaymentResponse> payAllEmployees(
            @RequestBody(required = false) SalaryPaymentRequest request) {

        if (request == null) {
            request = SalaryPaymentRequest.builder().additionalFunds(0.0).build();
        }

        return ResponseEntity.ok(salaryService.processSalaryPayment(request));
    }


    // POST: Pay salary to selected employees only
    @PostMapping("/pay-selected")
    public ResponseEntity<SalaryPaymentResponse> paySelectedEmployees(
            @RequestBody SalaryPaymentRequest request) {

        return ResponseEntity.ok(salaryService.processSelectedPayments(request));
    }


    // POST: Preview selected employees salary (no payment)
    @PostMapping("/preview-selected")
    public ResponseEntity<List<SalarySheetDTO>> previewSelected(
            @RequestBody SalaryPaymentRequest request) {

        List<SalarySheetDTO> all = salaryService.calculateSalaries();

        List<Long> ids = request.getEmployeeIds();
        List<SalarySheetDTO> selected = all.stream()
                .filter(s -> ids != null && ids.contains(s.getEmployeeId()))
                .toList();

        return ResponseEntity.ok(selected);
    }



// GET: Salary report / summary of salaries already paid by the company
    @GetMapping("/paid-report")
    public ResponseEntity<List<SalaryPaymentRecordDTO>> getPaidSalaryReport() {
        return ResponseEntity.ok(salaryService.getPaidSalaryReport());
 }

}