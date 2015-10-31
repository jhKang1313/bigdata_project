package jhKang;

public class MyGarbageCollector extends Thread{
	public void run(){
		while(true){
			try{
				Thread.sleep(10000);
				System.out.println("+++++++++++Garbage Collector+++++++++++");
				System.gc();
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
	}
}
