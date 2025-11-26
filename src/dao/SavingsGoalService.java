package dao;
import entity.User;

public interface SavingsGoalService {
    void setSavingsGoal(User user, double amount, int months);
    double calculateMonthlySavingsRequired(double totalGoal, int months);
    void displaySavingsGoal(User user);
}
