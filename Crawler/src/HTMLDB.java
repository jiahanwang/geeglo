package ir.assignment.crawler;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

public class HTMLDB {
	private CrawlerDBManager dbManager;
	//private Pattern title_filter = Pattern.compile("(?<=&lt;title&gt;).+(?=&lt;/title&gt;)");
	//private Pattern header_filter = Pattern.compile("(?<=&lt;h[1-6]&gt;)[A-Za-z0-9_]+(?=&lt;/h[1-6]&gt;)");
	
	public HTMLDB() throws ClassNotFoundException, SQLException{
		this.dbManager = new CrawlerDBManager();
	}
	
	public void updateHTMLFromText() throws SQLException{
		this.dbManager.updateHTMLFromText();
	}
	
	public void saveHTMLToDB(String sourcePath){
		try{
			InputStream inputStream = null;
			this.dbManager.initHTMLUpdateBatch();
			// read the data files one by one(file name starts with 1)
			for(int i = 1; i <= 118000; i++){
				if( i % 1000 == 0){
					System.out.println("Updating the database: " + i);
					this.dbManager.commitBatch();
					System.out.println("Updated the database: " + i);
				}
				try{
					inputStream = new FileInputStream(sourcePath + i + ".html");
				}catch(IOException e){
					System.out.println("File " + i +".html is missing. Skipped");
					continue;
				}
				Document doc = Jsoup.parse(inputStream, "UTF-8","");
				// title
				String title = doc.title();
				// headers
				String headers = "";
				Elements header_tags = doc.select("h0,h1,h2,h3,h4,h5,h6");
				if(!header_tags.isEmpty())
					headers = header_tags.text();
				// description 
				Elements metas = doc.select("meta[name=description]");
				if(!metas.isEmpty()){
					headers += metas.first().attr("content");
				}
			    //System.out.println(title);
				//System.out.println(headers);	
				title = title.trim().replaceAll("[\\W]+", " ").toLowerCase();
				headers = headers.trim().replaceAll("[\\W]+", " ").toLowerCase();
				String text_contents = doc.text().trim().replaceAll("[\\W]+", " ").toLowerCase();
			    // add batch
			    this.dbManager.addHTMLUpdateBatch(i, title, headers, text_contents);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void closeDB(){
		this.dbManager.close();
	}
}
