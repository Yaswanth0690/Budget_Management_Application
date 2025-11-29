package dao.impl;

import dao.SavingsGoalService;
import entity.SavingsGoal;
import entity.User;
import util.DBConnUtil;

import java.sql.*;

public class SavingsGoalServiceImpl implements SavingsGoalService {

    @Override
    public void setSavingsGoal(User user, double amount, int months) {
        String sql = "INSERT INTO savings_goals (user_id, amount, months) " +
                "VALUES (?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE amount = VALUES(amount), months = VALUES(months)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setDouble(2, amount);
            ps.setInt(3, months);

            ps.executeUpdate();
            System.out.println("Savings goal updated.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double calculateMonthlySavingsRequired(double totalGoal, int months) {
        if (months <= 0) return 0.0;
        return totalGoal / months;
    }

    @Override
    public void displaySavingsGoal(User user) {
        String sql = "SELECT amount, months FROM savings_goals WHERE user_id = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    double amount = rs.getDouble("amount");
                    int months = rs.getInt("months");
                    double monthly = calculateMonthlySavingsRequired(amount, months);

                    System.out.println("==== Savings Goal ====");
                    System.out.printf("Total Goal : %.2f%n", amount);
                    System.out.printf("Timeframe  : %d months%n", months);
                    System.out.printf("Monthly Needed: %.2f%n", monthly);
                } else {
                    System.out.println("No savings goal set yet.");
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
