//매일 신문 전용
package test;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
class CSVFormat{
	public String articleContent;
	public String articleCategory;
	public String articleDate;	
	public void resetData(){
		this.articleCategory = null;
		this.articleContent = null;
		this.articleDate = null;
	}
}
public class MyJsoup{
	public String field = "날짜, URL, 분류, 내용 \r\n";
	public String csvFileName = "c:/eco2015.csv";
	public String dateTag = "BUILD";
	public String category = "KEYWORDS";
	private CSVFormat format = new CSVFormat();
	public String url;
	private Document doc;
	private Elements ele;// = doc.select("div.article");
	private BufferedWriter writer;
	public MyJsoup() throws Exception{
		writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(csvFileName), "MS949"));
		writer.write(field);
		
	}
	public void run(){//54500
		for(int i = 1 ; i< 65000 ; i++){
			format.resetData();
			try{
				setURL(i);
				setArticleCategory2();
				if(format.articleCategory != null){
					System.out.println(i);
					setArticleDate();
					setArticleContent();
					writer.write(format.articleDate+" , "+i+" , "+format.articleCategory +" , "+format.articleContent +"\r\n");
				}

			}catch(Exception e){

			}
		}
		try {
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public void setURL(int i) throws Exception{
		url = "http://www.imaeil.com/sub_news/sub_news_view.php?news_id="+i+"&yy=2014";
		doc = Jsoup.connect(url).get();
	}
	public void setArticleCategory2(){
		String temp = null;
		ele = doc.select("td");

		for(Element e: ele){
			temp = e.getElementsByTag("td").iterator().next().text();
			if(temp.contains("<경제>")){
				format.articleCategory = "경제";
			}
			else if(temp.contains("<사회>")){
				format.articleCategory = "사회";
			}
		}
	}
	public void setArticleCategory(){
		ele = doc.select("META");
		for(Element e : ele){
			if(e.attr("Name").equals(category)){
				if(e.attr("Content").contains("경제")){
					format.articleCategory = "경제";
				}				
			}
		}
	}
	public void setArticleDate(){
		ele = doc.select("META");
		for(Element e : ele)
			if(e.attr("Name").equals(dateTag))
				format.articleDate = e.attr("Content");
	}
	public void setArticleContent(){
		ele = doc.select("div#_article");
		format.articleContent = ele.text();
		format.articleContent = format.articleContent.replaceAll(",", "");
	}
	public void concatArticleData(){
		
		
	}
}
