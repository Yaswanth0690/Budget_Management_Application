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
        String sql = "SELECT id, name FROM categories ORDER BY id";
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
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public void addCategory(String name) {
        String sql = "INSERT INTO categories (name) VALUES (?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, name);
            ps.executeUpdate();
            System.out.println("Category '" + name + "' added.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
