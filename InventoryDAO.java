import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;


public class InventoryDAO {

    public boolean createInventory(Inventory inventory) {
        String sql = "INSERT INTO inventory (product_id, current_qty, low_stock_threshold, last_restock_date) "
                   + "VALUES (?, ?, ?, ?)";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, inventory.getProductId());
            ps.setDouble(2, inventory.getCurrentQty());
            ps.setDouble(3, inventory.getLowStockThreshold());
            // Store today's date as the first restock date
            ps.setDate(4, Date.valueOf(LocalDate.now()));

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error creating inventory: " + e.getMessage());
            return false;
        } finally {
            closeConnection(con);
        }
    }

    public Inventory getInventoryByProduct(int productId) {
        String sql = "SELECT * FROM inventory WHERE product_id = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, productId);

            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Inventory inv = new Inventory();
                inv.setStockId(rs.getInt("stock_id"));
                inv.setProductId(rs.getInt("product_id"));
                inv.setCurrentQty(rs.getDouble("current_qty"));
                inv.setLowStockThreshold(rs.getDouble("low_stock_threshold"));

                // rs.getDate() returns java.sql.Date, we convert to LocalDate
                Date d = rs.getDate("last_restock_date");
                if (d != null) {
                    inv.setLastRestockDate(d.toLocalDate());
                }

                return inv;
            }

        } catch (SQLException e) {
            System.out.println("Error fetching inventory: " + e.getMessage());
        } finally {
            closeConnection(con);
        }

        return null;
    }

   
    public ArrayList<Inventory> getAllInventoryWithProduct() {
        ArrayList<Inventory> list = new ArrayList<>();

        // JOIN query: combines inventory and product data
        String sql = "SELECT i.*, p.product_name, p.unit_type, "
                   + "p.price_per_unit, p.cost_per_unit "
                   + "FROM inventory i "
                   + "JOIN product p ON i.product_id = p.product_id "
                   + "ORDER BY p.product_name";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("unit_type"),
                    rs.getDouble("price_per_unit"),
                    rs.getDouble("cost_per_unit")
                );

                // Build the Inventory object
                Inventory inv = new Inventory();
                inv.setStockId(rs.getInt("stock_id"));
                inv.setProductId(rs.getInt("product_id"));
                inv.setCurrentQty(rs.getDouble("current_qty"));
                inv.setLowStockThreshold(rs.getDouble("low_stock_threshold"));
                inv.setProduct(p); // attach the product to the inventory

                Date d = rs.getDate("last_restock_date");
                if (d != null) {
                    inv.setLastRestockDate(d.toLocalDate());
                }

                list.add(inv);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching inventory list: " + e.getMessage());
        } finally {
            closeConnection(con);
        }

        return list;
    }


    public ArrayList<Inventory> getLowStockItems() {
        ArrayList<Inventory> list = new ArrayList<>();

        // Only fetch rows where stock has fallen below the threshold
        String sql = "SELECT i.*, p.product_name, p.unit_type, "
                   + "p.price_per_unit, p.cost_per_unit "
                   + "FROM inventory i "
                   + "JOIN product p ON i.product_id = p.product_id "
                   + "WHERE i.current_qty < i.low_stock_threshold "
                   + "ORDER BY i.current_qty ASC"; // lowest stock first

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Product p = new Product(
                    rs.getInt("product_id"),
                    rs.getString("product_name"),
                    rs.getString("unit_type"),
                    rs.getDouble("price_per_unit"),
                    rs.getDouble("cost_per_unit")
                );

                Inventory inv = new Inventory();
                inv.setStockId(rs.getInt("stock_id"));
                inv.setProductId(rs.getInt("product_id"));
                inv.setCurrentQty(rs.getDouble("current_qty"));
                inv.setLowStockThreshold(rs.getDouble("low_stock_threshold"));
                inv.setProduct(p);

                list.add(inv);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching low stock items: " + e.getMessage());
        } finally {
            closeConnection(con);
        }

        return list;
    }

    public boolean deductStock(int productId, double qtySold) {
        String sql = "UPDATE inventory SET current_qty = current_qty - ? "
                   + "WHERE product_id = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDouble(1, qtySold);
            ps.setInt(2, productId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error deducting stock: " + e.getMessage());
            return false;
        } finally {
            closeConnection(con);
        }
    }

    public boolean addStock(int productId, double qtyToAdd) {
        String sql = "UPDATE inventory SET current_qty = current_qty + ?, "
                   + "last_restock_date = ? "
                   + "WHERE product_id = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDouble(1, qtyToAdd);
            ps.setDate(2, Date.valueOf(LocalDate.now())); // today's date
            ps.setInt(3, productId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error adding stock: " + e.getMessage());
            return false;
        } finally {
            closeConnection(con);
        }
    }

    public boolean updateThreshold(int productId, double threshold) {
        String sql = "UPDATE inventory SET low_stock_threshold = ? WHERE product_id = ?";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setDouble(1, threshold);
            ps.setInt(2, productId);

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error updating threshold: " + e.getMessage());
            return false;
        } finally {
            closeConnection(con);
        }
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