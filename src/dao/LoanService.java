package dao;
import entity.User;
import entity.Loan;

public interface LoanService {
    void addLoan(User user, double amount, double interestRate, int months);
    double calculateMonthlyRepayment(double amount, double interestRate, int months);
    void deductRepaymentFromBudget(User user, double repaymentAmount);
}
