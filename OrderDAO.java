import java.sql.*;

public class OrderDAO {
    public static boolean placeOrder(String userEmail, String productName, double total) {
        String sql = "INSERT INTO orders(user_email, product_name, total_price) VALUES(?,?,?)";
        try (Connection con = DBHelper.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, userEmail);
            ps.setString(2, productName);
            ps.setDouble(3, total);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
