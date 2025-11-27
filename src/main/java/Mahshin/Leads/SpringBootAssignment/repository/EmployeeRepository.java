package Mahshin.Leads.SpringBootAssignment.repository;


import Mahshin.Leads.SpringBootAssignment.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, String> {

    Optional<Employee> findByEmployeeId(String employeeId);

    List<Employee> findByGrade(Integer grade);

    List<Employee> findAllByOrderByGradeAsc();

    boolean existsByEmployeeId(String employeeId);

    long countByGrade(Integer grade);
}