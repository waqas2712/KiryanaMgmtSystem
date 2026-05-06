import java.sql.*;
import java.util.ArrayList;

public class SaleItemDAO {
    public boolean saveSaleItem(SaleItem item) {
        String sql = "INSERT INTO item_per_sale (sale_id, product_id, quantity_sold, sub_total) "
                   + "VALUES (?, ?, ?, ?)";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
 
            ps.setInt(1, item.getSaleId());
            ps.setInt(2, item.getProductId());
            ps.setDouble(3, item.getQtySold());
            ps.setDouble(4, item.getSubTotal());
 
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
 
        } catch (SQLException e) {
            System.out.println("Error saving sale item: " + e.getMessage());
            return false;
        } finally {
            closeConnection(con);
        }
    }
 
   
    public ArrayList<SaleItem> getItemsBySale(int saleId) {
        ArrayList<SaleItem> list = new ArrayList<>();
 
        // JOIN to get product name along with item details
        String sql = "SELECT i.*, p.product_name, p.unit_type, "
                   + "p.price_per_unit, p.cost_per_unit "
                   + "FROM item_per_sale i "
                   + "JOIN product p ON i.product_id = p.product_id "
                   + "WHERE i.sale_id = ?";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, saleId);
 
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                // Build the Product from the joined columns
                Product p = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("unit_type"),
                    rs.getDouble("price_per_unit"),
                    rs.getDouble("cost_per_unit")
                );
                 SaleItem item = new SaleItem(
                    rs.getInt("sale_id"),
                    p,
                    rs.getDouble("quantity_sold"),
                    rs.getDouble("sub_total")
                );
 
                list.add(item);
            }
 
        } catch (SQLException e) {
            System.out.println("Error fetching sale items: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return list;
    }
 
    // -------------------------------------------------------
    // PRIVATE HELPER
    // -------------------------------------------------------
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