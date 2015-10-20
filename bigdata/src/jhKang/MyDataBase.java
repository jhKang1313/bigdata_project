package jhKang;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.StringTokenizer;
public class MyDataBase{
	private String articleContent;
	private String exceptCharRegex = "[^a-zA-Z°¡-ÆR ]*"; 
	private String text;
	private OriginWordDiscriminator originWordDisc = new OriginWordDiscriminator(this);
	private SentimentWordDiscriminator sentiWordDisc = new SentimentWordDiscriminator();
	private Connection con;
	private OriginWord originWord;
	public void test() throws ClassNotFoundException, SQLException{
		con = new MyConnection().getConnection();
		PreparedStatement ps = con.prepareStatement("select article_content from article_table");
		ResultSet rs = ps.executeQuery();
		
		rs.next();	
		articleContent = rs.getString("article_content");	
		articleContent = articleContent.replaceAll(exceptCharRegex, "");	//Æ¯¼ö¹®ÀÚ Á¦°Å
		StringTokenizer token = new StringTokenizer(articleContent, " ");
		while(token.hasMoreTokens()){
			text = token.nextToken();
			originWord = originWordDisc.requestOriginWord(text);
			sentiWordDisc.sentimentWordRequest(originWord);
		}
		
		rs.close();
		ps.clearParameters();
		con.close();
		
	}
	public void addOriginWordRecord() throws ClassNotFoundException, SQLException{
		PreparedStatement ps = con.prepareStatement("insert into originword_table(text, origin_word) values (?,?)");
		ps.setString(1, originWord.text);
		ps.setString(2, originWord.originWord);
		
		
	}
	public void addSentiWordRecord() throws ClassNotFoundException, SQLException{
		PreparedStatement ps = con.prepareStatement("insert into sentimentword_table(text, origin_word) values (?,?)");
		
		
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		MyDataBase data = new MyDataBase();
		data.test();
	}

}
