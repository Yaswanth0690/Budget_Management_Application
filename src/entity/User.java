package entity;

import java.util.List;

public class User {

    private int id;                     // DB Primary Key
    private String userId;              // Generated UID (e.g. ABC123)
    private String userName;

    private List<Budget> budgets;
    private List<Expense> expenses;
    private List<Loan> loans;

    private SavingsGoal savingsGoal;
    private NotificationSettings notificationSettings;

    public User() {}

    public User(int id, String userId, String userName, List<Budget> budgets,
                List<Expense> expenses, List<Loan> loans,
                SavingsGoal savingsGoal, NotificationSettings notificationSettings) {
        this.id = id;
        this.userId = userId;
        this.userName = userName;
        this.budgets = budgets;
        this.expenses = expenses;
        this.loans = loans;
        this.savingsGoal = savingsGoal;
        this.notificationSettings = notificationSettings;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }

    public List<Budget> getBudgets() { return budgets; }
    public void setBudgets(List<Budget> budgets) { this.budgets = budgets; }

    public List<Expense> getExpenses() { return expenses; }
    public void setExpenses(List<Expense> expenses) { this.expenses = expenses; }

    public List<Loan> getLoans() { return loans; }
    public void setLoans(List<Loan> loans) { this.loans = loans; }

    public SavingsGoal getSavingsGoal() { return savingsGoal; }
    public void setSavingsGoal(SavingsGoal savingsGoal) { this.savingsGoal = savingsGoal; }

    public NotificationSettings getNotificationSettings() { return notificationSettings; }
    public void setNotificationSettings(NotificationSettings notificationSettings) {
        this.notificationSettings = notificationSettings;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", userId='" + userId + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
