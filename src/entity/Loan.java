package entity;

public class Loan {

    private double principal;
    private double interestRate;
    private int repaymentMonths;
    private double monthlyRepayment;

    public Loan() {}

    public Loan(double principal, double interestRate, int repaymentMonths, double monthlyRepayment) {
        this.principal = principal;
        this.interestRate = interestRate;
        this.repaymentMonths = repaymentMonths;
        this.monthlyRepayment = monthlyRepayment;
    }

    public double getPrincipal() { return principal; }
    public void setPrincipal(double principal) { this.principal = principal; }

    public double getInterestRate() { return interestRate; }
    public void setInterestRate(double interestRate) { this.interestRate = interestRate; }

    public int getRepaymentMonths() { return repaymentMonths; }
    public void setRepaymentMonths(int repaymentMonths) {
        this.repaymentMonths = repaymentMonths;
    }

    public double getMonthlyRepayment() { return monthlyRepayment; }
    public void setMonthlyRepayment(double monthlyRepayment) {
        this.monthlyRepayment = monthlyRepayment;
    }

    @Override
    public String toString() {
        return "Loan: " + principal + " @ " + interestRate + "% for " +
                repaymentMonths + " months (" + monthlyRepayment + "/month)";
    }
}
