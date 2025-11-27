package Mahshin.Leads.SpringBootAssignment.repository;


import Mahshin.Leads.SpringBootAssignment.entity.SalaryConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SalaryConfigurationRepository extends JpaRepository<SalaryConfiguration, Long> {
}