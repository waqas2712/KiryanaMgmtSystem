import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;


class DebtDAO{



	private Debt buildDebtFromResultSet(ResultSet rs) throws SQLException {
        Debt d = new Debt();
        d.setDebtId(rs.getInt("debt_id"));
        d.setSaleId(rs.getInt("sale_id"));
        d.setCustomerName(rs.getString("customer_name"));
        d.setAmount(rs.getDouble("amount"));
        d.setRemaining(rs.getDouble("remaining"));
        d.setStatus(rs.getString("status"));
 
        Timestamp ts = rs.getTimestamp("debt_date");
        if (ts != null) {
            d.setDebtDate(ts.toLocalDateTime());
        }
 
        return d;
    }

	public int createDebt (Debt debt){
		String sql= "INSERT INTO debt (sale_id, customer_name, amount, remaining, debt_date, status)"+
		"VALUES (?, ?,?,?,?,?)";


		Connection con = null;

		try {
			con= DBConnection.getConnection();
			PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);


			ps.setInt(1,debt.getSaleId ());
			ps.setString(2, debt.getCustomerName());
			ps.setDouble (3, debt.getAmount());
			ps.setDouble (4, debt.getRemaining());
			ps.setTimestamp(5,Timestamp.valueOf (debt.getDebtDate()));
			ps.setString (6, debt.getStatus());

			int rowsaffected = ps.executeUpdate();

			if (rowsaffected > 0){
				ResultSet keys = ps.getGeneratedKeys();
				if (keys.next()){
					return keys.getInt(1);
				}
			}
		} catch (SQLException e ){
			System.out.println("Error creating debt: " + e.getMessage());
		} finally {
			closeConnection(con);
		}


		return -1;
	}


	public ArrayList<Debt> getAllDebts() {
        ArrayList<Debt> list = new ArrayList<>();
        String sql = "SELECT * FROM debt ORDER BY debt_date DESC";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                list.add(buildDebtFromResultSet(rs));
            }
 
        } catch (SQLException e) {
            System.out.println("Error fetching debts: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return list;
    }

    public ArrayList<Debt> getPendingDebts() {
        ArrayList<Debt> list = new ArrayList<>();
 
        // Use IN to match either status value
        String sql = "SELECT * FROM debt WHERE status IN ('UNPAID', 'PARTIALLY_PAID') "
                   + "ORDER BY debt_date ASC"; // oldest debt first
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();
 
            while (rs.next()) {
                list.add(buildDebtFromResultSet(rs));
            }
 
        } catch (SQLException e) {
            System.out.println("Error fetching pending debts: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return list;
    }
     public ArrayList<Debt> searchByCustomer(String customerName) {
        ArrayList<Debt> list = new ArrayList<>();
        String sql = "SELECT * FROM debt WHERE customer_name LIKE ? ORDER BY debt_date DESC";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, "%" + customerName + "%");
 
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(buildDebtFromResultSet(rs));
            }
 
        } catch (SQLException e) {
            System.out.println("Error searching debts: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return list;
    }
    public Debt getDebtById(int debtId) {
        String sql = "SELECT * FROM debt WHERE debt_id = ?";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, debtId);
 
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return buildDebtFromResultSet(rs);
            }
 
        } catch (SQLException e) {
            System.out.println("Error fetching debt by ID: " + e.getMessage());
        } finally {
            closeConnection(con);
        }
 
        return null;
    }

    public boolean updateDebt(Debt debt) {
        String sql = "UPDATE debt SET remaining = ?, status = ? WHERE debt_id = ?";
 
        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
 
            ps.setDouble(1, debt.getRemaining());
            ps.setString(2, debt.getStatus());
            ps.setInt(3, debt.getDebtId());
 
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
 
        } catch (SQLException e) {
            System.out.println("Error updating debt: " + e.getMessage());
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

