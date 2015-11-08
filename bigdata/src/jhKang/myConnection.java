package jhKang;
import java.sql.*;
public class MyConnection {
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://192.168.0.10/bigdata_proj", "newuser", "1234");
		return con;
	}
}
