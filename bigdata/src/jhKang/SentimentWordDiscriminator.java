package jhKang;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.util.StringTokenizer;

public class SentimentWordDiscriminator {
	private String sentiURL = "http://api.openhangul.com/dic?api_key=";
	private String key = "tjfdydgkr20151021004723";//"yooraekyoung20151020225203";
	private URL sentiWordRequestURL = null;
	private BufferedReader input = null;	
	private String responseString;
	private String tokenWord;
	private int offset = 0;
	private boolean flag = false;
	public SentimentWord sentimentWordRequest(OriginWord originWord){
		try {
			offset = 0;
			flag = false;
			sentiWordRequestURL = new URL(sentiURL+key+"&q="+URLEncoder.encode(originWord.originWord, "UTF-8"));
			input = new BufferedReader(new InputStreamReader(sentiWordRequestURL.openStream(), "UTF-8"));
			for(int i = 0 ;(responseString = input.readLine()) != null; i++){
				if(i == 6){
					if(responseString.equals(""))
						return null;
					SentimentWord sentimentWord = new SentimentWord();
					StringTokenizer token = new StringTokenizer(responseString, "\"");
					while(token.hasMoreTokens()){
						tokenWord = token.nextToken();			//offset -> 3 ����, -> 7 ���, -> 11 sentiType, ->15 score
						if(offset == 1 && tokenWord.equals("message")){
							sentimentWord.errorMsg = "error"; 
							return sentimentWord;
						}
						if(offset == 3 && tokenWord.equals(": ") == false)
							sentimentWord.originWord = tokenWord;						
						else if(offset == 7 && tokenWord.equals(": ") == false)
							sentimentWord.wordType = tokenWord;
						else if(offset == 10 && tokenWord.equals(": ") == false){
							if(tokenWord.equals("����"))
								sentimentWord.sentimentType = SentimentType.PO_SENTI_WORD;
							else if(tokenWord.equals("����"))
								sentimentWord.sentimentType = SentimentType.NE_SENTI_WORD;
							else
								sentimentWord.sentimentType = SentimentType.NON_SENTI_WORD;								
							flag = true;
						}
						else if(offset == 11 && !flag){
							if(tokenWord.equals("����"))
								sentimentWord.sentimentType = SentimentType.PO_SENTI_WORD;
							else if(tokenWord.equals("����"))
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
					return sentimentWord;
				}
			}
		} catch (Exception e) {}
		return null;
	}
}
