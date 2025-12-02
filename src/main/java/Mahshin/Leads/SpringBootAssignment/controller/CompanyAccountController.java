package Mahshin.Leads.SpringBootAssignment.controller;

import java.util.*;
import Mahshin.Leads.SpringBootAssignment.dto.*;
import Mahshin.Leads.SpringBootAssignment.entity.*;
import Mahshin.Leads.SpringBootAssignment.service.CompanyAccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/company-account")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
class CompanyAccountController {

    private final CompanyAccountService companyAccountService;

    // Create a new account
    @PostMapping
    public ResponseEntity<CompanyAccount> createCompanyAccount(@RequestBody CompanyAccountDTO dto) {
        CompanyAccount account = companyAccountService.createCompanyAccount(dto);
        return ResponseEntity.ok(account);
    }

    // Get all accounts
    @GetMapping
    public ResponseEntity<List<CompanyAccount>> getAllAccounts() {
        return ResponseEntity.ok(companyAccountService.getAllCompanyAccounts());
    }

    // Get a single account by ID
    @GetMapping("/{id}")
    public ResponseEntity<CompanyAccount> getAccountById(@PathVariable Long id) {
        CompanyAccount account = companyAccountService.getCompanyAccountById(id);
        return ResponseEntity.ok(account);
    }

    // Add funds to a specific account using POST and JSON body
    @PostMapping("/{id}/add-funds")
    public ResponseEntity<CompanyAccount> addFunds(
            @PathVariable Long id,
            @RequestBody AddFundsDTO dto) {
        CompanyAccount account = companyAccountService.addFunds(id, dto.getAmount());
        return ResponseEntity.ok(account);
    }

}
