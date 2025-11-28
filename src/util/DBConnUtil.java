package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnUtil {

    private static final String URL = "jdbc:mysql://localhost:3306/budget_app";
    private static final String USER = "root";
    private static final String PASSWORD = "Yash@123";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver"); // For MySQL 8+
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
