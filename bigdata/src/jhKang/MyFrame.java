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
	int leng;
	MyFrame(){
		this.setVisible(true);
		this.setSize(500, 200);
		this.setLayout(new FlowLayout());
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		String [] key = {"tjfdydgkr20151021004723:tjfdydgkr/123qwe",
				"psm43320151103042216:psm433/1234",
				"jjm2220151103042322:jjm22/0000",
				"lee2220151103042432:lee22/22",
				"ehdsuz12320151103042700:ehdsuz123/123",
				"blue9120151103042812:blue91/2233",
				"yoon9120151103042921:yoon91/910203",
				"bbk2220151103043049:bbk22/22",
				
				"yooraekyoung20151020225203:yooraekyoung/0909",
				"kkong20151021004943:kkong/930909",
				"jinhuk131320151017194558:jinhuk1313/wlsgur1313",
				"jinhuk9220151021020056:jinhuk92/wlsgur1313",
				"efsfgtjtj20151021023303:efsfgtjtj/1234",
				"dudgns14520151103012537:dudgns145/dudgns145",
				"whddn12320151103012715:whddn123/whddn123",
				"tkdals12320151103012817:tkdals123/tkdals123",
				"tmdwo7720151103012927:tmdwo77/tmdwo77",
				"cjh51220151103013030:cjh512/wogh123",
				"ggmm20151103013133:ggmm/1234",
				"sjsj2220151103223104:sjsj22/1212",
				"sunhee6820151103223205:sunhee68/6868",
				"junhee7720151103223309:junhee77/77",
				"movegun20151103223429:movegun/123",
				"competi20151103223546:competi/123",
				"zeroegg20151103223632:zeroegg/123"

				};
		leng = key.length;
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
					for(int i = 0 ; i < leng ; i++){
						Lock key = new ReentrantLock();
						Condition exitCondition = key.newCondition();
						MyDataBase db = new MyDataBase();
						String apiKey = (String)combo.getSelectedItem();
						apiKey = apiKey.replaceAll(":.*", "");

						ExecutorService exec = Executors.newCachedThreadPool();
						exec.execute(new MyGarbageCollector(apiKey));
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
						combo.setSelectedIndex((comboIndex%leng)+i);
			
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
