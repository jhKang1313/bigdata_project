package jhKang;

public class MyGarbageCollector extends Thread{
	private Logger log = new Logger();
	public void run(){
		while(true){
			try{
				Thread.sleep(10000);
				log.show("+++++++++++Garbage Collector+++++++++++");
				System.gc();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
