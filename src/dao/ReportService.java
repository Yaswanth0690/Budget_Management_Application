package dao;
import java.util.List;
import entity.User;
import entity.Expense;
import entity.Category;

public interface ReportService {
    List<Expense> getExpensesByCategory(User user, Category category);
    void displayExpenseReport(User user);
    void analyzeSpendingPatterns(User user);
    void recommendSavingsAreas(User user);
}
