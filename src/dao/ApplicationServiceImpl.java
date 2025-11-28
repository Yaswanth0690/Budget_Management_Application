package dao.impl;

import dao.*;
import entity.Category;
import entity.User;

import java.util.List;
import java.util.Scanner;

public class ApplicationServiceImpl implements ApplicationService {

    private final BudgetService budgetService;
    private final ExpenseService expenseService;
    private final AlertService alertService;
    private final ReportService reportService;
    private final SavingsGoalService savingsGoalService;
    private final LoanService loanService;
    private final CategoryService categoryService;

    private final Scanner scanner = new Scanner(System.in);

    public ApplicationServiceImpl(BudgetService budgetService,
                                  ExpenseService expenseService,
                                  AlertService alertService,
                                  ReportService reportService,
                                  SavingsGoalService savingsGoalService,
                                  LoanService loanService,
                                  CategoryService categoryService) {
        this.budgetService = budgetService;
        this.expenseService = expenseService;
        this.alertService = alertService;
        this.reportService = reportService;
        this.savingsGoalService = savingsGoalService;
        this.loanService = loanService;
        this.categoryService = categoryService;
    }

    @Override
    public void showMainMenu(User user) {
        while (true) {
            System.out.println("\n===== Main Menu =====");
            System.out.println("1. Record a Payment");
            System.out.println("2. Extend/Edit Budget");
            System.out.println("3. View Reports");
            System.out.println("4. Savings Goal");
            System.out.println("5. Loan Management");
            System.out.println("6. Exit");
            System.out.print("Choose an option: ");

            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }

            switch (choice) {
                case 1 -> handleRecordPayment(user);
                case 2 -> handleEditBudget(user);
                case 3 -> reportService.displayExpenseReport(user);
                case 4 -> handleSavingsMenu(user);
                case 5 -> handleLoanMenu(user);
                case 6 -> {
                    exitApplication();
                    return;
                }
                default -> System.out.println("Invalid option. Try again.");
            }
        }
    }

    private void handleRecordPayment(User user) {
        System.out.print("Enter amount: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount.");
            return;
        }

        System.out.print("Enter date (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();

        Category category = chooseCategoryFromMenu();
        if (category == null) {
            System.out.println("No category selected. Cancelling payment.");
            return;
        }

        // record expense
        expenseService.recordExpense(user, amount, dateStr, category);

        // alerts
        alertService.alertIfExceededDailyBudget(user, category);
        alertService.alertIfExceededMonthlyBudget(user, category);
    }

    private Category chooseCategoryFromMenu() {
        while (true) {
            List<Category> categories = categoryService.getAllCategories();

            if (categories.isEmpty()) {
                System.out.println("⚠️ No categories found. Please add one.");
                System.out.print("Enter new category name: ");
                String name = scanner.nextLine();
                categoryService.addCategory(name);
                continue;
            }

            System.out.println("\nAvailable Categories:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i).getCategoryName());
            }
            System.out.println("0. ➕ Add NEW Category");
            System.out.println("-1. Cancel");

            System.out.print("Choose category: ");
            int choice;
            try {
                choice = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
                continue;
            }

            if (choice == -1) {
                return null;
            }

            if (choice == 0) {
                System.out.print("Enter new category name: ");
                String name = scanner.nextLine();
                categoryService.addCategory(name);
                continue; // reload list
            }

            if (choice < 1 || choice > categories.size()) {
                System.out.println("❌ Invalid selection.");
                continue;
            }

            return categories.get(choice - 1);
        }
    }

    private void handleEditBudget(User user) {
        // Reuse the same category menu
        Category category = chooseCategoryFromMenu();
        if (category == null) {
            System.out.println("No category selected.");
            return;
        }

        System.out.print("Enter new MONTHLY budget for " +
                category.getCategoryName() + ": ");
        double newAmount;
        try {
            newAmount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount.");
            return;
        }

        budgetService.updateBudget(user, category, newAmount);

        double total = budgetService.getTotalBudget(user);
        System.out.println("Total monthly budget now: " + total);
    }

    private void handleSavingsMenu(User user) {
        System.out.print("Enter savings goal amount: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount.");
            return;
        }

        System.out.print("Enter timeframe in months: ");
        int months;
        try {
            months = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number of months.");
            return;
        }

        savingsGoalService.setSavingsGoal(user, amount, months);
        savingsGoalService.displaySavingsGoal(user);
    }

    private void handleLoanMenu(User user) {
        System.out.print("Enter loan amount: ");
        double amount;
        try {
            amount = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid amount.");
            return;
        }

        System.out.print("Enter interest rate (% per year): ");
        double rate;
        try {
            rate = Double.parseDouble(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid rate.");
            return;
        }

        System.out.print("Enter repayment months: ");
        int months;
        try {
            months = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("❌ Invalid number of months.");
            return;
        }

        loanService.addLoan(user, amount, rate, months);
    }

    @Override
    public void exitApplication() {
        System.out.println("Exiting application. Goodbye!");
    }
}
