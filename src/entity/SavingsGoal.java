package entity;

public class SavingsGoal {

    private double amount;
    private int timeframeMonths;

    public SavingsGoal() {}

    public SavingsGoal(double amount, int timeframeMonths) {
        this.amount = amount;
        this.timeframeMonths = timeframeMonths;
    }

    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public int getTimeframeMonths() { return timeframeMonths; }
    public void setTimeframeMonths(int timeframeMonths) {
        this.timeframeMonths = timeframeMonths;
    }

    @Override
    public String toString() {
        return amount + " in " + timeframeMonths + " months";
    }
}
