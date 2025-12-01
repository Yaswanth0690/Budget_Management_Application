package dao.impl;

import dao.*;
import entity.Category;
import entity.User;
import util.DBConnUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
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
            System.out.println();
            System.out.println("===== 🏠 MAIN MENU =====");
            System.out.println("1. 🧾 Record a Payment");
            System.out.println("2. 💰 Extend/Edit Budget");
            System.out.println("3. 📊 View Reports");
            System.out.println("4. 🎯 Savings Goals");
            System.out.println("5. 🏦 Loan Management");
            System.out.println("6. 🚪 Exit");

            int choice = readInt("➡ Choose an option: ");

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
                default -> System.out.println("❌ Invalid option. Enter 1-6.");
            }
        }
    }

    // ===================== RECORD PAYMENT =====================

    private void handleRecordPayment(User user) {

        System.out.println("\n🧾 Record a Payment");
        Category category = chooseCategoryFromMenu();
        if (category == null) {
            System.out.println("❌ Payment cancelled.");
            return;
        }

        double monthlyBudget = budgetService.getCategoryBudget(user, category);
        if (monthlyBudget <= 0) {
            System.out.println("\n⚠️ No monthly budget set for '" + category.getCategoryName() + "'.");
            System.out.println("1. 💰 Set a budget now");
            System.out.println("2. ➡ Continue without budget");
            int choice = readInt("Choose: ");
            if (choice == 1) {
                handleEditBudget(user);
                return;
            }
        }

        double amount = readPositiveDouble("💵 Enter amount: ");
        String today = LocalDate.now().toString();

        expenseService.recordExpense(user, amount, today, category);
        System.out.println("✅ Expense recorded successfully on " + today + ".");

        if (monthlyBudget > 0) {
            alertService.alertIfExceededDailyBudget(user, category);
            alertService.alertIfExceededMonthlyBudget(user, category);
        }
    }

    // ===================== EDIT BUDGET =====================

    private void handleEditBudget(User user) {

        System.out.println("\n💰 Extend/Edit Budget");
        Category category = chooseCategoryFromMenu();
        if (category == null) {
            System.out.println("❌ Operation cancelled.");
            return;
        }

        double currentBudget = budgetService.getCategoryBudget(user, category);

        System.out.println("\n📂 Category: " + category.getCategoryName());
        if (currentBudget > 0)
            System.out.println("📌 Current monthly budget: ₹" + currentBudget);
        else
            System.out.println("⚠️ No budget set for this category.");

        System.out.println("\nWhat would you like to do?");
        System.out.println("1. ✏ Update Budget");
        System.out.println("2. 🗑 Remove Budget");
        System.out.println("3. ❌ Cancel");

        int choice = readInt("Choose: ");

        switch (choice) {
            case 1 -> {
                double newAmount = readPositiveDouble("💵 Enter new MONTHLY budget: ");
                budgetService.updateBudget(user, category, newAmount);
                System.out.println("✅ Budget updated successfully!");
            }
            case 2 -> {
                if (currentBudget <= 0) {
                    System.out.println("❌ No existing budget to remove.");
                    return;
                }
                System.out.print("⚠️ Are you sure you want to DELETE budget for "
                        + category.getCategoryName() + "? (yes/no): ");

                String confirm = scanner.nextLine().trim().toLowerCase();
                if (confirm.equals("yes")) {
                    boolean removed = budgetService.removeBudget(user, category);
                    if (removed) {
                        System.out.println("🗑 Budget removed successfully!");
                    } else {
                        System.out.println("❌ Failed to remove budget.");
                    }
                } else {
                    System.out.println("❌ Deletion cancelled.");
                }
            }
            case 3 -> System.out.println("❌ Cancelled.");
            default -> System.out.println("❌ Invalid choice.");
        }

        double totalBudget = budgetService.getTotalBudget(user);
        System.out.println("🧮 Updated TOTAL monthly budget: ₹" + totalBudget);
    }

    // ===================== CATEGORY SELECT =====================

    private Category chooseCategoryFromMenu() {
        while (true) {
            List<Category> categories = categoryService.getAllCategories();

            System.out.println("\n📂 --- Choose Category ---");

            if (categories.isEmpty()) {
                String name = readNonEmptyString("⚠️ No categories exist. Enter name to create: ");
                categoryService.addCategory(name);
                continue;
            }

            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i).getCategoryName());
            }
            System.out.println("0. ➕ Add NEW Category");
            System.out.println("-1. ❌ Cancel");

            int choice = readInt("➡ Choose: ");

            if (choice == -1) return null;
            if (choice == 0) {
                String name = readNonEmptyString("➕ Enter category name: ");
                categoryService.addCategory(name);
                continue;
            }
            if (choice >= 1 && choice <= categories.size()) {
                return categories.get(choice - 1);
            }

            System.out.println("❌ Invalid choice. Try again.");
        }
    }

    // ===================== SAVINGS MENU (Option 4) =====================

    private void handleSavingsMenu(User user) {
        System.out.println("\n🎯 Savings Goals");

        // 1️⃣ Show existing goals
        savingsGoalService.displaySavingsGoal(user);

        // 2️⃣ Ask if want to add new goal
        System.out.print("\n➕ Do you want to ADD a new savings goal? (yes/no): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        if (!ans.equals("yes")) {
            System.out.println("ℹ️ No new savings goals added.");
            return;
        }

        // 3️⃣ Add new goal
        double amount = readPositiveDouble("💰 Enter savings goal amount: ");
        int months = readPositiveInt("📅 Enter timeframe (months): ");

        savingsGoalService.setSavingsGoal(user, amount, months);

        // 4️⃣ Show updated list
        savingsGoalService.displaySavingsGoal(user);
    }

    // ===================== LOAN MENU (Option 5) =====================

    private void handleLoanMenu(User user) {
        System.out.println("\n🏦 Loan Management");

        // 1️⃣ Show existing loans from DB
        showExistingLoans(user);

        // 2️⃣ Ask if they want to add a new one
        System.out.print("\n➕ Do you want to add a NEW loan? (yes/no): ");
        String ans = scanner.nextLine().trim().toLowerCase();
        if (!ans.equals("yes")) {
            System.out.println("ℹ️ No new loans added.");
            return;
        }

        // 3️⃣ Add new loan
        double amount = readPositiveDouble("💰 Loan amount: ");
        double rate = readNonNegativeDouble("📈 Interest rate (% per year): ");
        int months = readPositiveInt("📅 Repayment months: ");

        loanService.addLoan(user, amount, rate, months);
    }

    private void showExistingLoans(User user) {
        String sql = "SELECT principal, interest_rate, repayment_months, monthly_repayment " +
                "FROM loans WHERE user_id = ?";

        System.out.println("\n📜 Your existing loans:");

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                boolean any = false;

                System.out.printf("%-12s %-10s %-10s %-15s%n",
                        "Principal", "Rate%", "Months", "Monthly EMI");
                System.out.println("-------------------------------------------------");

                while (rs.next()) {
                    any = true;
                    double principal = rs.getDouble("principal");
                    double rate = rs.getDouble("interest_rate");
                    int months = rs.getInt("repayment_months");
                    double emi = rs.getDouble("monthly_repayment");

                    System.out.printf("%-12.2f %-10.2f %-10d %-15.2f%n",
                            principal, rate, months, emi);
                }

                if (!any) {
                    System.out.println("ℹ️ You currently have no loans recorded.");
                }

            }

        } catch (SQLException e) {
            System.out.println("❌ Error loading existing loans.");
            e.printStackTrace();
        }
    }

    // ===================== EXIT =====================

    @Override
    public void exitApplication() {
        System.out.println("\n👋 Thanks for using the Budget App. Goodbye!");
        System.exit(0);
    }

    // ===================== INPUT HELPERS =====================

    private int readInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (Exception e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }
    }

    private int readPositiveInt(String prompt) {
        while (true) {
            int x = readInt(prompt);
            if (x > 0) return x;
            System.out.println("⚠️ Must be greater than 0.");
        }
    }

    private double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double x = Double.parseDouble(scanner.nextLine().trim());
                if (x > 0) return x;
                System.out.println("⚠️ Must be greater than 0.");
            } catch (Exception e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }
    }

    private double readNonNegativeDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double x = Double.parseDouble(scanner.nextLine().trim());
                if (x >= 0) return x;
                System.out.println("⚠️ Must be 0 or more.");
            } catch (Exception e) {
                System.out.println("❌ Invalid number. Try again.");
            }
        }
    }

    private String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String s = scanner.nextLine().trim();
            if (!s.isEmpty()) return s;
            System.out.println("⚠️ Input can't be empty!");
        }
    }
}
