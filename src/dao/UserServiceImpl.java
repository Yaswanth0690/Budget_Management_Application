package dao.impl;

import dao.UserService;
import entity.User;
import util.DBConnUtil;

import java.sql.*;
import java.util.Scanner;

public class UserServiceImpl implements UserService {

    // simple console input here (you can refactor later to ConsoleUtils)
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public boolean isNewUser(String userId) {
        String sql = "SELECT COUNT(*) FROM users WHERE user_uid = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return true; // assume new on error
    }

    @Override
    public User promptForUserName() {
        System.out.print("Enter your name: ");
        String name = scanner.nextLine().trim();

        String generatedId = generateUserId(name);

        User user = new User();
        user.setUserId(generatedId);
        user.setUserName(name);

        // insert into DB
        String sql = "INSERT INTO users (user_uid, user_name) VALUES (?, ?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, generatedId);
            ps.setString(2, name);
            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println("Your generated User ID is: " + generatedId);
        return user;
    }

    @Override
    public String generateUserId(String userName) {
        // very simple: first 3 letters of name + current millis (trim spaces, uppercase)
        String base = userName.trim().replaceAll("\\s+", "").toUpperCase();
        if (base.length() > 3) {
            base = base.substring(0, 3);
        }
        return base + System.currentTimeMillis();
    }

    @Override
    public User retrieveUserData(String userId) {
        String sql = "SELECT id, user_uid, user_name FROM users WHERE user_uid = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUserId(rs.getString("user_uid"));
                    user.setUserName(rs.getString("user_name"));
                    return user;
                } else {
                    System.out.println("No user found with ID: " + userId);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}
