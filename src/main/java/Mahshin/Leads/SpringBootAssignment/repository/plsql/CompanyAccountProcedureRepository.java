
package Mahshin.Leads.SpringBootAssignment.repository.plsql;

import Mahshin.Leads.SpringBootAssignment.entity.CompanyAccount;
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
 * Repository implementation using PL/SQL stored procedures for Company Account operations
 */
@Repository
public class CompanyAccountProcedureRepository {

    private final JdbcTemplate jdbcTemplate;
    private final SimpleJdbcCall getCompanyAccountProc;
    private final SimpleJdbcCall addFundsProc;
    private final SimpleJdbcCall updateCompanyAccountProc;

    public CompanyAccountProcedureRepository(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);

        this.getCompanyAccountProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("SP_GET_COMPANY_ACCOUNT")
                .declareParameters(
                        new SqlOutParameter("p_cursor", Types.REF_CURSOR, new CompanyAccountRowMapper())
                );

        this.addFundsProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("SP_ADD_FUNDS")
                .declareParameters(
                        new SqlParameter("p_amount", Types.NUMERIC),
                        new SqlOutParameter("p_result", Types.NUMERIC),
                        new SqlOutParameter("p_message", Types.VARCHAR),
                        new SqlOutParameter("p_new_balance", Types.NUMERIC)
                );

        this.updateCompanyAccountProc = new SimpleJdbcCall(dataSource)
                .withProcedureName("SP_UPDATE_COMPANY_ACCOUNT")
                .declareParameters(
                        new SqlParameter("p_account_name", Types.VARCHAR),
                        new SqlParameter("p_account_number", Types.VARCHAR),
                        new SqlParameter("p_bank_name", Types.VARCHAR),
                        new SqlParameter("p_branch_name", Types.VARCHAR),
                        new SqlOutParameter("p_result", Types.NUMERIC),
                        new SqlOutParameter("p_message", Types.VARCHAR)
                );
    }

    @SuppressWarnings("unchecked")
    public Optional<CompanyAccount> getCompanyAccount() {
        Map<String, Object> result = getCompanyAccountProc.execute();
        List<CompanyAccount> accounts = (List<CompanyAccount>) result.get("p_cursor");
        return accounts.isEmpty() ? Optional.empty() : Optional.of(accounts.get(0));
    }

    public AddFundsResult addFunds(Double amount) {
        Map<String, Object> inParams = Map.of("p_amount", amount);
        Map<String, Object> result = addFundsProc.execute(inParams);

        return new AddFundsResult(
                ((Number) result.get("p_result")).intValue(),
                (String) result.get("p_message"),
                ((Number) result.get("p_new_balance")).doubleValue()
        );
    }

    public ProcedureResult updateCompanyAccount(CompanyAccount account) {
        Map<String, Object> inParams = Map.of(
                "p_account_name", account.getAccountName(),
                "p_account_number", account.getAccountNumber(),
                "p_bank_name", account.getBankName(),
                "p_branch_name", account.getBranchName()
        );

        Map<String, Object> result = updateCompanyAccountProc.execute(inParams);

        return new ProcedureResult(
                ((Number) result.get("p_result")).intValue(),
                (String) result.get("p_message")
        );
    }

    // Row mapper for CompanyAccount
    private static class CompanyAccountRowMapper implements RowMapper<CompanyAccount> {
        @Override
        public CompanyAccount mapRow(ResultSet rs, int rowNum) throws SQLException {
            return CompanyAccount.builder()
                    .id(rs.getLong("ID"))
                    .balance(rs.getDouble("BALANCE"))
                    .accountName(rs.getString("ACCOUNT_NAME"))
                    .accountNumber(rs.getString("ACCOUNT_NUMBER"))
                    .bankName(rs.getString("BANK_NAME"))
                    .branchName(rs.getString("BRANCH_NAME"))
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

    public static class AddFundsResult extends ProcedureResult {
        private final double newBalance;

        public AddFundsResult(int resultCode, String message, double newBalance) {
            super(resultCode, message);
            this.newBalance = newBalance;
        }

        public double getNewBalance() {
            return newBalance;
        }
    }
}

