package com.geeglo.servlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.geeglo.query.DBManager;
import com.geeglo.query.Document;
import com.geeglo.query.QueryOptimized;
import com.google.gson.Gson;

@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	private DBManager dbManager;
	private QueryOptimized query_processor;
	private Gson gson;
	
       
    public QueryServlet() {
        super();
        this.gson = new Gson();
        this.query_processor = new QueryOptimized();
        try {
			this.dbManager = new DBManager("data_text");
		} catch (Exception e) {
			this.dbManager = null;
			e.printStackTrace();
		}
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		long start = System.currentTimeMillis();
		ResponseResult final_result = new ResponseResult();
		try{
			String query = request.getParameter("query");
			List<String> query_terms = query_processor.parseQuery(query);
			// call queryTextOptimized to get the sorted document list
			List<Document> document_list = query_processor.queryTextOptimized(query);
			//List<Document> document_list = null;
			if(document_list.size() == 0){
				// no matching documents
				final_result.setSuccess(true);
				final_result.setTerms(query_terms);
			}else {
				// get title and snippet of every document
				List<ResultDocument> document_list_to_return = new ArrayList<ResultDocument>();
				for(Iterator<Document> iterator = document_list.iterator(); iterator.hasNext();){
					Document document = iterator.next();
					ResultDocument result_document =  new ResultDocument(document.getDocId());
					String[] details = this.dbManager.getDetailsByDocId(document.getDocId());
					if(details != null){
						// set url 
						result_document.setUrl(details[0]);
						// set title
						result_document.setTitle(details[1]);
						// deal with the snippet
						String[] words = details[2].split("[\\s+]");
						int words_number = words.length;
						for(int i = 0; i < words_number; i++){
							if(query_terms.contains(words[i])){
								String[] snippet_array = Arrays.copyOfRange(words, (i - 20 < 0 ? 0 : i - 20), (i + 20 > words_number ? words_number : i + 20));
								String snippet = "";
								int snippet_number = snippet_array.length;
								for(int j = 0; j < snippet_number; j++)
									snippet += snippet_array[j] + " ";
								result_document.setSnippet(snippet);
								break;
							}
						}
					}
					document_list_to_return.add(result_document);
				}
				// build the final result
				final_result.setSuccess(true);
				final_result.setTerms(query_terms);
				final_result.setDocuments(document_list_to_return);
			}
		}catch (Exception e){
			// query failed
			final_result.setSuccess(false);
			e.printStackTrace();
		}
		// transform to JSON and return
		final_result.setProcess_time((System.currentTimeMillis() - start));
		PrintWriter out = response.getWriter();
	    out.println(this.gson.toJson(final_result));
	}
		
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String query = request.getParameter("query");
		PrintWriter out = response.getWriter();
	    out.println("Only get request will be responsed: " + query);
	}

}
