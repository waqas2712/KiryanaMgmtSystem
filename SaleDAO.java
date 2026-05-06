import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


class SaleDAO {
	private Sale buildSaleFromResultSet ( ResultSet rs ) throws SQLException{
			Sale s = new Sale ();
			s.setSaleId (rs.getInt("sale_id"));
			s.setCustomerName(rs.getString ("customer_name"));
			s.setAmount(rs.getDouble("amount"));
			s.setStatus(rs.getString("status"));

			Timestamp ts = rs.getTimestamp("sale_date");
			if (ts != null ) {
				s.setSaleDate(ts.toLocalDateTime());
			}
			return s;
	}

public int saveSale(Sale sale) {
        String sql = "INSERT INTO sales (customer_name, amount, sale_date, status) "
                   + "VALUES (?, ?, ?, ?)";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
 
            PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
 
            ps.setString(1, sale.getCustomerName()); // can be null for cash sales
            ps.setDouble(2, sale.getAmount());
 
            // Convert LocalDateTime to java.sql.Timestamp for JDBC
            ps.setTimestamp(3, Timestamp.valueOf(sale.getSaleDate()));
            ps.setString(4, sale.getStatus());
 
            int rowsAffected = ps.executeUpdate();
 
            if (rowsAffected > 0) {
                // Get the auto-generated ID back from the database
                ResultSet generatedKeys = ps.getGeneratedKeys();
                if (generatedKeys.next()) {
                    return generatedKeys.getInt(1); // this is the new sale_id
                }
            }
 
        } catch (SQLException e) {
            System.out.println("Error saving sale: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return -1; // means the insert failed
    }



     public ArrayList<Sale> getRecentSales() {
        ArrayList<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY sale_date DESC LIMIT 10";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                Sale s = buildSaleFromResultSet(rs);
                list.add(s);
            }
 
        } catch (SQLException e) {
            System.out.println("Error fetching recent sales: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return list;
    }
    public ArrayList<Sale> getAllSales() {
        ArrayList<Sale> list = new ArrayList<>();
        String sql = "SELECT * FROM sales ORDER BY sale_date DESC";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                Sale s = buildSaleFromResultSet(rs);
                list.add(s);
            }
 
        } catch (SQLException e) {
            System.out.println("Error fetching all sales: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return list;
    }





    public Sale getSaleById(int saleId) {
        String sql = "SELECT * FROM sales WHERE sale_id = ?";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, saleId);
 
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildSaleFromResultSet(rs);
            }
 
        } catch (SQLException e) {
            System.out.println("Error fetching sale by ID: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return null;
    }


  

    private void closeConnection(Connection con) {
        if (con != null) {
            try {
                con.close();
            } catch (SQLException e) {
                System.out.println("Error closing connection: " + e.getMessage());
            }
        }
    }


}