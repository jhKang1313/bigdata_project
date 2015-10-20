package jhKang;

public class SentimentWord {
	public String originWord;
	public String wordType;
	public SentimentType sentimentType;
	public int sentimentScore;
	public SentimentWord(String originWord, String wordType, int sentimentType, int sentimentScore){
		this.originWord = originWord;
		this.wordType = wordType;
		if(sentimentType == 0)
			this.sentimentType = SentimentType.NON_SENTI_WORD;
		else if(sentimentType == 1)
			this.sentimentType = SentimentType.PO_SENTI_WORD;
		else if(sentimentType == 2)
			this.sentimentType = SentimentType.NE_SENTI_WORD;
		this.sentimentScore = sentimentScore;
	}
	public SentimentWord(){
		
		
	}
}
