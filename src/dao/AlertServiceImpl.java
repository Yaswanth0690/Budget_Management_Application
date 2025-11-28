package dao.impl;

import dao.AlertService;
import dao.BudgetService;
import dao.ExpenseService;
import entity.Category;
import entity.User;

import java.time.LocalDate;

public class AlertServiceImpl implements AlertService {

    private final BudgetService budgetService;
    private final ExpenseService expenseService;

    public AlertServiceImpl(BudgetService budgetService, ExpenseService expenseService) {
        this.budgetService = budgetService;
        this.expenseService = expenseService;
    }

    @Override
    public void alertIfExceededDailyBudget(User user, Category category) {
        double monthlyLimit = budgetService.getCategoryBudget(user, category);
        if (monthlyLimit <= 0) return;

        int daysInMonth = LocalDate.now().lengthOfMonth();
        double dailyLimit = monthlyLimit / daysInMonth;

        double spentToday = expenseService.getTotalSpentToday(user, category);

        if (spentToday > dailyLimit) {
            System.out.println("⚠️ DAILY ALERT for " + category.getCategoryName());
            System.out.printf("   Daily limit: %.2f | Spent today: %.2f%n", dailyLimit, spentToday);
        }
    }

    @Override
    public void alertIfExceededMonthlyBudget(User user, Category category) {
        double monthlyLimit = budgetService.getCategoryBudget(user, category);
        if (monthlyLimit <= 0) return;

        double spentMonth = expenseService.getTotalSpentThisMonth(user, category);

        if (spentMonth > monthlyLimit) {
            System.out.println("🚨 MONTHLY ALERT for " + category.getCategoryName());
            System.out.printf("   Monthly limit: %.2f | Spent this month: %.2f%n", monthlyLimit, spentMonth);
        }
    }

    @Override
    public void setPreferences(User user, boolean dailyAlert, boolean monthlyAlert, boolean reminders) {
        // store in DB (notification_settings table) if you create one
        // for now just show message
        System.out.println("Notification preferences updated (not yet persisted).");
    }
}
