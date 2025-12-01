package main;

import dao.*;
import dao.impl.*;
import entity.Budget;
import entity.Category;
import entity.User;

import java.util.List;
import java.util.Scanner;

public class Main {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {

        // Service initialization
        UserService userService = new UserServiceImpl();
        CategoryService categoryService = new CategoryServiceImpl();
        BudgetService budgetService = new BudgetServiceImpl();
        ExpenseService expenseService = new ExpenseServiceImpl();
        AlertService alertService = new AlertServiceImpl(budgetService, expenseService);
        ReportService reportService = new ReportServiceImpl();
        SavingsGoalService savingsGoalService = new SavingsGoalServiceImpl();
        LoanService loanService = new LoanServiceImpl();

        ApplicationService appService = new ApplicationServiceImpl(
                budgetService,
                expenseService,
                alertService,
                reportService,
                savingsGoalService,
                loanService,
                categoryService
        );

        System.out.println("=======================================");
        System.out.println("💰  BUDGET MANAGEMENT APPLICATION");
        System.out.println("=======================================\n");

        System.out.print("🧾 Are you a new user? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        User user;
        boolean isNewUser = false;

        if (response.equals("yes")) {
            user = userService.promptForUserName();
            isNewUser = true;
        } else {
            System.out.print("🔐 Enter your User ID: ");
            String userId = scanner.nextLine().trim();

            if (userService.isNewUser(userId)) {
                System.out.println("⚠️ No user found with that ID. Creating a new profile...");
                user = userService.promptForUserName();
                isNewUser = true;
            } else {
                user = userService.retrieveUserData(userId);
                System.out.println("\n👋 Welcome back, " + user.getUserName() + "!");
            }
        }

        if (user == null) {
            System.out.println("❌ User setup failed. Exiting...");
            return;
        }

        // Check if user already has budgets
        List<Budget> existingBudgets = budgetService.getAllBudgets(user);
        boolean hasBudgets = existingBudgets != null && !existingBudgets.isEmpty();

        // Show existing budgets for returning users
        if (!isNewUser && hasBudgets) {
            System.out.println("\n📊 Your current budgets:");
            System.out.println("--------------------------------");
            double total = 0.0;
            for (Budget b : existingBudgets) {
                Category c = b.getCategory();
                String name = (c != null) ? c.getCategoryName() : "Unknown";
                System.out.printf("• %-12s : ₹%.2f%n", name, b.getMonthlyAmount());
                total += b.getMonthlyAmount();
            }
            System.out.println("--------------------------------");
            System.out.printf("🧮 TOTAL        : ₹%.2f%n", total);
        }

        // If new user OR no budgets → force setup
        if (isNewUser || !hasBudgets) {
            System.out.println("\n🛠 You don't have any budgets yet. Let's set them up.");
            setupInitialBudgets(user, budgetService, categoryService);
        } else {
            System.out.print("\n🔁 Do you want to review or change your budgets? (yes/no): ");
            String ans = scanner.nextLine().trim().toLowerCase();
            if (ans.equals("yes")) {
                setupInitialBudgets(user, budgetService, categoryService);
            }
        }

        // Go to main menu
        appService.showMainMenu(user);
    }

    private static void setupInitialBudgets(
            User user,
            BudgetService budgetService,
            CategoryService categoryService
    ) {

        System.out.println("\n📅 --- Set Up / Edit Your Monthly Budgets ---");

        while (true) {
            List<Category> categories = categoryService.getAllCategories();

            if (categories.isEmpty()) {
                System.out.println("⚠️ No categories found. Please add one.");
                System.out.print("➕ Enter new category name: ");
                String newCat = scanner.nextLine().trim();
                if (!newCat.isEmpty()) {
                    categoryService.addCategory(newCat);
                } else {
                    System.out.println("❌ Category name cannot be empty.");
                }
                continue;
            }

            System.out.println("\n📂 Available Categories:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i).getCategoryName());
            }
            System.out.println("0. ➕ Add NEW Category");

            System.out.print("➡ Choose category (number) or 0 to add new (or -1 to stop): ");
            int selection;
            try {
                selection = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
                continue;
            }

            if (selection == -1) {
                break;
            }

            if (selection == 0) {
                System.out.print("➕ Enter new category name: ");
                String newCat = scanner.nextLine().trim();
                if (!newCat.isEmpty()) {
                    categoryService.addCategory(newCat);
                } else {
                    System.out.println("❌ Category name cannot be empty.");
                }
                continue; // reload list
            }

            if (selection < 1 || selection > categories.size()) {
                System.out.println("❌ Invalid choice. Try again.");
                continue;
            }

            Category chosenCategory = categories.get(selection - 1);

            System.out.print("💵 Enter MONTHLY budget for " +
                    chosenCategory.getCategoryName() + ": ");
            double amount;
            try {
                amount = Double.parseDouble(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid amount. Try again.");
                continue;
            }

            budgetService.setMonthlyBudget(user, chosenCategory, amount);

            double totalBudget = budgetService.getTotalBudget(user);
            System.out.println("✅ Budget set for category " + chosenCategory.getCategoryName());
            System.out.println("🧮 Updated TOTAL monthly budget: ₹" + totalBudget);

            System.out.print("🔁 Do you want to set/change another budget? (yes/no): ");
            String ans = scanner.nextLine().trim().toLowerCase();
            if (!ans.equals("yes")) {
                break;
            }
        }

        System.out.println("\n✨ Budget setup/review complete.");
        System.out.println("----------------------------------\n");
    }
}
