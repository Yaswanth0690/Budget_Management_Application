package dao.impl;

import dao.CategoryService;
import entity.Category;
import util.DBConnUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryServiceImpl implements CategoryService {

    @Override
    public List<Category> getAllCategories() {
        String sql = "SELECT id, name FROM categories ORDER BY name ASC";
        List<Category> list = new ArrayList<>();

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Category cat = new Category();
                int id = rs.getInt("id");
                cat.setId(id);
                cat.setCategoryId(String.valueOf(id));
                cat.setCategoryName(rs.getString("name"));
                list.add(cat);
            }

        } catch (SQLException e) {
            System.out.println("❌ Error loading categories.");
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public boolean addCategory(String name) {
        if (name == null || name.trim().isEmpty()) {
            System.out.println("⚠️ Category name cannot be empty.");
            return false;
        }

        String trimmed = name.trim();

        // Check duplicate
        String checkSql = "SELECT id FROM categories WHERE LOWER(name) = LOWER(?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement checkPs = conn.prepareStatement(checkSql)) {

            checkPs.setString(1, trimmed);
            try (ResultSet rs = checkPs.executeQuery()) {
                if (rs.next()) {
                    System.out.println("ℹ️ Category '" + trimmed + "' already exists.");
                    return false;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error checking category.");
            e.printStackTrace();
            return false;
        }

        // Insert
        String insertSql = "INSERT INTO categories (name) VALUES (?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(insertSql)) {

            ps.setString(1, trimmed);
            ps.executeUpdate();
            System.out.println("✅ Category '" + trimmed + "' added.");
            return true;

        } catch (SQLException e) {
            System.out.println("❌ Error adding category.");
            e.printStackTrace();
        }

        return false;
    }
}
