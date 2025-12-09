package service.sllbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import service.sllbackend.entity.ExpenseCategory;

public interface ExpenseCategoryRepo extends JpaRepository<ExpenseCategory, Long> {
}
