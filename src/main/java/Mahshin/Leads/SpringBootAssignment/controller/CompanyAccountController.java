package Mahshin.Leads.SpringBootAssignment.controller;


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

    public final CompanyAccountService companyAccountService;



    @PostMapping
    public ResponseEntity<CompanyAccount> createOrUpdateCompanyAccount(
            @RequestBody CompanyAccountDTO dto) {
        CompanyAccount account = companyAccountService.createOrUpdateCompanyAccount(dto);
        return ResponseEntity.ok(account);
    }

    @GetMapping
    public ResponseEntity<CompanyAccount> getCompanyAccount() {
        CompanyAccount account = companyAccountService.getCompanyAccount();
        return ResponseEntity.ok(account);
    }

    @PostMapping("/add-funds")
    public ResponseEntity<CompanyAccount> addFunds(@RequestParam Double amount) {
        CompanyAccount account = companyAccountService.addFunds(amount);
        return ResponseEntity.ok(account);
    }
}

