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
            ps.setInt(2, Integer.parseInt(category.getCategoryId()));
            ps.setDouble(3, amount);

            ps.executeUpdate();
            System.out.println("Budget set for category " + category.getCategoryName());

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double getTotalBudget(User user) {
        String sql = "SELECT COALESCE(SUM(monthly_amount), 0) AS total " +
                "FROM budgets WHERE user_id = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());

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
    public List<Budget> getAllBudgets(User user) {
        String sql = "SELECT b.category_id, b.monthly_amount, c.name " +
                "FROM budgets b JOIN categories c ON b.category_id = c.id " +
                "WHERE b.user_id = ?";

        List<Budget> list = new ArrayList<>();

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Budget budget = new Budget();
                    Category cat = new Category();
                    cat.setCategoryId(String.valueOf(rs.getInt("category_id")));
                    cat.setCategoryName(rs.getString("name"));

                    budget.setCategory(cat);
                    budget.setMonthlyAmount(rs.getDouble("monthly_amount"));

                    list.add(budget);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void updateBudget(User user, Category category, double newAmount) {
        setMonthlyBudget(user, category, newAmount);
    }

    @Override
    public double getCategoryBudget(User user, Category category) {
        String sql = "SELECT monthly_amount FROM budgets " +
                "WHERE user_id = ? AND category_id = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setInt(2, Integer.parseInt(category.getCategoryId()));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("monthly_amount");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
