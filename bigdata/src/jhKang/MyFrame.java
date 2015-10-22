package jhKang;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;

public class MyFrame extends JFrame{
	MyFrame(){
		this.setVisible(true);
		this.setSize(500, 200);
		this.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String [] key = {"tjfdydgkr20151021004723:tjfdydgkr/123qwe",
				"yooraekyoung20151020225203:yooraekyoung/0909",
				"kkong20151021004943:kkong/930909",
				"jinhuk131320151017194558:jinhuk1313/wlsgur1313",
				"jinhuk9220151021020056:jinhuk92/wlsgur1313",
				"efsfgtjtj20151021023303:efsfgtjtj/1234",
				"yoo320151021180808:yoo3/930909",
				"yoo520151021184638:yoo5/930909",
				"yoo620151021184724:yoo6/930909",
				"yoo820151021184842:yoo8/930909",
				"yoo920151021184929:yoo9/930909",
				"yoo1020151021185026:yoo10/930909",
				"yoo1120151021185106:yoo11/930909",
				"yoo1220151021185143:yoo12/930909",
		"yoo1320151021185222:yoo13/930909"};
		JComboBox combo = new JComboBox(key);
		combo.setVisible(true);
		this.add(combo);
		JButton button = new JButton("시작");
		this.add(button);
		button.setVisible(true);
		button.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent arg0) {
				try{
					int comboIndex = combo.getSelectedIndex();
					for(int i = 0 ; i < 10 ; i++){
						Lock key = new ReentrantLock();
						Condition exitCondition = key.newCondition();
						MyDataBase db = new MyDataBase();
						String apiKey = (String)combo.getSelectedItem();
						apiKey = apiKey.replaceAll(":.*", "");

						ExecutorService exec = Executors.newCachedThreadPool();
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));
						exec.execute(new MyExecutor(db,exitCondition, key, apiKey));

						key.lock();
						exitCondition.await();
						key.unlock();

						exec.shutdown();
						db.myDataBaseClose();
						System.out.println("디비 닫음");
						combo.setSelectedIndex(++comboIndex);
					}
				}catch(Exception e){
					e.printStackTrace();
				}
			}
		});
	}
	public static void main(String[] args){
		new MyFrame();
	}
}
