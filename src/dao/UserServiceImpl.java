package dao.impl;

import dao.UserService;
import entity.User;
import util.DBConnUtil;

import java.sql.*;
import java.util.Scanner;

public class UserServiceImpl implements UserService {

    private final Scanner scanner = new Scanner(System.in);

    @Override
    public boolean isNewUser(String userId) {
        return retrieveUserData(userId) == null;
    }

    @Override
    public User promptForUserName() {
        System.out.println("\n👋 Welcome! Let's get you registered.");
        System.out.print("📝 Enter your name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("⚠️ Name cannot be empty. Please try again.");
            return promptForUserName();
        }

        // Generate short user ID (like MAN42)
        String userId = generateUserId(name);

        // Ensure uniqueness
        while (!isNewUser(userId)) {
            userId = generateUserId(name);
        }

        User newUser = new User();
        newUser.setUserName(name);
        newUser.setUserId(userId);

        saveUserToDB(newUser);

        System.out.println("\n✅ Registration successful!");
        System.out.println("👤 Name   : " + name);
        System.out.println("🆔 UserID : " + userId);
        System.out.println("💡 Please remember this User ID for future logins.");

        return newUser;
    }

    @Override
    public String generateUserId(String userName) {
        String cleaned = userName.toUpperCase()
                .replaceAll("[^A-Z]", ""); // letters only

        if (cleaned.length() < 3) {
            cleaned = (cleaned + "XXX");
        }

        String prefix = cleaned.substring(0, 3);
        int random = (int) (Math.random() * 90 + 10); // 10–99

        return prefix + random;
    }

    @Override
    public User retrieveUserData(String userId) {
        String sql = "SELECT id, user_name, user_uid FROM users WHERE user_uid = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUserName(rs.getString("user_name"));
                    user.setUserId(rs.getString("user_uid"));
                    return user;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error retrieving user.");
            e.printStackTrace();
        }
        return null;
    }

    private void saveUserToDB(User user) {
        String sql = "INSERT INTO users (user_uid, user_name) VALUES (?, ?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUserId());
            ps.setString(2, user.getUserName());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    user.setId(keys.getInt(1));
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error saving user to database.");
            e.printStackTrace();
        }
    }
}
