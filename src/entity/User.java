package entity;
import java.util.List;
public class User {
    private String userId;
    private String userName;
    private List<Budget> budgets;
    private List<Expense> expenses;
    private List<Loan> loans;
    private SavingsGoal savingsGoal;
    private NotificationSettings notificationSettings;
}
