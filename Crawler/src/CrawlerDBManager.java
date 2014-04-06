package ir.assignment.crawler;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

public class CrawlerDBManager {
	
	private Connection connection;
	private PreparedStatement preparedStatement;
	private String textTableName;
	private String htmlTableName;

	public CrawlerDBManager() throws ClassNotFoundException, SQLException{
		this.textTableName = "crawl_data_text";
		this.htmlTableName = "crawl_data_html";
		Class.forName("com.mysql.jdbc.Driver");
		String url  = "jdbc:mysql://localhost:3306/inf225";
		String user = "root";
		String password = "1990623";
		this.connection = DriverManager.getConnection(url, user, password);
		this.connection.setAutoCommit(false);
		
	}
	
	public void initTextBatch() throws SQLException{
		String insertTableSQL = "INSERT INTO " + this.textTableName + "(DOC_ID, URL, CONTENTS) VALUES" + "(?,?,?)";	
		this.preparedStatement = this.connection.prepareStatement(insertTableSQL);
	}
	
	public void addTextInsertBatch(int docId, String url, String contents) throws SQLException{
		this.preparedStatement.setInt(1, docId);
		this.preparedStatement.setString(2, url);
		this.preparedStatement.setString(3, contents);
		this.preparedStatement.addBatch();
	}
	
	public void initHTMLInsertBatch() throws SQLException{
		String insertTableSQL = "INSERT INTO " + this.htmlTableName + "(DOC_ID, TITLE, CONTENTS) VALUES" + "(?,?,?)";	
		this.preparedStatement = this.connection.prepareStatement(insertTableSQL);
	}
	
	public void addHTMLInsertBatch(int docId, String title, String contents) throws SQLException{
		this.preparedStatement.setInt(1, docId);
		this.preparedStatement.setString(2, title);
		this.preparedStatement.setString(3, contents);
		this.preparedStatement.addBatch();
	}
	
	public void initHTMLUpdateBatch() throws SQLException{
		String updateTableSQL = "UPDATE " + this.htmlTableName + " SET TITLE = ?, HEADER_TEXT = ?, TEXT_CONTENTS = ? WHERE DOC_ID = ?";	
		this.preparedStatement = this.connection.prepareStatement(updateTableSQL);
	}
	
	public void addHTMLUpdateBatch(int doc_id, String title, String header_text, String text_contents) throws SQLException{
		this.preparedStatement.setString(1, title);
		this.preparedStatement.setString(2, header_text);
		this.preparedStatement.setString(3, text_contents);
		this.preparedStatement.setInt(4, doc_id);
		this.preparedStatement.addBatch();
	}
	
	public void updateTextFromHTML() throws SQLException{
		String updateSQL = "UPDATE " + this.textTableName + " SET TITLE = ?, HEADER_TEXT = ?, CONTENTS = ? WHERE DOC_ID = ?";	
		this.preparedStatement = this.connection.prepareStatement(updateSQL);
		Statement statement = this.connection.createStatement();
		
		for(int i = 0; i <= 118000; i++){
			if(i % 1000 == 0){
				String sql = "SELECT DOC_ID, TITLE, HEADER_TEXT, TEXT_CONTENTS FROM " + this.htmlTableName + " WHERE DOC_ID > " + i + " AND DOC_ID <= " + (i + 1000);
				ResultSet resultSet = statement.executeQuery(sql);
				while(resultSet.next()){
					System.out.println(resultSet.getInt("DOC_ID"));
					this.preparedStatement.setString(1, resultSet.getString("TITLE"));
					this.preparedStatement.setString(2, resultSet.getString("HEADER_TEXT"));
					this.preparedStatement.setString(3, resultSet.getString("TEXT_CONTENTS"));
					this.preparedStatement.setInt(4, resultSet.getInt("DOC_ID"));
					this.preparedStatement.addBatch();
				}
				this.commitBatch();
			}
		}
	}
	
	public void updateHTMLFromText() throws SQLException{
		String updateSQL = "UPDATE  " + this.htmlTableName + " SET URL = ? WHERE DOC_ID = ?";	
		this.preparedStatement = this.connection.prepareStatement(updateSQL);
		Statement statement = this.connection.createStatement();
		for(int i = 0; i <= 118000; i++){
			if(i % 1000 == 0){
				String sql = "SELECT DOC_ID, URL FROM " + this.textTableName + " WHERE DOC_ID > " + i + " AND DOC_ID <= " + (i + 1000);
				ResultSet resultSet = statement.executeQuery(sql);
				while(resultSet.next()){
					System.out.println(resultSet.getInt("doc_id"));
					this.preparedStatement.setString(1, resultSet.getString("URL"));
					this.preparedStatement.setInt(2, resultSet.getInt("DOC_ID"));
					this.preparedStatement.addBatch();
				}
				this.commitBatch();
			}
		}
	}
	
	public void commitBatch() throws SQLException{
		this.preparedStatement.executeBatch();
		this.connection.commit();
	}

	public void close(){
		try {
			if(this.connection != null)
				this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
