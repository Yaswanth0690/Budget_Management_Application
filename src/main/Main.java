package main;

import dao.*;
import dao.impl.*;
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

        System.out.println("========== Budget Management Application ==========");

        System.out.print("Are you a new user? (yes/no): ");
        String response = scanner.nextLine().trim().toLowerCase();

        User user;
        boolean isNewUser = false;

        if (response.equals("yes")) {
            user = userService.promptForUserName();
            isNewUser = true;
        } else {
            System.out.print("Enter your User ID: ");
            String userId = scanner.nextLine();

            if (userService.isNewUser(userId)) {
                System.out.println("⚠️ User not found. Creating new user...");
                user = userService.promptForUserName();
                isNewUser = true;
            } else {
                user = userService.retrieveUserData(userId);
                System.out.println("Welcome back, " + user.getUserName() + "!");
            }
        }

        if (user == null) {
            System.out.println("❌ User setup failed. Exiting...");
            return;
        }

        // Check if user already has budgets
        boolean hasBudgets = !budgetService.getAllBudgets(user).isEmpty();

        if (isNewUser || !hasBudgets) {
            System.out.println("\nYou don't have any budgets yet. Let's set them up.");
            setupInitialBudgets(user, budgetService, categoryService);
        } else {
            System.out.print("\nDo you want to review or change your budgets? (yes/no): ");
            String ans = scanner.nextLine().trim().toLowerCase();
            if (ans.equals("yes")) {
                setupInitialBudgets(user, budgetService, categoryService);
            }
        }

        // Then go to the main menu
        appService.showMainMenu(user);
    }

    /**
     * Handles categories and budget setup:
     * - If DB is empty: allow category creation
     * - Otherwise: show category list and assign budgets
     */
    private static void setupInitialBudgets(
            User user,
            BudgetService budgetService,
            CategoryService categoryService
    ) {

        System.out.println("\n--- Set Up Your Monthly Budgets ---");

        while (true) {
            List<Category> categories = categoryService.getAllCategories();

            if (categories.isEmpty()) {
                System.out.println("⚠️ No categories found! Add one below:");
                System.out.print("Enter category name: ");
                String newCat = scanner.nextLine();
                categoryService.addCategory(newCat);
                System.out.println("Category '" + newCat + "' added.");
                continue; // re-loop, now categories should exist
            }

            System.out.println("\nAvailable Categories:");
            for (int i = 0; i < categories.size(); i++) {
                System.out.println((i + 1) + ". " + categories.get(i).getCategoryName());
            }
            System.out.println("0. ➕ Add NEW Category");

            System.out.print("Choose category (number): ");
            int selection;
            try {
                selection = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Invalid input. Please enter a number.");
                continue;
            }

            if (selection == 0) {
                System.out.print("Enter new category name: ");
                String newCat = scanner.nextLine();
                categoryService.addCategory(newCat);
                System.out.println("Category '" + newCat + "' added.");
                continue; // refresh list
            }

            if (selection < 1 || selection > categories.size()) {
                System.out.println("❌ Invalid option. Try again.");
                continue;
            }

            Category chosenCategory = categories.get(selection - 1);

            System.out.print("Enter MONTHLY budget for " +
                    chosenCategory.getCategoryName() + ": ");
            double amount;
            try {
                amount = Double.parseDouble(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number.");
                continue;
            }

            budgetService.setMonthlyBudget(user, chosenCategory, amount);

            double total = budgetService.getTotalBudget(user);
            System.out.println("📌 Updated TOTAL monthly budget: " + total);

            System.out.print("Do you want to set another budget? (yes/no): ");
            String ans = scanner.nextLine().trim().toLowerCase();
            if (!ans.equals("yes")) {
                break;
            }
        }

        System.out.println("\n✨ Budget setup complete!");
        System.out.println("----------------------------------\n");
    }
}
