package Mahshin.Leads.SpringBootAssignment.controller;


import Mahshin.Leads.SpringBootAssignment.dto.SalaryConfigurationDTO;
import Mahshin.Leads.SpringBootAssignment.entity.SalaryConfiguration;
import Mahshin.Leads.SpringBootAssignment.service.SalaryConfigurationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/salary-configuration")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
class SalaryConfigurationController {

    private final SalaryConfigurationService salaryConfigService;

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