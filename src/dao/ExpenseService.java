package dao;
import entity.User;
import entity.Expense;
import entity.Category;

public interface ExpenseService {
    void recordExpense(User user, double amount, String date, Category category);
    double getTotalSpentToday(User user, Category category);
    double getTotalSpentThisMonth(User user, Category category);
    void saveExpense(User user, Expense expense);
}
