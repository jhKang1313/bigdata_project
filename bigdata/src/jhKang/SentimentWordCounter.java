package jhKang;

public class SentimentWordCounter {
	public int positiveWordCount;
	public int negativeWordCount;
	public int nonSentiWordCount;
	public SentimentWordCounter(int posi, int nega, int non){
		this.positiveWordCount = posi;
		this.negativeWordCount = nega;
		this.nonSentiWordCount = non;
	}
	public SentimentWordCounter(){
		
		
	}
	public void reset(){
		this.positiveWordCount = 0;
		this.negativeWordCount = 0;
		this.nonSentiWordCount = 0;
	}
}
