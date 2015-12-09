package article.senti.jhKang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class SentimentWordDiscriminator {
	private String sentiURL = "http://api.openhangul.com/dic?api_key=";
	private String key;
	private URL sentiWordRequestURL = null;
	private BufferedReader input = null;	
	private String responseString;
	private String tokenWord;
	private int offset = 0;
	private boolean flag = false;
	private InputStreamReader stream;
	private SentimentWord sentimentWord;
	private StringTokenizer token;
	public SentimentWordDiscriminator(String apiKey){
		this.key = apiKey;
	}
	public SentimentWord sentimentWordRequest(OriginWord originWord){
		try {
			offset = 0;
			flag = false;
			sentiWordRequestURL = new URL(sentiURL+key+"&q="+URLEncoder.encode(originWord.originWord, "UTF-8"));
			stream = new InputStreamReader(sentiWordRequestURL.openStream(), "UTF-8");
			input = new BufferedReader(stream);
			for(int i = 0 ;(responseString = input.readLine()) != null; i++){
				if(i == 6){
					if(responseString.equals("")){
						cleanUp();
						return null;
					}
					sentimentWord = new SentimentWord();
					token = new StringTokenizer(responseString, "\"");
					while(token.hasMoreTokens()){
						tokenWord = token.nextToken();			//offset -> 3 원형, -> 7 명사, -> 11 sentiType, ->15 score
						if(offset == 1 && tokenWord.equals("message")){
							sentimentWord.errorMsg = "error"; 
							cleanUp();
							return sentimentWord;
						}
						if(offset == 3 && tokenWord.equals(": ") == false)
							sentimentWord.originWord = tokenWord;						
						else if(offset == 7 && tokenWord.equals(": ") == false)
							sentimentWord.wordType = tokenWord;
						else if(offset == 10 && tokenWord.equals(": ") == false){
							if(tokenWord.equals("긍정"))
								sentimentWord.sentimentType = SentimentType.PO_SENTI_WORD;
							else if(tokenWord.equals("부정"))
								sentimentWord.sentimentType = SentimentType.NE_SENTI_WORD;
							else
								sentimentWord.sentimentType = SentimentType.NON_SENTI_WORD;								
							flag = true;
						}
						else if(offset == 11 && !flag){
							if(tokenWord.equals("긍정"))
								sentimentWord.sentimentType = SentimentType.PO_SENTI_WORD;
							else if(tokenWord.equals("부정"))
								sentimentWord.sentimentType = SentimentType.NE_SENTI_WORD;
							else
								sentimentWord.sentimentType = SentimentType.NON_SENTI_WORD;								
						}
						else if(offset == 14 && flag){
							tokenWord = tokenWord.replace("%", "");
							sentimentWord.sentimentScore = (int)Double.parseDouble(tokenWord);
						}
						else if(offset == 15 && !flag){
							tokenWord = tokenWord.replace("%", "");
							sentimentWord.sentimentScore = (int)Double.parseDouble(tokenWord);
						}
						offset++;
					}
					cleanUp();
					return sentimentWord;
				}
			}
			cleanUp();
		} catch (Exception e) {}
		return null;
	}
	public void cleanUp() throws IOException{
		this.stream.close();
		this.input.close();
	}
}
