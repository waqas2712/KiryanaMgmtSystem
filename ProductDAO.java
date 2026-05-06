import java.sql.*;
import java.util.ArrayList;

public class ProductDAO{
	public boolean addProduct(Product product){
		String sql = "INSERT INTO product (product_name, unit_type, price_per_unit, cost_per_unit)"+
		"values (?, ?,?,?)";
		Connection con = null;
		try {
			con = DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);

			ps.setString(1, product.getProductName());
			ps.setString (2,product.getUnitType());
			ps.setDouble (3, product.getPricePerUnit());
			ps.setDouble (4, product.getCostPerUnit());

			int affectRows = ps.executeUpdate();
			return affectRows > 0;
		} catch (SQLException e){
			System.out.println("Error adding Product: " + e.getMessage());
			return false;
		} finally {
			closeConnection(con);
		}
	}

	public ArrayList<Product> getAllProduct (){
		ArrayList<Product> list = new ArrayList <>();
		String sql = "SELECT * FROM product ORDER BY proudct_name";

		Connection con =null;

		try {
			con= DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				Product p = new Product (rs.getInt("product_id"),rs.getString("product_name"),
					rs.getString("unit_type"),
					rs.getDouble("price_per_unit"),
					rs.getDouble("cost_per_unit") );
				list.add(p);
			}
			
		} catch (SQLException e){
			System.out.println("Error Fetching products: " + e.getMessage());
		} finally{
			closeConnection(con);
		}

		return list;
	}
  public ArrayList<Product> searchProduct (String keyword){
  			ArrayList<Product> list = new ArrayList<>();
  			String sql = "SELECT * FROM product WHERE product_name LIKE ?";
  			Connection con = null; 

  			try {
  				con= DBConnection.getConnection();

  				PreparedStatement ps = con.prepareStatement(sql);
  				ps.setString (1, "%" +keyword +"%");

  				ResultSet rs = ps.executeQuery();
  				while (rs.next()){
  					Product p = new Product(rs.getInt("product_id"), rs.getString("product_name"),

  							rs.getString("unit_type"),
  							rs.getDouble("price_per_unit"),
  							rs.getDouble("cost_per_unit")

  						);
  					list.add(p);
  				}
            } catch (SQLException e){
            	System.out.println ("Error searching Product: " + e.getMessage());
            }finally{
            	closeConnection(con);
            }
            return list;
    }

    public Product getProductbyId (int productId){

    	String sql = "SELECT * FROM product WHERE product_id = ?";

    	Connection con = null;
    	try {
    		con = DBConnection.getConnection();
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setInt(1, productId);

    		ResultSet rs = ps.executeQuery();

    		if (rs.next()){
    			return new Product (
    				   rs.getInt("product_id"), 
    				   rs.getString("product_name"),
  							rs.getString("unit_type"),
  							rs.getDouble("price_per_unit"),
  							rs.getDouble("cost_per_unit")
    			);
    		}
    	} catch (SQLException e ){
    		System.out.println("Error Fetching product by Id: " + e.getMessage());
    	} finally {
    		closeConnection(con);
    	}
    	return null; //Product not found
    }

    public boolean updateProduct (Product product){
    	String sql = "UPDATE product SET product_name = ?, unit_type = ?,"+
    	"price_per_unit=?, cost_per_unit= ?";

    	Connection con = null;
    	try {
    		con = DBConnection.getConnection();
    		PreparedStatement ps = con.prepareStatement(sql);
    		ps.setString(1, product.getProductName());
            ps.setString(2, product.getUnitType());
            ps.setDouble(3, product.getPricePerUnit());
            ps.setDouble(4, product.getCostPerUnit());
            ps.setInt(5, product.getProductId());   // WHERE clause — which row to update
 
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
 
        } catch (SQLException e) {
            System.out.println("Error updating product: " + e.getMessage());
            return false;
        } finally {
            closeConnection(con);
        }
    	}

    	public boolean deleteProduct(int productId) {
        String sql = "DELETE FROM product WHERE product_id = ?";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, productId);
 
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
 
        } catch (SQLException e) {
            System.out.println("Error deleting product: " + e.getMessage());
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