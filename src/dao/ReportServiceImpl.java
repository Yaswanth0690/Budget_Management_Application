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
        String sql = "SELECT amount, expense_date, description " +
                "FROM expenses WHERE user_id = ? AND category_id = ? " +
                "ORDER BY expense_date DESC";

        List<Expense> list = new ArrayList<>();

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, Integer.parseInt(category.getCategoryId()));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Expense e = new Expense();
                    e.setAmount(rs.getDouble("amount"));
                    e.setDate(rs.getDate("expense_date").toLocalDate());
                    e.setCategory(category);
                    e.setDescription(rs.getString("description"));
                    list.add(e);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void displayExpenseReport(User user) {
        String sql = "SELECT c.name AS category, SUM(e.amount) AS total " +
                "FROM expenses e JOIN categories c ON e.category_id = c.id " +
                "WHERE e.user_id = ? " +
                "GROUP BY c.name ORDER BY total DESC";

        System.out.println("==== Expense Report ====");

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String cat = rs.getString("category");
                    double total = rs.getDouble("total");
                    System.out.printf("%-15s : %.2f%n", cat, total);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void analyzeSpendingPatterns(User user) {
        // Simple example: show last 7 days vs previous 7 days
        System.out.println("==== Spending Analysis (simple) ====");

        String sql = "SELECT " +
                "  CASE WHEN expense_date >= CURDATE() - INTERVAL 7 DAY THEN 'Last 7 Days' " +
                "       WHEN expense_date >= CURDATE() - INTERVAL 14 DAY THEN 'Previous 7 Days' " +
                "  END AS period, " +
                "  SUM(amount) AS total " +
                "FROM expenses " +
                "WHERE user_id = ? " +
                "  AND expense_date >= CURDATE() - INTERVAL 14 DAY " +
                "GROUP BY period";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("%s : %.2f%n",
                            rs.getString("period"),
                            rs.getDouble("total"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void recommendSavingsAreas(User user) {
        // Very simple: list top 3 categories by spend this month
        String sql = "SELECT c.name AS category, SUM(e.amount) AS total " +
                "FROM expenses e JOIN categories c ON e.category_id = c.id " +
                "WHERE e.user_id = ? " +
                "  AND YEAR(e.expense_date) = YEAR(CURDATE()) " +
                "  AND MONTH(e.expense_date) = MONTH(CURDATE()) " +
                "GROUP BY c.name ORDER BY total DESC LIMIT 3";

        System.out.println("==== Suggested Areas to Cut Spending ====");

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.printf("- %s (%.2f)%n",
                            rs.getString("category"),
                            rs.getDouble("total"));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
