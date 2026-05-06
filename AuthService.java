import java.sql.*;
public class AuthService {
 

    public User login(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
 
            ps.setString(1, username);
            ps.setString(2, password);
 
            ResultSet rs = ps.executeQuery();
 
            if (rs.next()) {
                // A matching row was found — login is successful
                User user = new User();
                user.setUserId(rs.getInt("user_id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                return user;
            }
 
        } catch (SQLException e) {
            System.out.println("Login error: " + e.getMessage());
        } finally {
            if (con != null) {
                try { con.close(); } catch (SQLException e) { }
            }
        }
 
        // No matching user found — login failed
        return null;
    }
}