package Mahshin.Leads.SpringBootAssignment.controller;


import Mahshin.Leads.SpringBootAssignment.dto.SalaryPaymentRequest;
import Mahshin.Leads.SpringBootAssignment.dto.SalaryPaymentResponse;
import Mahshin.Leads.SpringBootAssignment.dto.SalarySheetDTO;
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

    private SalaryService salaryService;

    public SalaryService getSalaryService() {
        return salaryService;
    }

    @GetMapping("/calculate")
    public ResponseEntity<List<SalarySheetDTO>> calculateSalaries() {
        List<SalarySheetDTO> salarySheet = salaryService.calculateSalaries();
        return ResponseEntity.ok(salarySheet);
    }

    @PostMapping("/process-payment")
    public ResponseEntity<SalaryPaymentResponse> processSalaryPayment(
            @RequestBody SalaryPaymentRequest request) {
        SalaryPaymentResponse response = salaryService.processSalaryPayment(request);
        return ResponseEntity.ok(response);
    } 
}