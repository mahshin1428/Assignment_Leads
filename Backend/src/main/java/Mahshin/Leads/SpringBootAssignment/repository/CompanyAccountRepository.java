package Mahshin.Leads.SpringBootAssignment.repository;



import Mahshin.Leads.SpringBootAssignment.entity.CompanyAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CompanyAccountRepository extends JpaRepository<CompanyAccount, Long> {
}