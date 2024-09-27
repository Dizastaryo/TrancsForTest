package com.example.trancs.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import com.example.trancs.repository.AccountRepository;
import com.example.trancs.model.Account;
@Component
public class AccountInitializer implements ApplicationListener<ContextRefreshedEvent> {
    @Autowired
    private AccountRepository accountRepository;
    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        if (accountRepository.count() == 0) {
            Account account = new Account();
            account.setBalance(10000.0);
            accountRepository.save(account);
            System.out.println("Account created with initial balance: " + account.getBalance());
        }
    }
}
