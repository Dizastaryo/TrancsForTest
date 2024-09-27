package com.example.trancs.controller;
import com.example.trancs.model.Transaction;
import com.example.trancs.service.TransactionService;
import com.example.trancs.service.AccountService;
import org.springframework.web.bind.annotation.*;
import java.io.File;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
@RestController
@RequestMapping("/api/financial")
@CrossOrigin(origins = "http://localhost:3000")
public class FinancialController {
    private final AccountService accountService;
    private final TransactionService transactionService;
    public FinancialController(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }
    @GetMapping("/balance")
    public ResponseEntity<Double> getAccountBalance() {
        double balance = accountService.getBalance();
        return ResponseEntity.ok(balance);
    }
    @PostMapping("/transactions/add-current-date")
    public ResponseEntity<String> addTransactionWithCurrentDate(
            @RequestParam Double amount,
            @RequestParam String operation,
            @RequestParam String details,
            @RequestParam String currency
    ) {
        try {
            transactionService.addTransactionWithCurrentDate(amount, operation, details, currency);
            return ResponseEntity.ok("Транзакция успешно добавлена с текущей датой.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при добавлении транзакции: " + e.getMessage());
        }
    }
    @PostMapping("/transactions/upload-fixed")
    public ResponseEntity<String> uploadFixedTransactions() {
        String filePath = "C:\\Users\\fedya\\IdeaProjects\\trancs\\транзакции.xlsx"; // Замените на фактический путь к вашему файлу
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Файл не найден");
            }
            transactionService.uploadTransactionsFromFile(file); // Измените здесь
            return ResponseEntity.ok("Транзакции успешно загружены");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Ошибка при загрузке транзакций: " + e.getMessage());
        }
    }

    @GetMapping("/transactions/exceeding-limit")
    public List<Transaction> getExceedingLimitTransactions() {
        return transactionService.getExceedingLimitTransactions();
    }
    @PostMapping("/transactions/set-limit")
    public ResponseEntity<String> setLimit(@RequestParam double amount) {
        try {
            transactionService.setLimit(amount);
            return ResponseEntity.ok("Лимит успешно установлен");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @GetMapping("/transactions/all")
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }
}

