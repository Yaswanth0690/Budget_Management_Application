package entity;

import java.time.LocalDate;

public class Expense {

    private double amount;
    private LocalDate date;
    private Category category;
    private String description;

    public Expense() {}

    public Expense(double amount, LocalDate date, Category category, String description) {
        this.amount = amount;
        this.date = date;
        this.category = category;
        this.description = description;
    }

    // Getters & Setters
    public double getAmount() { return amount; }
    public void setAmount(double amount) { this.amount = amount; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    @Override
    public String toString() {
        return date + " - " + amount + " (" + category.getCategoryName() + ")";
    }
}
