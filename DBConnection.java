import java.sql.*;

class DBConnection{

		private static final String url = "jdbc:mysql://localhost:3306/kiryana_db";
		private static final String DB_user = "root";
		private static final String DB_pass = "Post0322@";

		public static Connection getConnection (){
			try {
				Class.forName("com.mysql.cj.jdbc.Driver");
				Connection con  = DriverManager.getConnection(url, DB_user, DB_pass);
				return con;
			} catch (ClassNotFoundException e){
				System.out.println("MySQL Driver not found! Add mysql-connector-java.jar");
				throw new RuntimeException (e);
			} catch (SQLException e){
				System.out.println("Database connection failed! Check URL, username, and password.");
                System.out.println("Error: " + e.getMessage());
                throw new RuntimeException(e);
			}

		}
	public static void testConnection (){
		try {
		 Connection con = getConnection();
			if (con != null){
				System.out.println ("Connection Successfully! Database is ready.");
				con.close();
			} 
		}
		 catch (Exception e ){
				System.out.println("Connection successful: " + 
					e.getMessage());
			}
		
	}
}