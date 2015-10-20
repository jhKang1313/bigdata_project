package jhKang;

public class SentimentWord {
	public String originWord;
	public String wordType;
	public SentimentType sentimentType;
	public int sentimentScore;
	public SentimentWord(String originWord, String wordType, SentimentType sentimentType, int sentimentScore){
		this.originWord = originWord;
		this.wordType = wordType;
		this.sentimentType = sentimentType;
		this.sentimentScore = sentimentScore;
	}
}
