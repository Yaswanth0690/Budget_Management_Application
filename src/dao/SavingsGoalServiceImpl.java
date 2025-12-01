package dao.impl;

import dao.SavingsGoalService;
import entity.User;
import util.DBConnUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class SavingsGoalServiceImpl implements SavingsGoalService {

    @Override
    public void setSavingsGoal(User user, double amount, int months) {
        double monthlyRequired = calculateMonthlySavingsRequired(amount, months);

        String sql = "INSERT INTO savings_goals (user_id, amount, months) VALUES (?, ?, ?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setDouble(2, amount);
            ps.setInt(3, months);

            ps.executeUpdate();

            System.out.println("🎯 Savings goal added successfully!");
            System.out.printf("   Goal: ₹%.2f in %d months (₹%.2f per month)%n",
                    amount, months, monthlyRequired);

        } catch (SQLException e) {
            System.out.println("❌ Error saving savings goal.");
            e.printStackTrace();
        }
    }

    @Override
    public double calculateMonthlySavingsRequired(double totalGoal, int months) {
        if (months <= 0) return totalGoal;
        return totalGoal / months;
    }

    @Override
    public void displaySavingsGoal(User user) {
        String sql = "SELECT amount, months FROM savings_goals WHERE user_id = ? ORDER BY months ASC";

        System.out.println("\n📜 Your Savings Goals:");

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());

            try (ResultSet rs = ps.executeQuery()) {
                boolean found = false;

                System.out.printf("%-5s %-12s %-10s %-15s%n",
                        "#", "Goal (₹)", "Months", "Per Month (₹)");
                System.out.println("------------------------------------------------");

                int index = 1;
                while (rs.next()) {
                    found = true;
                    double amount = rs.getDouble("amount");
                    int months = rs.getInt("months");
                    double monthly = calculateMonthlySavingsRequired(amount, months);

                    System.out.printf("%-5d %-12.2f %-10d %-15.2f%n",
                            index++, amount, months, monthly);
                }

                if (!found) {
                    System.out.println("ℹ️ You have no savings goals yet.");
                }

            }

        } catch (SQLException e) {
            System.out.println("❌ Error loading savings goals.");
            e.printStackTrace();
        }
    }
}
