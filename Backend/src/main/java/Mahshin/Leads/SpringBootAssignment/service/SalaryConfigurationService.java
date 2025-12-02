package Mahshin.Leads.SpringBootAssignment.service;
import Mahshin.Leads.SpringBootAssignment.dto.SalaryConfigurationDTO;
import Mahshin.Leads.SpringBootAssignment.entity.SalaryConfiguration;
import Mahshin.Leads.SpringBootAssignment.exception.ResourceNotFoundException;
import Mahshin.Leads.SpringBootAssignment.repository.SalaryConfigurationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SalaryConfigurationService {

    private final SalaryConfigurationRepository salaryConfigRepository;

    @Transactional
    public SalaryConfiguration createOrUpdateConfiguration(SalaryConfigurationDTO dto) {
        List<SalaryConfiguration> configs = salaryConfigRepository.findAll();

        SalaryConfiguration config;
        if (configs.isEmpty()) {
            config = new SalaryConfiguration();
        } else {
            config = configs.get(0);
        }

        config.setLowestGradeBasicSalary(dto.getLowestGradeBasicSalary());
        config.setGradeIncrement(dto.getGradeIncrement() != null ? dto.getGradeIncrement() : 5000.0);
        config.setHouseRentPercentage(dto.getHouseRentPercentage() != null ? dto.getHouseRentPercentage() : 20.0);
        config.setMedicalAllowancePercentage(dto.getMedicalAllowancePercentage() != null ? dto.getMedicalAllowancePercentage() : 15.0);

        return salaryConfigRepository.save(config);
    }

    public SalaryConfiguration getConfiguration() {
        return salaryConfigRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Salary configuration not found"));
    }
}
