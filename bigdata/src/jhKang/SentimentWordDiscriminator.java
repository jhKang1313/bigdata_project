package jhKang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class SentimentWordDiscriminator {
	private String sentiURL = "http://api.openhangul.com/dic?api_key=";
	private String key = "jinhuk131320151017194558";
	private URL sentiWordRequestURL = null;
	private BufferedReader input = null;	
	private String responseString;
	private String tokenWord;
	private int offset = 0;
	private boolean flag = false;
	public void sentimentWordRequest(OriginWord originWord){
		try {
			sentiWordRequestURL = new URL(sentiURL+key+"&q="+URLEncoder.encode(originWord.originWord, "UTF-8"));
			input = new BufferedReader(new InputStreamReader(sentiWordRequestURL.openStream(), "UTF-8"));
			for(int i = 0 ;(responseString = input.readLine()) != null; i++){
				if(i == 6){
					SentimentWordCounter wordCounter = new SentimentWordCounter();
					StringTokenizer token = new StringTokenizer(responseString, "\"");
					while(token.hasMoreTokens()){
						tokenWord = token.nextToken();
						if(offset == 10 && tokenWord.equals(": ") == false){
							wordCounter.positiveWordCount++;
							//addSentiWordRecord();
							
							
							
						}
					}
					
					
					
					
					break;
				}
			}
		} catch (Exception e) {}
		
	}
}
