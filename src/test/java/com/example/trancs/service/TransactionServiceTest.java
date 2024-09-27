package com.example.trancs.service;
import com.example.trancs.model.Transaction;
import com.example.trancs.model.Account;
import com.example.trancs.repository.TransactionRepository;
import com.example.trancs.repository.AccountRepository;
import com.example.trancs.repository.LimitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class TransactionServiceTest {

    @Mock
    private ExchangeRateService exchangeRateService;

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private LimitRepository limitRepository;

    @InjectMocks
    private TransactionService transactionService;

    private Account account;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Инициализируем тестовый аккаунт
        account = new Account();
        account.setId(1L);
        account.setBalance(10000.0);

        when(accountRepository.findById(1L)).thenReturn(Optional.of(account));
    }

    @Test
    public void testAddTransactionWithCurrentDate_Population() {
        double amount = 100.0;
        String operation = "Пополнение";
        String details = "Пополнение счета";
        String currency = "USD";

        transactionService.addTransactionWithCurrentDate(amount, operation, details, currency);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertEquals(10100.0, account.getBalance()); // Проверка обновленного баланса
    }

    @Test
    public void testAddTransactionWithCurrentDate_Purchase() {
        double amount = 50.0;
        String operation = "Покупка";
        String details = "Покупка товаров";
        String currency = "USD";

        transactionService.addTransactionWithCurrentDate(amount, operation, details, currency);

        verify(transactionRepository, times(1)).save(any(Transaction.class));
        assertEquals(9950.0, account.getBalance()); // Проверка обновленного баланса
    }

    @Test
    public void testSetLimit() {
        double newLimit = -1000.0;
        transactionService.setLimit(newLimit);

        // Проверка сохранения нового лимита
        verify(limitRepository, times(1)).save(any());
    }

    @Test
    public void testUploadTransactionsFromFile_FileNotFound() {
        File file = new File("nonexistent_file.xlsx");

        Exception exception = assertThrows(RuntimeException.class, () -> {
            transactionService.uploadTransactionsFromFile(file);
        });

        assertEquals("Файл не найден: nonexistent_file.xlsx", exception.getMessage());
    }

    @Test
    public void testExceedingLimit() {
        Transaction transaction = new Transaction();
        transaction.setAmount(-200.0);
        transaction.setCurrency("USD");
        transaction.setDate(LocalDate.now());

        when(transactionRepository.getMonthlyTotalSafe(any(), any())).thenReturn(-600.0);

        assertTrue(transactionService.isLimitExceeded(transaction));
    }
}

