package com.example.trancs.repository;
import com.example.trancs.model.Account;
import org.springframework.data.jpa.repository.JpaRepository;
public interface AccountRepository extends JpaRepository<Account, Long> {
}
