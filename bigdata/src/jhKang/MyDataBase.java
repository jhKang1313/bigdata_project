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
	
	private Connection connection;
	private Connection connection2;
	private PreparedStatement articlePreparedStatement;
	private ResultSet articleResultSet;
	private PreparedStatement preparedStatement;
	private ResultSet resultSet;
	
	public MyDataBase() throws ClassNotFoundException, SQLException{
		connection = new MyConnection().getConnection();
		this.articlePreparedStatement = this.connection.prepareStatement("select * from article_table");
		this.articleResultSet = this.articlePreparedStatement.executeQuery();
		
	}
	public void myDataBaseClose() throws ClassNotFoundException, SQLException{
		articleResultSet.close();
		resultSet.close();
		articlePreparedStatement.clearParameters();
		preparedStatement.clearParameters();
		connection.close();	
	}
	synchronized
	public String getArticle() throws ClassNotFoundException, SQLException{
		if(articleResultSet.next()){
			if(articleResultSet.getInt("posi_word_count") != 0 && articleResultSet.getInt("nega_word_count") != 0 && articleResultSet.getInt("non_word_count") != 0)
				return "continue";
			else
				return articleResultSet.getString("article_content");	//특수문자 제거
		}
		else
			return "exit";
	}
	synchronized
	public void addSentimentWordCount(SentimentWordCounter count, String article) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement("update article_table set posi_word_count=?, nega_word_count=?, non_word_count=? where article_content=?");
		preparedStatement.setInt(1, count.positiveWordCount);
		preparedStatement.setInt(2, count.negativeWordCount);
		preparedStatement.setInt(3, count.nonSentiWordCount);
		preparedStatement.setString(4, article);
		preparedStatement.executeUpdate();
		
	}
	synchronized
	public boolean searchOriginWord(String text) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement("select count(*) from originword_table where text=?");
		preparedStatement.setString(1, text);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		if(resultSet.getInt(1) != 0)
			return true;
		else 
			return false;
	}
	
	synchronized
	public OriginWord getOriginWord(String text) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement("select * from originword_table where text=?");
		preparedStatement.setString(1, text);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		OriginWord originWord = new OriginWord(text, resultSet.getString("origin_word"));
		return originWord;
	}
	synchronized
	public boolean searchSentimentWord(String originWord) throws ClassNotFoundException, SQLException{
		this.preparedStatement = this.connection.prepareStatement("select count(*) from sentimentword_table where origin_word=?");
		preparedStatement.setString(1, originWord);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		if(resultSet.getInt(1) != 0)
			return true;
		else 
			return false;
	}
	synchronized
	public SentimentWord getSentimentWord(String originWord) throws ClassNotFoundException, SQLException{
		this.preparedStatement = this.connection.prepareStatement("select * from sentimentword_table where origin_word=?");
		preparedStatement.setString(1, originWord);
		resultSet = preparedStatement.executeQuery();
		resultSet.next();
		SentimentWord sentimentWord = new SentimentWord(resultSet.getString("origin_word"), resultSet.getString("word_type"), resultSet.getInt("sentiment_type"), resultSet.getInt("sentiment_score"));
		return sentimentWord;
		
	}
	synchronized
	public void addOriginWordRecord(OriginWord originWord) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement("insert into originword_table(text, origin_word) values (?,?)");
		preparedStatement.setString(1, originWord.text);
		preparedStatement.setString(2, originWord.originWord);
		
		preparedStatement.executeUpdate();
	}
	synchronized
	public void addSentiWordRecord(SentimentWord sentiWord) throws ClassNotFoundException, SQLException{
		preparedStatement = this.connection.prepareStatement("insert into sentimentword_table(origin_word, word_type, sentiment_type, sentiment_score) values (?,?,?,?)");
		preparedStatement.setString(1, sentiWord.originWord);
		preparedStatement.setString(2, sentiWord.wordType);
		preparedStatement.setInt(3, sentiWord.sentimentType.ordinal());
		preparedStatement.setInt(4, sentiWord.sentimentScore);
		
		preparedStatement.executeUpdate();
	}
}
