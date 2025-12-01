package dao;

import entity.Budget;
import entity.Category;
import entity.User;

import java.util.List;

public interface BudgetService {

    void setMonthlyBudget(User user, Category category, double amount);

    double getTotalBudget(User user);

    List<Budget> getAllBudgets(User user);

    void updateBudget(User user, Category category, double newAmount);

    double getCategoryBudget(User user, Category category);

    boolean removeBudget(User user, Category category); // NEW
}
