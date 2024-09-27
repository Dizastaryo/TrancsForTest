package com.example.trancs.service;
import com.example.trancs.model.Transaction;
import com.example.trancs.model.Account;
import com.example.trancs.model.Limit;
import com.example.trancs.repository.TransactionRepository;
import com.example.trancs.repository.LimitRepository;
import com.example.trancs.repository.AccountRepository;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;

@Service
public class TransactionService {
    private final ExchangeRateService exchangeRateService;
    private final TransactionRepository transactionRepository;
    private final AccountRepository accountRepository;
    private final LimitRepository limitRepository;
    private double monthlyLimit = -500.0;
    public TransactionService(ExchangeRateService exchangeRateService,
                              TransactionRepository transactionRepository,
                              AccountRepository accountRepository,
                              LimitRepository limitRepository) {
        this.exchangeRateService = exchangeRateService;
        this.transactionRepository = transactionRepository;
        this.accountRepository = accountRepository;
        this.limitRepository = limitRepository;
    }
    public void setLimit(double limit) {
        if (limit >= 0) {
            throw new IllegalArgumentException("Лимит должен быть отрицательным.");
        }
        this.monthlyLimit = limit; 
        Limit newLimit = new Limit(limit, LocalDate.now().atStartOfDay());
        limitRepository.save(newLimit);
    }
    public void uploadTransactionsFromFile(File file) {
        if (!file.exists()) {
            throw new RuntimeException("Файл не найден: " + file.getPath());
        }
        List<Transaction> exceedingLimitTransactions = new ArrayList<>();
        try (XSSFWorkbook workbook = new XSSFWorkbook(new FileInputStream(file))) {
            var sheet = workbook.getSheetAt(0);
            int startRow = 1;
            for (int i = startRow; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                Transaction transaction = new Transaction();
                if (row.getCell(0) != null && row.getCell(0).getCellType() == CellType.NUMERIC) {
                    transaction.setDate(row.getCell(0).getLocalDateTimeCellValue().toLocalDate());
                }
                double amount = (row.getCell(1) != null && row.getCell(1).getCellType() == CellType.NUMERIC)
                        ? row.getCell(1).getNumericCellValue() : 0.0;
                transaction.setAmount(amount);
                String operation = "";
                if (row.getCell(2) != null) {
                    operation = (row.getCell(2).getCellType() == CellType.STRING)
                            ? row.getCell(2).getStringCellValue()
                            : String.valueOf(row.getCell(2).getNumericCellValue());
                }
                transaction.setOperation(operation);
                String details = "";
                Cell detailsCell = row.getCell(3);
                if (detailsCell != null) {
                    if (detailsCell.getCellType() == CellType.STRING) {
                        details = detailsCell.getStringCellValue();
                    } else if (detailsCell.getCellType() == CellType.NUMERIC) {
                        details = String.valueOf(detailsCell.getNumericCellValue());
                    }
                }
                transaction.setDetails(details.trim());
                String currency = "";
                if (row.getCell(4) != null) {
                    currency = (row.getCell(4).getCellType() == CellType.STRING)
                            ? row.getCell(4).getStringCellValue().trim()
                            : String.valueOf(row.getCell(4).getNumericCellValue()).trim();
                }
                transaction.setCurrency(currency);
                if ("Покупка".equalsIgnoreCase(operation) || "Перевод".equalsIgnoreCase(operation)) {
                    transaction.setAmount(-Math.abs(amount)); // Отрицательная сумма
                } else if ("Пополнение".equalsIgnoreCase(operation)) {
                    transaction.setAmount(Math.abs(amount)); // Положительная сумма
                } else {
                    throw new IllegalArgumentException("Неизвестная операция: " + operation);
                }
                transaction.setLimitExceeded(isLimitExceeded(transaction));
                if (transaction.getLimitExceeded()) {
                    exceedingLimitTransactions.add(transaction);
                }
                transactionRepository.save(transaction);
            }
            for (Transaction exceedingTransaction : exceedingLimitTransactions) {
                exceedingTransaction.setLimitExceeded(true);
                transactionRepository.save(exceedingTransaction);
            }
        } catch (IOException e) {
            throw new RuntimeException("Не удалось обработать файл Excel", e);
        }
    }
    public void addTransactionWithCurrentDate(Double amount, String operation, String details, String currency) {
        if (amount == null || operation == null || currency == null) {
            throw new IllegalArgumentException("Необходимо указать сумму, операцию и валюту.");
        }
        Transaction transaction = new Transaction();
        transaction.setDate(LocalDate.now());
        if ("Покупка".equalsIgnoreCase(operation) || "Перевод".equalsIgnoreCase(operation)) {
            transaction.setAmount(-Math.abs(amount)); // Отрицательная сумма
        } else if ("Пополнение".equalsIgnoreCase(operation)) {
            transaction.setAmount(Math.abs(amount)); // Положительная сумма
        } else {
            throw new IllegalArgumentException("Неизвестная операция: " + operation);
        }
        transaction.setOperation(operation);
        transaction.setDetails(details);
        transaction.setCurrency(currency);
        transaction.setLimitExceeded(isLimitExceeded(transaction));
        double convertedAmount = convertToUSD(transaction.getAmount(), currency, transaction.getDate());
        updateAccountBalance(convertedAmount);
        transactionRepository.save(transaction);
    }
    private void updateAccountBalance(Double amount) {
        Account account = accountRepository.findById(1L).orElseThrow(() -> new RuntimeException("Аккаунт не найден"));
        account.setBalance(account.getBalance() + amount);
        accountRepository.save(account);
    }
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }
    public List<Transaction> getExceedingLimitTransactions() {
        return transactionRepository.findByLimitExceeded(true);
    }
    private double getCurrentMonthlyTotal(LocalDate date) {
        LocalDate firstDayOfMonth = date.withDayOfMonth(1);
        LocalDate lastDayOfMonth = date.withDayOfMonth(date.lengthOfMonth());
        Double total = transactionRepository.getMonthlyTotalSafe(firstDayOfMonth, lastDayOfMonth);
        return total != null ? total : 0.0;
    }
    boolean isLimitExceeded(Transaction transaction) {
        double transactionAmountInUSD = convertToUSD(transaction.getAmount(), transaction.getCurrency(), transaction.getDate());
        double currentTotal = getCurrentMonthlyTotal(transaction.getDate());
        return (currentTotal + transactionAmountInUSD) < monthlyLimit;
    }
    private double convertToUSD(double amount, String currency, LocalDate date) {
        if ("USD".equalsIgnoreCase(currency)) {
            return amount;
        }
        return exchangeRateService.calculateExpenseInUSD(amount, currency, date);
    }
}




