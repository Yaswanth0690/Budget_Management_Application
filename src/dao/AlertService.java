package dao;
import entity.User;
import entity.Category;

public interface AlertService {
    void alertIfExceededDailyBudget(User user, Category category);
    void alertIfExceededMonthlyBudget(User user, Category category);
    void setPreferences(User user, boolean dailyAlert, boolean monthlyAlert, boolean reminders);
}
