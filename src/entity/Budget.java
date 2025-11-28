package entity;

public class Budget {

    private Category category;
    private double monthlyAmount;

    public Budget() {}

    public Budget(Category category, double monthlyAmount) {
        this.category = category;
        this.monthlyAmount = monthlyAmount;
    }

    // Getters & Setters
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public double getMonthlyAmount() { return monthlyAmount; }
    public void setMonthlyAmount(double monthlyAmount) { this.monthlyAmount = monthlyAmount; }

    @Override
    public String toString() {
        return category.getCategoryName() + ": " + monthlyAmount;
    }
}
