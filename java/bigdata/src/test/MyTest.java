package test;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
public class MyTest{
	public static void main(String[] args) throws Exception{
		String articleURL = "http://www.imaeil.com/sub_news/sub_news_view.php?news_id=20000&yy=2015";	//신문기사 URL
		Document doc = Jsoup.connect(articleURL).get();		// document 객체 생성.
		Elements ele = doc.select("div#_article");			// 아이디가 _article인 div 태크 선택
		String str = ele.text();							// 값 저장
		System.out.println(str);
	}
}
