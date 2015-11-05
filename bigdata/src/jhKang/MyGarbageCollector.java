package jhKang;

public class MyGarbageCollector extends Thread{
	private Logger log = new Logger();
	private String apiKey;
	public MyGarbageCollector(String apiKey) {
		// TODO Auto-generated constructor stub
		this.apiKey = apiKey;
	}
	public void run(){
		while(true){
			try{
				Thread.sleep(10000);
				log.show("+++++++++++Garbage Collector+++++++++++");
				log.show("+++++++++++"+apiKey+"+++++++++++");
				System.gc();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
