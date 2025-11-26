package dao;
import java.util.List;
import entity.User;
import entity.Category;
import entity.Budget;

public interface BudgetService {
    void setMonthlyBudget(User user, Category category, double amount);
    double getTotalBudget(User user);
    List<Budget> getAllBudgets(User user);
    void updateBudget(User user, Category category, double newAmount);
    double getCategoryBudget(User user, Category category);
}
