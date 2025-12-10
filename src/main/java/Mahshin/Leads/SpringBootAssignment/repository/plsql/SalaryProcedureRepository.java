package Mahshin.Leads.SpringBootAssignment.repository.plsql;

import Mahshin.Leads.SpringBootAssignment.dto.SalarySheetDTO;
import Mahshin.Leads.SpringBootAssignment.entity.SalaryConfiguration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Repository implementation using PL/SQL stored procedures for Salary operations
 */
@Repository
public class SalaryProcedureRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcCall getSalaryConfigProc;
    private final SimpleJdbcCall updateSalaryConfigProc;
    private final SimpleJdbcCall calculateSalarySheetProc;
    private final SimpleJdbcCall payAllSalariesProc;
    private final SimpleJdbcCall paySelectedSalariesProc;

    public SalaryProcedureRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.getSalaryConfigProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("SP_GET_SALARY_CONFIG")
                .declareParameters(
                        new SqlOutParameter("p_cursor", Types.REF_CURSOR, new SalaryConfigRowMapper())
                );

        this.updateSalaryConfigProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("SP_UPDATE_SALARY_CONFIG")
                .declareParameters(
                        new SqlParameter("p_lowest_salary", Types.NUMERIC),
                        new SqlParameter("p_grade_increment", Types.NUMERIC),
                        new SqlParameter("p_house_rent_pct", Types.NUMERIC),
                        new SqlParameter("p_medical_pct", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.NUMERIC),
                        new SqlOutParameter("p_message", Types.VARCHAR)
                );

        this.calculateSalarySheetProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("SP_CALCULATE_SALARY_SHEET")
                .declareParameters(
                        new SqlOutParameter("p_cursor", Types.REF_CURSOR, new SalarySheetRowMapper())
                );

        this.payAllSalariesProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("SP_PAY_ALL_SALARIES")
                .declareParameters(
                        new SqlParameter("p_additional_funds", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.NUMERIC),
                        new SqlOutParameter("p_message", Types.VARCHAR),
                        new SqlOutParameter("p_total_paid", Types.NUMERIC),
                        new SqlOutParameter("p_remaining_balance", Types.NUMERIC)
                );

        this.paySelectedSalariesProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("SP_PAY_SELECTED_SALARIES")
                .declareParameters(
                        new SqlParameter("p_employee_ids", Types.VARCHAR),
                        new SqlParameter("p_additional_funds", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.NUMERIC),
                        new SqlOutParameter("p_message", Types.VARCHAR),
                        new SqlOutParameter("p_total_paid", Types.NUMERIC),
                        new SqlOutParameter("p_remaining_balance", Types.NUMERIC)
                );
    }

    @SuppressWarnings("unchecked")
    public Optional<SalaryConfiguration> getSalaryConfiguration() {
        Map<String, Object> result = getSalaryConfigProc.execute();
        List<SalaryConfiguration> configs = (List<SalaryConfiguration>) result.get("p_cursor");
        return configs.isEmpty() ? Optional.empty() : Optional.of(configs.get(0));
    }

    public ProcedureResult updateSalaryConfiguration(SalaryConfiguration config) {
        Map<String, Object> inParams = Map.of(
                "p_lowest_salary", config.getLowestGradeBasicSalary(),
                "p_grade_increment", config.getGradeIncrement(),
                "p_house_rent_pct", config.getHouseRentPercentage(),
                "p_medical_pct", config.getMedicalAllowancePercentage()
        );

        Map<String, Object> result = updateSalaryConfigProc.execute(inParams);

        return new ProcedureResult(
                ((Number) result.get("p_result")).intValue(),
                (String) result.get("p_message")
        );
    }

    @SuppressWarnings("unchecked")
    public List<SalarySheetDTO> calculateSalarySheet() {
        Map<String, Object> result = calculateSalarySheetProc.execute();
        return (List<SalarySheetDTO>) result.get("p_cursor");
    }

    public SalaryPaymentResult payAllSalaries(Double additionalFunds) {
        Map<String, Object> inParams = Map.of("p_additional_funds", additionalFunds != null ? additionalFunds : 0.0);
        Map<String, Object> result = payAllSalariesProc.execute(inParams);

        return new SalaryPaymentResult(
                ((Number) result.get("p_result")).intValue(),
                (String) result.get("p_message"),
                ((Number) result.get("p_total_paid")).doubleValue(),
                ((Number) result.get("p_remaining_balance")).doubleValue()
        );
    }

    public SalaryPaymentResult paySelectedSalaries(List<String> employeeIds, Double additionalFunds) {
        String idsString = String.join(",", employeeIds);

        Map<String, Object> inParams = Map.of(
                "p_employee_ids", idsString,
                "p_additional_funds", additionalFunds != null ? additionalFunds : 0.0
        );

        Map<String, Object> result = paySelectedSalariesProc.execute(inParams);

        return new SalaryPaymentResult(
                ((Number) result.get("p_result")).intValue(),
                (String) result.get("p_message"),
                ((Number) result.get("p_total_paid")).doubleValue(),
                ((Number) result.get("p_remaining_balance")).doubleValue()
        );
    }

    // Row mapper for SalaryConfiguration
    private static class SalaryConfigRowMapper implements RowMapper<SalaryConfiguration> {
        @Override
        public SalaryConfiguration mapRow(ResultSet rs, int rowNum) throws SQLException {
            return SalaryConfiguration.builder()
                    .id(rs.getLong("ID"))
                    .lowestGradeBasicSalary(rs.getDouble("LOWEST_GRADE_BASIC_SALARY"))
                    .gradeIncrement(rs.getDouble("GRADE_INCREMENT"))
                    .houseRentPercentage(rs.getDouble("HOUSE_RENT_PERCENTAGE"))
                    .medicalAllowancePercentage(rs.getDouble("MEDICAL_ALLOWANCE_PERCENTAGE"))
                    .build();
        }
    }

    // Row mapper for SalarySheet
    private static class SalarySheetRowMapper implements RowMapper<SalarySheetDTO> {
        @Override
        public SalarySheetDTO mapRow(ResultSet rs, int rowNum) throws SQLException {
            return SalarySheetDTO.builder()
                    .employeeId(rs.getString("EMPLOYEE_ID"))
                    .employeeName(rs.getString("EMPLOYEE_NAME"))
                    .grade(rs.getInt("GRADE"))
                    .basicSalary(rs.getDouble("BASIC_SALARY"))
                    .houseRent(rs.getDouble("HOUSE_RENT"))
                    .medicalAllowance(rs.getDouble("MEDICAL_ALLOWANCE"))
                    .totalSalary(rs.getDouble("TOTAL_SALARY"))
                    .build();
        }
    }

    // Result classes
    public static class ProcedureResult {
        private final int resultCode;
        private final String message;

        public ProcedureResult(int resultCode, String message) {
            this.resultCode = resultCode;
            this.message = message;
        }

        public boolean isSuccess() {
            return resultCode == 0;
        }

        public int getResultCode() {
            return resultCode;
        }

        public String getMessage() {
            return message;
        }
    }

    public static class SalaryPaymentResult extends ProcedureResult {
        private final double totalPaid;
        private final double remainingBalance;

        public SalaryPaymentResult(int resultCode, String message, double totalPaid, double remainingBalance) {
            super(resultCode, message);
            this.totalPaid = totalPaid;
            this.remainingBalance = remainingBalance;
        }

        public double getTotalPaid() {
            return totalPaid;
        }

        public double getRemainingBalance() {
            return remainingBalance;
        }
    }
}

