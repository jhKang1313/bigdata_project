package test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class MyTest{
	public static void main(String[] args) throws Exception{
		String articleURL = "http://www.imaeil.com/sub_news/sub_news_view.php?news_id=20000&yy=2015";	//�Ź���� URL
		Document doc = Jsoup.connect(articleURL).get();		// document ��ü ����.
		Elements ele = doc.select("div#_article");			// ���̵� _article�� div ��ũ ����
		String str = ele.text();							// �� ����
		System.out.println(str);
	}
}
