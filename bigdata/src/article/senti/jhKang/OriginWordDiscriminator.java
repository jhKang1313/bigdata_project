package article.senti.jhKang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class OriginWordDiscriminator {
	private final String requestURL = "http://api.openhangul.com/basic?q=";
	private URL originWordRequestURL = null;
	private BufferedReader input = null;		//BufferedReader의 변수명을 reader라 하면 안되네.
	private String responseString;			
	private StringTokenizer token;
	private String word;
	private InputStreamReader stream;
	public OriginWord requestOriginWord(String text){
		try{
			originWordRequestURL = new URL(requestURL + URLEncoder.encode(text, "UTF-8"));
			stream = new InputStreamReader(originWordRequestURL.openStream(), "UTF-8");
			input = new BufferedReader(stream);
			for(int i = 0 ;(responseString = input.readLine()) != null; i++){
				if(i == 6){
					token = new StringTokenizer(responseString, "\"");
					int offset = 0;
					while(token.hasMoreTokens()){
						word = token.nextToken();
						if(offset == 7){
							if(word.equals(", ") == true){
								cleanUp();
								return null;
							}
							else{
								cleanUp();
								return new OriginWord(text, word);
							}
						}
						offset++;
					}
				}
			}
		}
		catch(Exception e){}
		return null;
	}
	public void cleanUp() throws IOException{
		this.stream.close();
		this.input.close();
	}
}
