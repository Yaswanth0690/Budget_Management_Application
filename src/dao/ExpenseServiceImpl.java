package dao.impl;

import dao.ExpenseService;
import entity.Category;
import entity.Expense;
import entity.User;
import util.DBConnUtil;

import java.sql.*;
import java.time.LocalDate;

public class ExpenseServiceImpl implements ExpenseService {

    @Override
    public void recordExpense(User user, double amount, String dateStr, Category category) {
        String sql = "INSERT INTO expenses (user_id, category_id, amount, expense_date, description) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            LocalDate date = LocalDate.parse(dateStr); // expecting YYYY-MM-DD

            ps.setInt(1, user.getId());
            ps.setInt(2, Integer.parseInt(category.getCategoryId()));
            ps.setDouble(3, amount);
            ps.setDate(4, Date.valueOf(date));
            ps.setString(5, "Expense"); // simple default description

            ps.executeUpdate();
            System.out.println("Expense recorded successfully.");

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to record expense.");
        }
    }

    @Override
    public double getTotalSpentToday(User user, Category category) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total " +
                "FROM expenses WHERE user_id = ? AND category_id = ? " +
                "AND expense_date = CURDATE()";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, Integer.parseInt(category.getCategoryId()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public double getTotalSpentThisMonth(User user, Category category) {
        String sql = "SELECT COALESCE(SUM(amount), 0) AS total " +
                "FROM expenses WHERE user_id = ? AND category_id = ? " +
                "AND YEAR(expense_date) = YEAR(CURDATE()) " +
                "AND MONTH(expense_date) = MONTH(CURDATE())";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, Integer.parseInt(category.getCategoryId()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }

    @Override
    public void saveExpense(User user, Expense expense) {
        recordExpense(user, expense.getAmount(), expense.getDate().toString(), expense.getCategory());
    }
}
