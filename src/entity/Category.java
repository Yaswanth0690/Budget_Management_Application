package entity;

public class Category {

    private int id;                // DB Primary Key
    private String categoryId;     // Stored as string in DB operations
    private String categoryName;

    public Category() {}

    public Category(int id, String categoryId, String categoryName) {
        this.id = id;
        this.categoryId = categoryId;
        this.categoryName = categoryName;
    }

    // Getters & Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getCategoryId() { return categoryId; }
    public void setCategoryId(String categoryId) { this.categoryId = categoryId; }

    public String getCategoryName() { return categoryName; }
    public void setCategoryName(String categoryName) { this.categoryName = categoryName; }

    @Override
    public String toString() {
        return categoryName + " (ID: " + categoryId + ")";
    }
}
