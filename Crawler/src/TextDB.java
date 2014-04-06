package ir.assignment.crawler;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.SQLException;

public class TextDB {
	private CrawlerDBManager dbManager;
	
	public TextDB() throws ClassNotFoundException, SQLException{
		this.dbManager = new CrawlerDBManager();
	}
	
	public void updateTextFromHTML() throws SQLException{
		this.dbManager.updateTextFromHTML();
	}
	public void saveTextToDB(String sourcePath){
		try{
			BufferedReader reader = null;
			this.dbManager.initTextBatch();
			// read the data files one by one(file name starts with 1)
			for(int i = 1; i <= 118000; i++){
				if( i % 1000 == 0){
					System.out.println("Saving into database: " + i);
					this.dbManager.commitBatch();
					System.out.println("Saved into database: " + i);
				}
				try{
					reader = new BufferedReader(new FileReader(sourcePath + Integer.toString(i) + ".txt"));
				}catch(IOException e){
					System.out.println("File " + i +".txt is missing. Skipped");
					continue;
				}
				String line = "";
				String url = "";
				String contents = "";
				// get the first line of the file(url)
				if((line = reader.readLine()) != null){
					url = line;
				}
				else{
					System.out.println("File " + i +".txt is empty. Skipped");
					continue;
				}
				// read the contents of the file 
			    while ((line = reader.readLine()) != null){
					contents += line.trim().replaceAll("[\\W]+", " ").toLowerCase();
			    }
			    // add batch
			    this.dbManager.addTextInsertBatch(i, url, contents);
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void closeDB(){
		this.dbManager.close();
	}

}
