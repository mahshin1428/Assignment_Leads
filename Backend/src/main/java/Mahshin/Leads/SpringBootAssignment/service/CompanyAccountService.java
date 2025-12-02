package Mahshin.Leads.SpringBootAssignment.service;


import Mahshin.Leads.SpringBootAssignment.dto.CompanyAccountDTO;
import Mahshin.Leads.SpringBootAssignment.entity.CompanyAccount;
import Mahshin.Leads.SpringBootAssignment.exception.ResourceNotFoundException;
import Mahshin.Leads.SpringBootAssignment.repository.CompanyAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
@Service
@RequiredArgsConstructor
public class CompanyAccountService {

    private final CompanyAccountRepository companyAccountRepository;

    // Create a new company account
    @Transactional
    public CompanyAccount createCompanyAccount(CompanyAccountDTO dto) {
        CompanyAccount companyAccount = new CompanyAccount();
        companyAccount.setBalance(dto.getBalance());
        companyAccount.setAccountName(dto.getAccountName());
        companyAccount.setAccountNumber(dto.getAccountNumber());
        companyAccount.setBankName(dto.getBankName());
        companyAccount.setBranchName(dto.getBranchName());

        return companyAccountRepository.save(companyAccount);
    }

    // Get all company accounts
    public List<CompanyAccount> getAllCompanyAccounts() {
        List<CompanyAccount> accounts = companyAccountRepository.findAll();
        if (accounts.isEmpty()) {
            throw new ResourceNotFoundException("No company accounts found");
        }
        return accounts;
    }

    // Get a single company account by ID
    public CompanyAccount getCompanyAccountById(Long id) {
        return companyAccountRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Company account not found with id: " + id));
    }

    // Add funds to a specific company account
    @Transactional
    public CompanyAccount addFunds(Long id, Double amount) {
        // Find the account by ID
        CompanyAccount account = getCompanyAccountById(id);

        // Update the balance
        if (amount != null && amount > 0) {
            account.setBalance(account.getBalance() + amount);
        } else {
            throw new IllegalArgumentException("Amount must be positive and not null");
        }

        // Save and return
        return companyAccountRepository.save(account);
    }

}
