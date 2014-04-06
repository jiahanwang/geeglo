package ir.assignment.indexer;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.Map;

public abstract class Indexer {
	protected IndexerDBManager dbManager;
	
	public Indexer(String tableType) throws ClassNotFoundException, SQLException{
		this.dbManager = new IndexerDBManager(tableType);
	}
	
	// print the whole index into a file
	public void printIntoFile(String outputPath, Index index) throws IOException{
		Iterator<Map.Entry<String, IndexItem>> indexIterator = index.getIndex().entrySet().iterator();
		BufferedWriter writer = new BufferedWriter(new FileWriter(outputPath));
	    while (indexIterator.hasNext()) {
	    	Map.Entry<String, IndexItem> pair = (Map.Entry<String, IndexItem>)indexIterator.next();
	    	String output = pair.getValue().toString();
	    	writer.write(output);
	    	writer.newLine();
			writer.flush();
	    }
	    writer.flush();
	    writer.close();
	}
	
	// save the index into the database
	public void saveIntoDataBase(Index index) throws Exception{
		if(this.dbManager == null)
			throw new Exception("DataBase Error");
		Iterator<Map.Entry<String, IndexItem>> indexIterator = index.getIndex().entrySet().iterator();
	    while (indexIterator.hasNext()) {
	    	Map.Entry<String, IndexItem> pair = (Map.Entry<String, IndexItem>)indexIterator.next();
	    	if(this.dbManager.containsIndexItem(pair.getKey()))
	    		this.dbManager.updateIndexItem(pair.getKey(), pair.getValue());
	    	else
	    		this.dbManager.insertIndexItem(pair.getValue());
	    	//System.out.println(pair.getKey());
	    }
	}
	
	// close database
	public void closeDataBase(){
		this.dbManager.close();
	}
	
	// abstract method buildIndexFromDataBase
	public abstract void buildIndexFromDataBase(); 
	
	// abstract method buildIndexFromFiles
	public abstract void buildIndexFromFiles(String source_path); 
	
	/*
	public void updateDataBaseByTfIdf(){
		this.dbManager.updateTablesByTfIdf();
	}*/

}
