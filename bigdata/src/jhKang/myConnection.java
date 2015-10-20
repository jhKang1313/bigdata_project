package jhKang;
import java.sql.*;
public class MyConnection {
	public Connection getConnection() throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		Connection con = DriverManager.getConnection("jdbc:mysql://localhost/bigdata_proj", "jhKang1313", "1234");
		return con;
	}
}
