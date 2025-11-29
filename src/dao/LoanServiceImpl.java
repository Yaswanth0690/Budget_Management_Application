package dao.impl;

import dao.LoanService;
import entity.User;
import util.DBConnUtil;

import java.sql.*;

public class LoanServiceImpl implements LoanService {

    @Override
    public void addLoan(User user, double amount, double interestRate, int months) {
        double monthlyRepayment = calculateMonthlyRepayment(amount, interestRate, months);

        String sql = "INSERT INTO loans (user_id, principal, interest_rate, repayment_months, monthly_repayment) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, user.getId());
            ps.setDouble(2, amount);
            ps.setDouble(3, interestRate);
            ps.setInt(4, months);
            ps.setDouble(5, monthlyRepayment);

            ps.executeUpdate();
            System.out.println("Loan added. Monthly repayment: " + monthlyRepayment);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public double calculateMonthlyRepayment(double amount, double interestRate, int months) {
        // simple amortization formula
        if (months <= 0) return 0.0;

        double monthlyRate = interestRate / 100.0 / 12.0;
        if (monthlyRate == 0) {
            return amount / months;
        }

        double numerator = monthlyRate * amount;
        double denominator = 1 - Math.pow(1 + monthlyRate, -months);

        return numerator / denominator;
    }

    @Override
    public void deductRepaymentFromBudget(User user, double repaymentAmount) {
        // For simplicity just print for now; you could:
        // - Decrease some "available budget" field
        // - Record it as an expense in a "Loan Repayment" category
        System.out.println("Repayment of " + repaymentAmount +
                " should be considered in your monthly budget (not yet persisted).");
    }
}
