import java.sql.*;
import java.util.ArrayList;


public class DebtTransactionDAO {

    
    public boolean saveTransaction(DebtTransaction transaction) {
        String sql = "INSERT INTO debt_tracking (debt_id, amount, type, trans_date) "
                   + "VALUES (?, ?, ?, ?)";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);

            ps.setInt(1, transaction.getDebtId());
            ps.setDouble(2, transaction.getAmount());
            ps.setString(3, transaction.getType());
            ps.setTimestamp(4, Timestamp.valueOf(transaction.getTransDate()));

            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error saving debt transaction: " + e.getMessage());
            return false;
        } finally {
            closeConnection(con);
        }
    }

   
     
    public ArrayList<DebtTransaction> getTransactionsByDebt(int debtId) {
        ArrayList<DebtTransaction> list = new ArrayList<>();
        String sql = "SELECT * FROM debt_tracking WHERE debt_id = ? ORDER BY trans_date ASC";

        Connection con = null;
        try {
            con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, debtId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                DebtTransaction dt = new DebtTransaction();
                dt.setTransId(rs.getInt("trans_id"));
                dt.setDebtId(rs.getInt("debt_id"));
                dt.setAmount(rs.getDouble("amount"));
                dt.setType(rs.getString("type"));

                // Convert Timestamp to LocalDateTime
                Timestamp ts = rs.getTimestamp("trans_date");
                if (ts != null) {
                    dt.setTransDate(ts.toLocalDateTime());
                }

                list.add(dt);
            }

        } catch (SQLException e) {
            System.out.println("Error fetching transactions: " + e.getMessage());
        } finally {
            closeConnection(con);
        }

        return list;
    }

    // PRIVATE HELPER
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