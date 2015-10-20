package jhKang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class OriginWordDiscriminator {
	private final String requestURL = "http://api.openhangul.com/basic?q=";
	private URL originWordRequestURL = null;
	private BufferedReader input = null;		//BufferedReader�� �������� reader�� �ϸ� �ȵǳ�.
	private String responseString;			
	private StringTokenizer token;
	private String word;
	public OriginWord requestOriginWord(String text){
		try{
			originWordRequestURL = new URL("http://api.openhangul.com/basic?q=" + URLEncoder.encode(text, "UTF-8"));
			input = new BufferedReader(new InputStreamReader(originWordRequestURL.openStream(), "UTF-8"));
			for(int i = 0 ;(responseString = input.readLine()) != null; i++){
				if(i == 6){
					token = new StringTokenizer(responseString, "\"");
					int offset = 0;
					while(token.hasMoreTokens()){
						word = token.nextToken();
						if(offset == 7){
							if(word.equals(", ") == true)
								return null;
							else
								return new OriginWord(text, word);
						}
						offset++;
					}
				}
			}
		}
		catch(Exception e){}
		return null;
	}
}
