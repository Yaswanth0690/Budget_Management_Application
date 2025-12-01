package dao.impl;

import dao.BudgetService;
import entity.Budget;
import entity.Category;
import entity.User;
import util.DBConnUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BudgetServiceImpl implements BudgetService {

    @Override
    public void setMonthlyBudget(User user, Category category, double amount) {
        String sql = "INSERT INTO budgets (user_id, category_id, monthly_amount) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE monthly_amount = VALUES(monthly_amount)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, category.getId());
            ps.setDouble(3, amount);
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getTotalBudget(User user) {
        String sql = "SELECT SUM(monthly_amount) AS total FROM budgets WHERE user_id = ?";
        double total = 0.0;

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    total = rs.getDouble("total");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return total;
    }

    @Override
    public List<Budget> getAllBudgets(User user) {
        String sql = "SELECT b.category_id, b.monthly_amount, c.name " +
                "FROM budgets b JOIN categories c ON b.category_id = c.id " +
                "WHERE b.user_id = ?";

        List<Budget> budgets = new ArrayList<>();

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Category category = new Category();
                    int catId = rs.getInt("category_id");
                    category.setId(catId);
                    category.setCategoryId(String.valueOf(catId));
                    category.setCategoryName(rs.getString("name"));

                    Budget budget = new Budget();
                    budget.setCategory(category);
                    budget.setMonthlyAmount(rs.getDouble("monthly_amount"));

                    budgets.add(budget);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return budgets;
    }

    @Override
    public void updateBudget(User user, Category category, double newAmount) {
        String sql = "UPDATE budgets SET monthly_amount = ? WHERE user_id = ? AND category_id = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newAmount);
            ps.setInt(2, user.getId());
            ps.setInt(3, category.getId());
            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getCategoryBudget(User user, Category category) {
        String sql = "SELECT monthly_amount FROM budgets WHERE user_id = ? AND category_id = ?";
        double amount = 0.0;

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, category.getId());
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    amount = rs.getDouble("monthly_amount");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return amount;
    }

    @Override
    public boolean removeBudget(User user, Category category) {
        String sql = "DELETE FROM budgets WHERE user_id = ? AND category_id = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, category.getId());
            int rows = ps.executeUpdate();

            return rows > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}
