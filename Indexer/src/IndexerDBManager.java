package ir.assignment.indexer;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IndexerDBManager {
	
	private Connection connection;
	private Statement statement;
	private String tableNameBase;
	private String[] tableIdentifiers = {"a","h","o","2"};
	private int[] tableTotalNumbers = {0, 0, 0, 0};
	private Pattern postingFilter =  Pattern.compile("\\{[^\\{]+\\}");
	private Pattern postingValuesFilter = Pattern.compile("(?<=\\:).*(?=,)");
	private Pattern positionFilter = Pattern.compile("\\d+");

	public IndexerDBManager(String tableType) throws ClassNotFoundException, SQLException{
		Class.forName("com.mysql.jdbc.Driver");
		String url  = "jdbc:mysql://localhost:3306/inf225";
		String user = "root";
		String password = "1990623";
		this.connection = DriverManager.getConnection(url, user, password);
		this.statement = this.connection.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
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
				return true;
			}
			return false;
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	private int getTotalNumberOfAllTables(){
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
	
	public boolean updateIndexItem(String term, IndexItem indexItem){
		try {
			ResultSet resultSet = getIndexItemInResultSet(term);
			if(resultSet.next()){
				resultSet.updateInt("df", resultSet.getInt("df") + indexItem.getDf());
				resultSet.updateString("postings", resultSet.getString("postings") + indexItem.getPostingsInString());
				resultSet.updateRow();
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public boolean insertIndexItem(IndexItem newIndexItem){
		String term = newIndexItem.getTerm();
		try {
			ResultSet resultSet = getIndexItemInResultSet(term);
			if(!resultSet.next()){
				resultSet.moveToInsertRow();
			    resultSet.updateString("term", term);
			    resultSet.updateInt("df", newIndexItem.getDf());
			    resultSet.updateString("postings", newIndexItem.getPostingsInString());
			    resultSet.insertRow();
			    resultSet.moveToCurrentRow();
				return true;
			}else{
				return false;
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public List<RawDocument> getTextData(int start_doc_id){
		List<RawDocument> documents = new ArrayList<RawDocument>();
		try {
			String sql = "SELECT DOC_ID, TITLE, HEADER_TEXT, CONTENTS FROM CRAWL_DATA_TEXT WHERE DOC_ID > " + start_doc_id + " AND DOC_ID <= " + (start_doc_id + 1000);
			ResultSet result_set = this.statement.executeQuery(sql);
			while(result_set.next()){
				RawDocument new_document = new RawDocument();
				new_document.setDoc_id(result_set.getInt("DOC_ID"));
				new_document.setTitle(result_set.getString("TITLE"));
				new_document.setHeader_text(result_set.getString("HEADER_TEXT"));
				new_document.setContents(result_set.getString("CONTENTS"));
				documents.add(new_document);
			}
			return documents;
		} catch (SQLException e) {
			e.printStackTrace();
			return documents;
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
	
	/*
	public void updateTablesByTfIdf(){
		Pattern posting_filter =  Pattern.compile("\\{[^\\{]+\\}");
		int totalNumber  = getTotalNumberOfAllTables();
		int i = 0;
		ResultSet resultSet = null;
			try {
				int count = this.tableTotalNumbers[i];
				for(int row = 0; row <= count ; row += 100){
					String sql = "SELECT * FROM `" + decideTable(this.tableIdentifiers[i]) + "` WHERE id >= " + row + " AND id < " + (row + 100) + ";";
					System.out.println(sql);
					resultSet = this.statement.executeQuery(sql);
					while(resultSet.next()){
						int df = resultSet.getInt("df");
						String posting = resultSet.getString("posting");
						Matcher matcher = posting_filter.matcher(posting);
						String newPosting = "";
						while (matcher.find()){
							try{
								String postingInString = matcher.group();
								String[] postingItems = postingInString.split(",(?!\\d)");
								int tf = 0;
								String newPostingItem = "";
								for(int j = 0; j <= 4 ; j++){
									if(j == 2)
										tf = Integer.parseInt(postingItems[2].substring(3));
									if(j == 3){
										double tf_idf = Math.log10(1 + tf) * Math.log10(totalNumber/(df + 1));
										newPostingItem = newPostingItem + "tf-idf:" + Double.toString(tf_idf)+ ",";
									}else
										newPostingItem = newPostingItem + postingItems[j] + ",";
								}
								newPosting += newPostingItem.substring(0, newPostingItem.length()-1);
							}catch(Exception e){
								e.printStackTrace();
								continue;
							}
						}
						resultSet.updateString("posting", newPosting);
						resultSet.updateRow();
					}
					if(resultSet != null){
						resultSet.close();
						resultSet = null;
						System.gc();
					}
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}*/
}
