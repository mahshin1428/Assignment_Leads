package Mahshin.Leads.SpringBootAssignment.controller;


import Mahshin.Leads.SpringBootAssignment.dto.CompanyAccountDTO;
import Mahshin.Leads.SpringBootAssignment.dto.SalaryConfigurationDTO;
import Mahshin.Leads.SpringBootAssignment.entity.CompanyAccount;
import Mahshin.Leads.SpringBootAssignment.entity.SalaryConfiguration;
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

    CompanyAccountController(CompanyAccountService companyAccountService) {
        this.companyAccountService = companyAccountService;
    }

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

@RestController
@RequestMapping("/api/salary-configuration")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
class SalaryConfigurationController {

    private final SalaryConfigurationService salaryConfigService;

    SalaryConfigurationController(SalaryConfigurationService salaryConfigService) {
        this.salaryConfigService = salaryConfigService;
    }

    @PostMapping
    public ResponseEntity<SalaryConfiguration> createOrUpdateConfiguration(
            @RequestBody SalaryConfigurationDTO dto) {
        SalaryConfiguration config = salaryConfigService.createOrUpdateConfiguration(dto);
        return ResponseEntity.ok(config);
    }

    @GetMapping
    public ResponseEntity<SalaryConfiguration> getConfiguration() {
        SalaryConfiguration config = salaryConfigService.getConfiguration();
        return ResponseEntity.ok(config);
    }
}