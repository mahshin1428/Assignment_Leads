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

    public CompanyAccountService(CompanyAccountRepository companyAccountRepository) {
        this.companyAccountRepository = companyAccountRepository;
    }

    @Transactional
    public CompanyAccount createOrUpdateCompanyAccount(CompanyAccountDTO dto) {
        List<CompanyAccount> accounts = companyAccountRepository.findAll();

        CompanyAccount companyAccount;
        if (accounts.isEmpty()) {
            companyAccount = new CompanyAccount();
        } else {
            companyAccount = accounts.get(0);
        }

        companyAccount.setBalance(dto.getBalance());
        companyAccount.setAccountName(dto.getAccountName());
        companyAccount.setAccountNumber(dto.getAccountNumber());
        companyAccount.setBankName(dto.getBankName());
        companyAccount.setBranchName(dto.getBranchName());

        return companyAccountRepository.save(companyAccount);
    }

    public CompanyAccount getCompanyAccount() {
        return companyAccountRepository.findAll().stream()
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("Company account not found"));
    }

    @Transactional
    public CompanyAccount addFunds(Double amount) {
        CompanyAccount account = getCompanyAccount();
        account.setBalance(account.getBalance() + amount);
        return companyAccountRepository.save(account);
    }
}