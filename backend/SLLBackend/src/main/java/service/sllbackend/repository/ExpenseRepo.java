package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.Expense;

import java.time.LocalDate;
import java.util.List;

public interface ExpenseRepo extends JpaRepository<Expense, Long> {
    List<Expense> findByDateIncurredBetween(LocalDate start, LocalDate end);
}
