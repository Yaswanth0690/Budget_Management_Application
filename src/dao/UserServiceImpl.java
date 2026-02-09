package dao.impl;

import dao.UserService;
import entity.User;
import util.DBConnUtil;

import java.sql.*;
import java.util.Scanner;

public class UserServiceImpl implements UserService {

    private final Scanner scanner = new Scanner(System.in);

    // ------------------------------
    // CHECK IF USER EXISTS
    // ------------------------------
    @Override
    public boolean isNewUser(String userId) {
        return retrieveUserData(userId) == null;
    }

    // ------------------------------
    // REGISTER NEW USER
    // ------------------------------
    @Override
    public User promptForUserName() {

        System.out.println("\n👋 Welcome! Let's get you registered.");

        System.out.print("📝 Enter your name: ");
        String name = scanner.nextLine().trim();

        if (name.isEmpty()) {
            System.out.println("⚠️ Name cannot be empty.");
            return null;
        }

        System.out.print("🔒 Create a password: ");
        String password = scanner.nextLine();

        if (password.isEmpty()) {
            System.out.println("⚠️ Password cannot be empty.");
            return null;
        }

        // Generate unique User ID
        String userId = generateUserId(name);
        while (!isNewUser(userId)) {
            userId = generateUserId(name);
        }

        User newUser = new User();
        newUser.setUserName(name);
        newUser.setUserId(userId);
        newUser.setPassword(password);

        saveUserToDB(newUser);

        System.out.println("\n✅ Registration successful!");
        System.out.println("👤 Name : " + name);
        System.out.println("🆔 UserID : " + userId);
        System.out.println("💡 Please remember your User ID and Password.");

        return newUser;
    }

    // ------------------------------
    // GENERATE USER ID
    // ------------------------------
    @Override
    public String generateUserId(String userName) {

        String cleaned = userName.toUpperCase().replaceAll("[^A-Z]", "");

        if (cleaned.length() < 3) {
            cleaned = (cleaned + "XXX");
        }

        String prefix = cleaned.substring(0, 3);
        int random = (int) (Math.random() * 90 + 10);

        return prefix + random;
    }

    // ------------------------------
    // LOGIN VALIDATION
    // ------------------------------
    public User validateLogin(String userId, String password) {

        User user = retrieveUserData(userId);

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(password)) {
            return null;
        }

        return user;
    }

    // ------------------------------
    // RETRIEVE USER FROM DB
    // ------------------------------
    @Override
    public User retrieveUserData(String userId) {

        String sql = "SELECT id, user_uid, user_name, password FROM users WHERE user_uid = ?";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, userId);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {

                    User user = new User();
                    user.setId(rs.getInt("id"));
                    user.setUserId(rs.getString("user_uid"));
                    user.setUserName(rs.getString("user_name"));
                    user.setPassword(rs.getString("password"));

                    return user;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Error retrieving user.");
            e.printStackTrace();
        }

        return null;
    }

    // ------------------------------
    // SAVE USER TO DB
    // ------------------------------
    private void saveUserToDB(User user) {

        String sql = "INSERT INTO users (user_uid, user_name, password) VALUES (?, ?, ?)";

        try (Connection conn = DBConnUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, user.getUserId());
            ps.setString(2, user.getUserName());
            ps.setString(3, user.getPassword());

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
