package jhKang;

import java.sql.SQLException;
import java.util.StringTokenizer;

public class MyExecutor {
	private MyDataBase db;
	private String exceptCharRegex = "[^a-zA-Z��-�R ]*"; 
	private OriginWordDiscriminator originWordDisc = new OriginWordDiscriminator();
	private SentimentWordDiscriminator sentiWordDisc = new SentimentWordDiscriminator();
	private SentimentWordCounter wordCounter = new SentimentWordCounter();
	private OriginWord originWord;
	private SentimentWord sentimentWord;
	private String articleContent;
	private String tokenWord;
	private String originArticleContent;
	public void run() throws ClassNotFoundException, SQLException{
		try{
			db = new MyDataBase();

			//--������� �ݺ� ����
			while(!(articleContent = db.getArticle()).equals("exit")){
				if(articleContent.equals("continue"))
					continue;
				wordCounter.reset();
				originArticleContent = articleContent;
				articleContent = articleContent.replaceAll(exceptCharRegex, "");
				StringTokenizer token = new StringTokenizer(articleContent, " ");
				while(token.hasMoreTokens()){
					originWord = null;
					sentimentWord = null;
					tokenWord = token.nextToken();
					System.out.print(tokenWord);
					if(db.searchOriginWord(tokenWord)){	//��� ���� �ܾ� ����.
						originWord = db.getOriginWord(tokenWord);
						if(db.searchSentimentWord(originWord.originWord)){
							System.out.print("\tDB Ž��");
							sentimentWord = db.getSentimentWord(originWord.originWord);
							if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
								wordCounter.negativeWordCount++;
							else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
								wordCounter.positiveWordCount++;
							else
								wordCounter.nonSentiWordCount++;
						}
						else
							continue;
					}
					else{				//��� ���� �ܾ� �������� ����
						originWord = originWordDisc.requestOriginWord(tokenWord);
						if(originWord != null){
							if(db.searchSentimentWord(originWord.originWord)){
								System.out.print("\tDB Ž��");
								sentimentWord = db.getSentimentWord(originWord.originWord);
								if(sentimentWord != null){
									db.addOriginWordRecord(originWord);
									if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
										wordCounter.negativeWordCount++;
									else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
										wordCounter.positiveWordCount++;
									else
										wordCounter.nonSentiWordCount++;
								}
							}
							else{
								System.out.print("\tAPI Ž��");
								sentimentWord = sentiWordDisc.sentimentWordRequest(originWord);
								if(sentimentWord != null){
									db.addSentiWordRecord(sentimentWord);
									db.addOriginWordRecord(originWord);
									if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
										wordCounter.negativeWordCount++;
									else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
										wordCounter.positiveWordCount++;
									else
										wordCounter.nonSentiWordCount++;
								}
							}
						}
					}
					System.out.println("\tdone");
				}
				System.out.println("Po : " + wordCounter.positiveWordCount);
				System.out.println("Ne : " + wordCounter.negativeWordCount);
				System.out.println("No : " + wordCounter.nonSentiWordCount);
				db.addSentimentWordCount(wordCounter, originArticleContent);
			}


			db.myDataBaseClose();
			System.out.println("��� ����");
		}catch(Exception e){
			System.out.println("������ ���� ��� ����");
			db.myDataBaseClose();
		}
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException{
		new MyExecutor().run(); 
	}
}
