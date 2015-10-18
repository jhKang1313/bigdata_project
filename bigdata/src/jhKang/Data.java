package jhKang;
import java.sql.*;
public class Data{
	public void test() throws ClassNotFoundException, SQLException{
		Connection con = new myConnection().getConnection();
		PreparedStatement ps = con.prepareStatement("select * from article");
		ResultSet rs = ps.executeQuery();
		int count = 0;
		while(rs.next()){
			count++;
		}
		System.out.println("Count = " + count);
		rs.close();
		ps.clearParameters();
		con.close();
		
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		Data data = new Data();
		data.test();
	}

}
