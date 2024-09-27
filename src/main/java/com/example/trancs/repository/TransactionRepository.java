package com.example.trancs.repository;
import com.example.trancs.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDate;
import java.util.List;
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    List<Transaction> findByLimitExceeded(boolean limitExceeded);
    @Query("SELECT SUM(t.amount) FROM Transaction t WHERE t.date BETWEEN :startDate AND :endDate")
    Double getMonthlyTotalSafe(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);
}




