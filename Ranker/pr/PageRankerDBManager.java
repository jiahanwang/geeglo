package ir.assignment.pr;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Connection;
import java.sql.SQLException;

public class PageRankerDBManager {
	
	private Connection connection;
	private PreparedStatement preparedStatement;
	private String pageRankTableName;
	private String htmlTableName;

	public PageRankerDBManager() throws ClassNotFoundException, SQLException{
		this.pageRankTableName = "page_rank_score";
		this.htmlTableName = "crawl_data_html";
		Class.forName("com.mysql.jdbc.Driver");
		String url  = "jdbc:mysql://localhost:3306/inf225";
		String user = "root";
		String password = "1990623";
		this.connection = DriverManager.getConnection(url, user, password);
		this.connection.setAutoCommit(false);
	}
	
	public String getUrlByDocId(int doc_id) throws SQLException{
		String sql = "SELECT URL FROM " + this.htmlTableName + " WHERE DOC_ID = " + doc_id;
		ResultSet resultSet = this.connection.createStatement().executeQuery(sql);
		if(resultSet.next()){
			//System.out.println(resultSet.getString("URL"));
			return resultSet.getString("URL");
		}
		return null;
	}
	
	public boolean containsUrl(String url) throws SQLException{
		String sql = "SELECT DOC_ID FROM " + this.htmlTableName + " WHERE URL = '" + url + "';";
		ResultSet resultSet = this.connection.createStatement().executeQuery(sql);
		if(resultSet.next()){
			return true;
		}
		else
			return false;
	}
	
	
	public void initPageRankInsertBatch() throws SQLException{
		String insertTableSQL = "INSERT INTO " + this.pageRankTableName + "(URL, SCORE) VALUES" + "(?,?)";	
		this.preparedStatement = this.connection.prepareStatement(insertTableSQL);
	}
	
	public void addPageRankInsertBatch(String url, double score) throws SQLException{
		this.preparedStatement.setString(1, url);
		this.preparedStatement.setDouble(2, score);
		this.preparedStatement.addBatch();
	}
	
	public void updatePageRankFromHTML() throws SQLException{
		// case sensitive !!!
		String updateSQL = "UPDATE  " + this.pageRankTableName + " SET DOC_ID = ? WHERE BINARY URL = ?;";	
		this.preparedStatement = this.connection.prepareStatement(updateSQL);
		Statement statement = this.connection.createStatement();
		for(int i = 0; i <= 118000; i++){
			if(i % 1000 == 0){
				String sql = "SELECT DOC_ID, URL FROM " + this.htmlTableName + " WHERE DOC_ID > " + i + " AND DOC_ID <= " + (i + 1000);
				ResultSet resultSet = statement.executeQuery(sql);
				while(resultSet.next()){
					System.out.println(resultSet.getString("DOC_ID"));
					this.preparedStatement.setInt(1, resultSet.getInt("DOC_ID"));
					this.preparedStatement.setString(2, resultSet.getString("URL"));
					this.preparedStatement.addBatch();
				}
				this.commitBatch();
				System.out.println("Updated: " + i);
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
