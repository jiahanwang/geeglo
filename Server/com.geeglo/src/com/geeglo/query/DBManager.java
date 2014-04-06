package com.geeglo.query;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DBManager {
	
	private Connection connection;
	private Statement statement;
	private String tableNameBase;
	private String[] tableIdentifiers = {"a","h","o","2"};
	private int[] tableTotalNumbers = {0, 0, 0, 0};
	private Pattern postingFilter =  Pattern.compile("\\{[^\\{]+\\}");
	private Pattern postingValuesFilter = Pattern.compile("\\d+[\\.]*\\d*");
	private final int collectionSize = 76825;
	
	// tableType can be "one_gram" or "two_gram" or "page_rank"
	public DBManager(String tableType) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		String url  = "jdbc:mysql://localhost:3306/inf225";
		String user = "root";
		String password = "1990623";
		this.connection = DriverManager.getConnection(url, user, password);
		this.statement = this.connection.createStatement();
		if(tableType.equals("page_rank"))
			this.tableNameBase = "page_rank_score";
		else if(tableType.equals("data_text"))
				this.tableNameBase = "crawl_data_text";
			 else
				this.tableNameBase = tableType + "_index_";
	}
	
	private String decideTable(String term){
		char first = term.charAt(0);
		if( first >= 'a' && first <= 'g' )
			return this.tableNameBase + "a-g";
		else 
			if(first >= 'h' && first <= 'n')
				return this.tableNameBase + "h-n";
			else
				if(first >= 'o' && first <= 'z')
					return this.tableNameBase + "o-z";
				else
					return this.tableNameBase + "others";
	}
	
	private ResultSet getIndexItemInResultSet(String term){
		if(term == null || term.length() == 0)
			return null;
		try {
			String sql = "SELECT * FROM `" + decideTable(term) + "` WHERE term = '" + term + "'";
			ResultSet resultSet = this.statement.executeQuery(sql);
			return resultSet;
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public boolean containsIndexItem(String term){
		if(term == null || term.length() == 0)
			return false;
		try {
			String sql = "SELECT * FROM `" + decideTable(term) + "` WHERE term = '" + term + "';";
			ResultSet resultSet = this.statement.executeQuery(sql);
			if(resultSet.next()){
				resultSet.close();
				return true;
			}
			resultSet.close();
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public int getNumberOfRows(){
		int totalNumber = 0;
		for (int i = 0; i < this.tableIdentifiers.length; i++){
			try {
				String sql = "SELECT COUNT(1) AS count FROM `" + decideTable(this.tableIdentifiers[i]) + "`;";
				ResultSet resultSet = this.statement.executeQuery(sql);
				while(resultSet.next()){
					System.out.println(this.tableIdentifiers[i] + ": "+ resultSet.getInt("count"));
					this.tableTotalNumbers[i] = resultSet.getInt("count");
					totalNumber += this.tableTotalNumbers[i];
				}
			} catch (Exception e) {
				e.printStackTrace();
				return 0;
			}
		}
		return totalNumber;
	}
	
	public IndexItem getIndexItem(String term){
		try {
			int totalNumber  = this.collectionSize;
			ResultSet resultSet = getIndexItemInResultSet(term);
			if(resultSet.next()){
				int df = resultSet.getInt("df");
				IndexItem newIndexItem = new IndexItem();
				newIndexItem.setTerm(term);
				newIndexItem.setDf(df);
				Matcher postingsMatcher = this.postingFilter.matcher(resultSet.getString("postings"));
				while (postingsMatcher.find()){
					try{
						String posting = postingsMatcher.group();
						Matcher matcher= this.postingValuesFilter.matcher(posting);
						int i = 1;
						Posting newPosting = new Posting();
						while(matcher.find()){
							switch(i++){
								case 1: {
									newPosting.setDocId(Integer.parseInt(matcher.group())); 
									break;
								}
								case 2:{
									newPosting.setTf(Integer.parseInt(matcher.group())); 
									break;
								}
								case 3:{
									newPosting.setTfIdf(Math.log10(1 + newPosting.getTf()) * (Math.log10(totalNumber) - Math.log10(df + 1)));
									break;
								}
								default:{
									newPosting.addPosition(Integer.parseInt(matcher.group()));
								}
							}
						}
						newIndexItem.addPosting(newPosting);
					}catch(Exception e){
						e.printStackTrace();
						continue;
					}
				}
				return newIndexItem;
			}else{
				return null;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return null;
		}		
	}
	
	public double getScoreByDocId(int doc_id) throws SQLException{
		String sql= "SELECT DOC_ID, SCORE FROM " + this.tableNameBase + " WHERE DOC_ID = " + doc_id;	
		ResultSet result_set = this.statement.executeQuery(sql);
		if(result_set.next()){
			return result_set.getDouble("SCORE");
		}
		return 0;
	}
	
	public String[] getDetailsByDocId(int doc_id) throws SQLException{
		String sql= "SELECT URL, TITLE, CONTENTS FROM " + this.tableNameBase + " WHERE DOC_ID = " + doc_id;	
		ResultSet result_set = this.statement.executeQuery(sql);
		String[] result = new String[3];
		if(result_set.next()){
			result[0] = result_set.getString("URL");
			result[1] = result_set.getString("TITLE");
			result[2] = result_set.getString("CONTENTS");
			return result;
		}else{
			return result;
		}
		
	}
	
	public void close(){
		try {
			if(this.statement != null)
				this.statement.close();
			if(this.connection != null)
				this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
