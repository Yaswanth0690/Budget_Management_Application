package dao.impl;

import dao.ReportService;
import entity.Category;
import entity.Expense;
import entity.User;
import util.DBConnUtil;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReportServiceImpl implements ReportService {

    @Override
    public List<Expense> getExpensesByCategory(User user, Category category) {
        List<Expense> expenses = new ArrayList<>();
        String sql = "SELECT id, amount, expense_date, category_id, description " +
                "FROM expenses WHERE user_id = ? AND category_id = ? " +
                "ORDER BY expense_date DESC";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, category.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Expense e = new Expense();
                    e.setAmount(rs.getDouble("amount"));
                    Date d = rs.getDate("expense_date");
                    if (d != null) {
                        e.setDate(d.toLocalDate());
                    }
                    e.setDescription(rs.getString("description"));

                    Category c = new Category();
                    c.setId(rs.getInt("category_id"));
                    e.setCategory(c);

                    expenses.add(e);
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error loading expenses.");
            e.printStackTrace();
        }

        return expenses;
    }

    @Override
    public void displayExpenseReport(User user) {
        LocalDate firstOfMonth = LocalDate.now().withDayOfMonth(1);

        String sql = "SELECT c.id, c.name, " +
                "       COALESCE(b.monthly_amount, 0) AS budget, " +
                "       COALESCE(SUM(e.amount), 0)    AS spent " +
                "FROM categories c " +
                "LEFT JOIN budgets b " +
                "  ON b.category_id = c.id AND b.user_id = ? " +
                "LEFT JOIN expenses e " +
                "  ON e.category_id = c.id AND e.user_id = ? AND e.expense_date >= ? " +
                "GROUP BY c.id, c.name, b.monthly_amount " +
                "ORDER BY c.name ASC";

        System.out.println();
        System.out.println("===== 📊 EXPENSE REPORT (This Month) =====");
        System.out.printf("%-12s %-10s %-10s %-12s %-12s%n",
                "Category", "Budget", "Spent", "Remaining", "Status");
        System.out.println("--------------------------------------------------------------");

        double totalBudget = 0.0;
        double totalSpent = 0.0;

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, user.getId());
            ps.setDate(3, Date.valueOf(firstOfMonth));

            try (ResultSet rs = ps.executeQuery()) {
                boolean anyRow = false;

                while (rs.next()) {
                    anyRow = true;
                    String name = rs.getString("name");
                    double budget = rs.getDouble("budget");
                    double spent = rs.getDouble("spent");
                    double remaining = budget - spent;

                    totalBudget += budget;
                    totalSpent += spent;

                    String statusEmoji;
                    String statusText;

                    if (budget == 0 && spent == 0) {
                        statusEmoji = "•";
                        statusText = "No activity";
                    } else if (budget == 0 && spent > 0) {
                        statusEmoji = "⚠️";
                        statusText = "No budget";
                    } else if (remaining < 0) {
                        statusEmoji = "❌";
                        statusText = "Exceeded";
                    } else if (remaining == 0) {
                        statusEmoji = "⚠️";
                        statusText = "At limit";
                    } else {
                        statusEmoji = "✅";
                        statusText = "OK";
                    }

                    System.out.printf("%-12s %-10.2f %-10.2f %-12.2f %-2s %-10s%n",
                            name, budget, spent, remaining, statusEmoji, statusText);
                }

                if (!anyRow) {
                    System.out.println("ℹ️ No categories or expenses found yet.");
                }

            }

        } catch (SQLException e) {
            System.out.println("❌ Error generating report.");
            e.printStackTrace();
        }

        System.out.println("--------------------------------------------------------------");
        double remainingTotal = totalBudget - totalSpent;
        System.out.printf("🧮 TOTAL BUDGET : %.2f%n", totalBudget);
        System.out.printf("💸 TOTAL SPENT  : %.2f%n", totalSpent);
        System.out.printf("💼 TOTAL REMAIN : %.2f%n", remainingTotal);
    }

    @Override
    public void analyzeSpendingPatterns(User user) {
        System.out.println("🔍 Spending analysis feature not fully implemented yet.");
    }

    @Override
    public void recommendSavingsAreas(User user) {
        System.out.println("💡 Savings recommendation feature not fully implemented yet.");
    }
}
