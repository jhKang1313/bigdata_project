package jhKang;

import java.sql.SQLException;
import java.util.StringTokenizer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
public class MyExecutor extends Thread{
	private MyDataBase db;
	private String exceptCharRegex = "[^a-zA-Z°¡-ÆR ]*"; 
	private OriginWordDiscriminator originWordDisc = new OriginWordDiscriminator();
	private SentimentWordDiscriminator sentiWordDisc = new SentimentWordDiscriminator();
	private SentimentWordCounter wordCounter = new SentimentWordCounter();
	private OriginWord originWord;
	private SentimentWord sentimentWord;
	private String articleContent;
	private String tokenWord;
	private String originArticleContent;
	private Condition exit;
	private Lock key;
	private Logger log = new Logger();
	public MyExecutor(MyDataBase db, Condition exit, Lock key){
		this.db = db;
		this.exit = exit;
		this.key = key;
	}
	public void run(){
		try{
			//--¿©±âºÎÅÍ ¹Ýº¹ ½ÃÀÛ
			while(!(articleContent = db.getArticle()).equals("exit")){
				log.show("-------------------±â»ç ÀÐ±â------------------");
				if(articleContent.equals("continue"))
					continue;
				wordCounter.reset();
				originArticleContent = articleContent;
				articleContent = articleContent.replaceAll(exceptCharRegex, " ");
				StringTokenizer token = new StringTokenizer(articleContent, " ");
				while(token.hasMoreTokens()){
					originWord = null;
					sentimentWord = null;
					tokenWord = token.nextToken();
					if(db.searchOriginWord(tokenWord)){	//µðºñ1¿¡ ¿øÇü ´Ü¾î Á¸Àç.
						originWord = db.getOriginWord(tokenWord);
						if(db.searchSentimentWord(originWord.originWord)){
							log.show(tokenWord+"\tDB Å½»ö\t¼º°ø");
							sentimentWord = db.getSentimentWord(originWord.originWord);
							if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
								wordCounter.negativeWordCount++;
							else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
								wordCounter.positiveWordCount++;
							else
								wordCounter.nonSentiWordCount++;
						}
						else{
							log.show(tokenWord+"\t°¨¼º¾îÈÖ DBÅ½»ö ½ÇÆÐ");
							continue;
						}
					}
					/*else if(db.searchOriginWord2(tokenWord)){	//µðºñ1¿¡ ¿øÇü ´Ü¾î Á¸Àç.
						originWord = db.getOriginWord2(tokenWord);
						if(db.searchSentimentWord2(originWord.originWord)){
							log.show(tokenWord+"\tDB Å½»ö\t¼º°ø");
							sentimentWord = db.getSentimentWord2(originWord.originWord);
							if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
								wordCounter.negativeWordCount++;
							else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
								wordCounter.positiveWordCount++;
							else
								wordCounter.nonSentiWordCount++;
						}
						else{
							log.show(tokenWord+"\t°¨¼º¾îÈÖ DBÅ½»ö ½ÇÆÐ");
							continue;
						}	
					}*/
					else{				//µðºñ¿¡ ¿øÇü ´Ü¾î Á¸ÀçÇÏÁö ¾ÊÀ½
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
									log.show(tokenWord+"\tDB Å½»ö\t¼º°ø");
								}
								else
									log.show(tokenWord+"\t°¨¼º ¾îÈÖ DBÅ½»ö ½ÇÆÐ");
								
							}
							/*else if(db.searchSentimentWord2(originWord.originWord)){
								sentimentWord = db.getSentimentWord2(originWord.originWord);
								if(sentimentWord != null){
									db.addOriginWordRecord(originWord);
									if(sentimentWord.sentimentType == SentimentType.NE_SENTI_WORD)
										wordCounter.negativeWordCount++;
									else if(sentimentWord.sentimentType == SentimentType.PO_SENTI_WORD)
										wordCounter.positiveWordCount++;
									else
										wordCounter.nonSentiWordCount++;
									log.show(tokenWord+"\tDB Å½»ö\t¼º°ø");
								}
								else
									log.show(tokenWord+"\t°¨¼º ¾îÈÖ DBÅ½»ö ½ÇÆÐ");
								
							}*/
							else{
								sentimentWord = sentiWordDisc.sentimentWordRequest(originWord);
								if(sentimentWord != null){
									if(sentimentWord.errorMsg != null){
										key.lock();
										log.show("Çã¿ë·® ÃÊ°ú");
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
									log.show(tokenWord+"\tAPI Å½»ö\t¼º°ø");
								}
								else
									log.show(tokenWord+"\t°¨¼º ¾îÈÖ ¿äÃ» ½ÇÆÐ");
							}
						}
					}
				}
				System.out.println("Po : " + wordCounter.positiveWordCount);
				System.out.println("Ne : " + wordCounter.negativeWordCount);
				System.out.println("No : " + wordCounter.nonSentiWordCount);
				db.addSentimentWordCount(wordCounter, originArticleContent);
			}
		}catch(Exception e){
			e.printStackTrace();
			log.show("-----------¿À·ù ¹ß»ý----------");	
		}
	}
	public static void main(String[] args) throws ClassNotFoundException, SQLException, InterruptedException{
		Lock key = new ReentrantLock();
		Condition exitCondition = key.newCondition();
		MyDataBase db = new MyDataBase();
		ExecutorService exec = Executors.newCachedThreadPool();
		exec.execute(new MyExecutor(db,exitCondition, key));
		exec.execute(new MyExecutor(db,exitCondition, key));
		exec.execute(new MyExecutor(db,exitCondition, key));
		exec.execute(new MyExecutor(db,exitCondition, key));
		exec.execute(new MyExecutor(db,exitCondition, key));
		exec.execute(new MyExecutor(db,exitCondition, key));
		
		
		key.lock();
		exitCondition.await();
		key.unlock();
	
		exec.shutdown();
		db.myDataBaseClose();
		System.out.println("µðºñ ´ÝÀ½");
	
	}
}
