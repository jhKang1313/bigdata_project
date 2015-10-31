package jhKang;

import java.util.StringTokenizer;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
public class MyExecutor extends Thread{
	private MyDataBase db;
	private String apiKey;
	private String exceptCharRegex = "[^a-zA-Z��-�R ]*"; 
	private OriginWordDiscriminator originWordDisc = new OriginWordDiscriminator();
	private SentimentWordDiscriminator sentiWordDisc;
	private SentimentWordCounter wordCounter = new SentimentWordCounter();
	private OriginWord originWord;
	private SentimentWord sentimentWord;
	private String articleContent;
	private String tokenWord;
	private String originArticleContent;
	private Condition exit;
	private Lock key;
	private Logger log = new Logger();
	private StringTokenizer token;
	public MyExecutor(MyDataBase db, Condition exit, Lock key, String apiKey){
		this.db = db;
		this.exit = exit;
		this.key = key;
		this.apiKey = apiKey;
		sentiWordDisc  = new SentimentWordDiscriminator(apiKey);
	}
	public void run(){
		try{
			//--������� �ݺ� ����
			while(!(articleContent = db.getArticle()).equals("exit")){
				log.show("-------------------��� �б�------------------");
				if(articleContent.equals("continue"))
					continue;
				wordCounter.reset();
				originArticleContent = articleContent;
				articleContent = articleContent.replaceAll(exceptCharRegex, "");	//Ư�� ���� ����
				token = new StringTokenizer(articleContent, " ");
				while(token.hasMoreTokens()){
					originWord = null;
					sentimentWord = null;
					tokenWord = token.nextToken();
					if(db.searchOriginWord(tokenWord)){	//��� ���� �ܾ� ����.
						originWord = db.getOriginWord(tokenWord);
						if(db.searchSentimentWord(originWord.originWord)){
							log.show(tokenWord+"\tDB Ž��\t����");
							sentimentWord = db.getSentimentWord(originWord.originWord);
							if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
								wordCounter.negativeWordCount++;
							else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
								wordCounter.positiveWordCount++;
							else
								wordCounter.nonSentiWordCount++;
						}
						else{
							log.show(tokenWord+"\t�������� DBŽ�� ����");
							continue;
						}
					}
					else{				//��� ���� �ܾ� �������� ����
						originWord = originWordDisc.requestOriginWord(tokenWord);
						if(originWord != null){
							if(db.searchSentimentWord(originWord.originWord)){
								sentimentWord = db.getSentimentWord(originWord.originWord);
								if(sentimentWord != null){
									db.addOriginWordRecord(originWord);
									if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
										wordCounter.negativeWordCount++;
									else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
										wordCounter.positiveWordCount++;
									else
										wordCounter.nonSentiWordCount++;
									log.show(tokenWord+"\tDB Ž��\t����");
								}
								else
									log.show(tokenWord+"\t���� ���� DBŽ�� ����");
								
							}
							else{
								sentimentWord = sentiWordDisc.sentimentWordRequest(originWord);
								if(sentimentWord != null){
									if(sentimentWord.errorMsg != null){
										key.lock();
										log.show("��뷮 �ʰ�");
										exit.signal();
										key.unlock();
									}
									db.addSentiWordRecord(sentimentWord);
									db.addOriginWordRecord(originWord);
									if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
										wordCounter.negativeWordCount++;
									else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
										wordCounter.positiveWordCount++;
									else
										wordCounter.nonSentiWordCount++;
									log.show(tokenWord+"\tAPI Ž��\t����");
								}
								else
									log.show(tokenWord+"\t���� ���� ��û ����");
							}
						}
					}
				}
				System.out.println("Po : " + wordCounter.positiveWordCount);
				System.out.println("Ne : " + wordCounter.negativeWordCount);
				System.out.println("No : " + wordCounter.nonSentiWordCount);
				db.addSentimentWordCount(wordCounter, originArticleContent);
				articleContent = null;
				originArticleContent = null;
			}
		}catch(Exception e){
			e.printStackTrace();
			log.show("-----------���� �߻�----------");	
		}
	}
}
