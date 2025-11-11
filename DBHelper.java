import java.sql.*;

public class DBHelper {
    private static final String URL = "jdbc:mysql://localhost:3306/ecommercesimple?serverTimezone=UTC";
    private static final String USER = "root";
    private static final String PASS = "root"; // <<-- change this

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASS);
    }

    // optional quick test
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            try (Connection c = getConnection()) {
                System.out.println("DB connected: " + (c != null && !c.isClosed()));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
