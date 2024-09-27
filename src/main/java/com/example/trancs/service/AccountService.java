package com.example.trancs.service;
import com.example.trancs.model.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.example.trancs.repository.AccountRepository;
@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    public double getBalance() {
        return accountRepository.findById(1L)
                .map(Account::getBalance)
                .orElseThrow(() -> new RuntimeException("Account not found"));
    }
}


