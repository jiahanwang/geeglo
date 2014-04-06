package ir.assignment.query;


import ir.assignment.indexer.IndexItem;
import ir.assignment.utilities.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


public class QueryOptimized {
	private QueryDBManager dbManager1;
	private QueryDBManager dbManager2;
	private QueryDBManager dbManagerPageRank;
	private final int collection_size = 76825;
	private Map<String, IndexItem> resultSet;
	private Map<String, IndexItem> resultSetTwoGram;
	
	public QueryOptimized(){
		try{
			dbManager1 = new QueryDBManager("one_gram");
			dbManager2 = new QueryDBManager("two_gram");
			dbManagerPageRank = new QueryDBManager("page_rank");
			resultSet = new TreeMap<String, IndexItem>();
			resultSetTwoGram = new TreeMap<String, IndexItem>();
		}catch(SQLException e){
			e.printStackTrace();
		}catch(ClassNotFoundException e){
			e.printStackTrace();
		}
	}
	
	/*
	 * lookup: get the index item
	 * 		   if no such index item return NULL
	 */
	private IndexItem lookup(String term){
		term = term.toLowerCase();
		if(!this.resultSet.containsKey(term)){
			IndexItem resultSet = dbManager1.getIndexItem(term);
			this.resultSet.put(term, resultSet);
		}
		return this.resultSet.get(term);
	}
	
	/*
	 * lookupDocumentSet: get document set for this term
	 * 					  if no such document return an EMPTY MAP
	 */
	private Map<Integer,Document> lookupDocumentSet(String term){
		IndexItem resultSet = lookup(term);
		Map<Integer, Document> documentSet = new HashMap<Integer,Document>();
		if(resultSet != null){
			for(int docId :resultSet.getPostings().keySet()){
					Document doc = new Document();
					doc.setDocId(docId);
					//doc.setUrl(resultSet.getPosting(docId).getUrl());
					doc.addTf_idf(term, resultSet.getPosting(docId).getTfIdf());
					doc.addPositionList(term, resultSet.getPosting(docId).getPositionList());
					documentSet.put(docId, doc);
			}
		}
		return documentSet;
	}
	
	/*
	 * lookupTwoGram: get the index item for this two-gram
	 * 		   		  if no such index item return NULL
	 */
	private IndexItem lookupTwoGram(String term){
		term = term.toLowerCase();
		if(!resultSetTwoGram.containsKey(term)){
			IndexItem resultSet = dbManager2.getIndexItem(term);
			resultSetTwoGram.put(term, resultSet);
		}
		return resultSetTwoGram.get(term);
	}
	
	/*
	 * lookupDocumentSetTwoGram: get document set for this two gram
	 * 					  if no such document return an EMPTY MAP
	 */
	private Map<Integer,Document> lookupDocumentSetTwoGram(String term){
		IndexItem resultSet = lookupTwoGram(term);
		HashMap<Integer,Document> documentSet = new HashMap<Integer,Document>();
		if(resultSet != null){
			for(int docId :resultSet.getPostings().keySet()){
				Document doc = new Document();
				doc.setDocId(docId);
				//doc.setUrl(resultSet.getPosting(docId).getUrl());
				doc.addTf_idf(term, resultSet.getPosting(docId).getTfIdf());
				doc.addPositionList(term, resultSet.getPosting(docId).getPositionList());
				documentSet.put(docId, doc);
			}
		}
		return documentSet;
	}	
	
	/*
	 * queryAND: return unsorted result set of documents: term1 AND term2
	 * 			 if any of the two terms doesn't exist, return an EMPTY MAP
	 */
	private Map<Integer, Document> queryAND(String term1, String term2){
		Map<Integer,Document> documentSet1 = lookupDocumentSet(term1);
		Map<Integer,Document> documentSet2 = lookupDocumentSet(term2);
		return queryANDDocument(documentSet1, documentSet2);
	}
	
	/*
	 * queryANDDocument: intersect two document sets
	 * 
	 */
	private Map<Integer, Document> queryANDDocument(Map<Integer, Document> documentSet1, Map<Integer, Document> documentSet2){
		if(documentSet1.size() == 0 || documentSet2.size() == 0)
			return new HashMap<Integer,Document>();
		Map<Integer, Document> resultSet = new HashMap<Integer, Document>();
		// make sure documentSet1 is the smaller one
		if(documentSet1.size() > documentSet2.size()){
			Map<Integer,Document> temp;
			temp = documentSet1;
			documentSet1 = documentSet2;
			documentSet2 = temp;
		}
		for(int docId : documentSet1.keySet()){
			if(documentSet2.containsKey(docId)){
				Document document = documentSet1.get(docId);
				document.addtfIdfs(documentSet2.get(docId).tfIdfMap());
				document.addPositionLists(documentSet2.get(docId).positionListMap());
				resultSet.put(docId, document);
			}
		}
		return resultSet;
	}
	
	/*
	 * queryOR: return the unsorted result set of documents of term1 OR term2
	 * 			if both terms don't exist, return an EMPTY MAP, otherwise return the non-null one
	 */
	private Map<Integer,Document> queryOR(String term_1, String term_2){
		Map<Integer,Document> documentSet1 = lookupDocumentSet(term_1);
		Map<Integer,Document> documentSet2 = lookupDocumentSet(term_2);
		return queryORDocument(documentSet1, documentSet2);
	}
	
	/*
	 * queryORDocument: union two document sets
	 * 
	 */
	private Map<Integer, Document> queryORDocument(Map<Integer, Document> documentSet1, Map<Integer, Document> documentSet2){
		if(documentSet1.size() == 0)
			return documentSet2;
		else if(documentSet2.size() == 0)
				return documentSet1;
		Map<Integer, Document> resultSet = new HashMap<Integer, Document>();
		// make sure documentSet1 is the smaller one
		if(documentSet1.size() > documentSet2.size()){
			Map<Integer,Document> temp;
			temp = documentSet1;
			documentSet1 = documentSet2;
			documentSet2 = temp;
		}
		List<Integer> docIdList = new ArrayList<Integer>(documentSet1.keySet());
		for(int docId : docIdList){
			if(documentSet2.containsKey(docId)){
				Document document = documentSet1.get(docId);
				document.addtfIdfs(documentSet2.get(docId).tfIdfMap());
				document.addPositionLists(documentSet2.get(docId).positionListMap());
				// put the same one into result set
				resultSet.put(docId, document);
				// remove the same one from both sets
				documentSet1.remove(docId);
				documentSet2.remove(docId);
			}
		}
		// put the rest into result set
		resultSet.putAll(documentSet1);
		resultSet.putAll(documentSet2);
		return resultSet;
	}
	
	/*
	 * parseQuery: parse the text input query into List<String> 
	 * 
	 */
	private ArrayList<String> parseQuery(String queryText){
		ArrayList<String> querywords = new ArrayList<String>();
		String delimitersRegex = "[^1-9a-zA-Z]";
		
		for (String str: queryText.split(delimitersRegex)){					
			if(str.length() > 0 && (Utilities.contains(str)==false)){
				querywords.add(str.toLowerCase());
			}
		}
		return querywords;
	}
	
	/*
	 * getQueryTdIdf
	 * 
	 */
	private Map<String, Double> getQueryTdIdf(List<Frequency> querywordfre){
		TreeMap<String, Double> query_tdIdf = new TreeMap<String, Double>();
		for(Frequency fre: querywordfre){
			IndexItem indexItem = this.resultSet.get(fre.getText());
			if(indexItem == null) continue;
			int df = indexItem.getDf();
			double tf_idf = Math.log10(1 + fre.getFrequency()) * ( Math.log10(collection_size)-Math.log10(df + 1));
			query_tdIdf.put(fre.getText(), tf_idf);
		}
		return query_tdIdf;
	}
	
	/*
	 * computeSimilarity
	 * 
	 */
	private double computeSimilarity(Map<String, Double> query, Map<String, Double> document){
		// assume query always has all the terms
		double normalize_base_query = 0;
		double normalize_base_document = 0;
		double cosine = 0;
		for(String term : query.keySet()){
			double query_tf_idf = query.get(term);
			normalize_base_query += Math.pow(query_tf_idf, 2);
			if(document.containsKey(term)){
				double document_tf_idf = document.get(term);
				normalize_base_document += Math.pow(document_tf_idf, 2);
				cosine += query_tf_idf * document_tf_idf;
			}
			else{
				//normalize_base_document += 0;
				//cosine += query_tf_idf * 0;
			}
		}
		cosine = cosine/(normalize_base_query * normalize_base_document);
		return cosine;
	}
	
	/*
	 * sortByPageRankAndTfIdf: sort the list of document by page_rank and tf_idf
	 * 
	 */
	private List<Document> sortByPageRankAndTfIdf(List<Document> resultDocList){
		Collections.sort(resultDocList, new Comparator<Document>(){
			@Override
			public int compare(Document  a, Document  b){
				double re = 10000 * b.getPagerank() * b.getFirstTfIdf() - 10000 * a.getPagerank() * a.getFirstTfIdf();
				if(re > 0){
					return 1;
				}else if(re == 0){
					return 0;
				}else{
					return -1;
				} 
			}
		});	
		return resultDocList;
	}
	
	/*
	 * sortByPageRankAndCosine: sort the list of document by page_rank and cosine
	 * 
	 */
	private List<Document> sortByPageRankAndCosine(List<Document> resultDocList){
		Collections.sort(resultDocList, new Comparator<Document>(){
			@Override
			public int compare(Document  a, Document  b){
				double re = 1000 * b.getPagerank() * b.getCosine() - 1000 * a.getPagerank() * a.getCosine();				if(re > 0){
					return 1;
				}else if(re == 0){
					return 0;
				}else{
					return -1;
				} 
			}
		});	
		return resultDocList;
	}
	
	/*
	 * sortByCosine: sort the list of document by cosine
	 * 
	 */
	private List<Document> sortByCosine(List<Document> resultDocList){
		Collections.sort(resultDocList, new Comparator<Document>(){
			@Override
			public int compare(Document  a, Document  b){
				double re = b.getCosine() - a.getCosine();			
			if(re > 0){
					return 1;
				}else if(re == 0){
					return 0;
				}else{
					return -1;
				} 
			}
		});	
		return resultDocList;
	}
	
	/*
	 * sortByTfIdf: sort the list of document by tf-idf of a term
	 * 
	 */
	private List<Document> sortByTfIdf(List<Document> resultDocList, final String term){
		Collections.sort(resultDocList, new Comparator<Document>(){
			@Override
			public int compare(Document  a, Document  b){
				double re = b.getTfIdf(term) - a.getTfIdf(term);			
			if(re > 0){
					return 1;
				}else if(re == 0){
					return 0;
				}else{
					return -1;
				} 
			}
		});	
		return resultDocList;
	}
	
	/*
	 * queryOneGram: return sorted result list of documents
	 * 					first: one term
	 * 				 sorted by page_rank and tf-idf
	 * 				 if no such one-gram, return an Empty List
	 */
	private List<Document> queryOneGram(List<String> query_words){
		// get frequency of the query
		List<Frequency> query_word_fre = WordFrequencyCounter.computeWordFrequencies(query_words);
		WordFrequencyCounter.printFrequencies(query_word_fre);
		// retrieve documents from the index
		List<Document> resultDocSet = new ArrayList<Document>(lookupDocumentSet(query_words.get(0)).values());
		if(resultDocSet.size() != 0){		
			/* sort by tf_idf to get the top 100 */
			// get page_rank for every document
			for(Document document: resultDocSet){
				try{
					double pagerank = dbManagerPageRank.getScoreByDocId(document.getDocId());
					document.setPagerank(pagerank);
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			// sort by (page_rank and tf_idf)
			sortByPageRankAndTfIdf(resultDocSet);
		}
		return resultDocSet;
	}
	
	/*
	 * queryTwoGram: return sorted list of documents: two gram query
	 * 				 	first: two-gram 	  		 
	 * 				 	second: term1 AND term2 		
	 * 				 	third: term1 OR term2
	 * 				 sorted by page_rank and cosine
	 * 				 if none of the two terms exist,return an Empty List
	 * 
	 */	
	private List<Document> queryTwoGram(List<String> query_words){
		// get frequency of the query
		List<Frequency> query_word_fre = WordFrequencyCounter.computeWordFrequencies(query_words);
		WordFrequencyCounter.printFrequencies(query_word_fre);
		// retrieve from two-gram index
		Map<Integer,Document> result_two_gram = lookupDocumentSetTwoGram(query_words.get(0) + " " + query_words.get(1));
		// if number of documents in two-gram index is smaller than 100, supplement result set by adding one-gram term1 AND one-gram term2
		if(result_two_gram.size() >= 100){
			List<Document> document_final_list = new ArrayList<Document>(result_two_gram.values());
			// get page rank
			for(Document document: document_final_list){
				try{
					double pagerank = dbManagerPageRank.getScoreByDocId(document.getDocId());
					document.setPagerank(pagerank);
				}catch(SQLException e){
					e.printStackTrace();
				}
			}
			// sort by page_rank and tf_idf
			this.sortByPageRankAndTfIdf(document_final_list);
			return document_final_list;
			
		}else{
			result_two_gram = this.queryAND(query_words.get(0), query_words.get(1));
		}
		// if still smaller than 100, supplement result set by adding one-gram term1 OR one-gram term2
		if(result_two_gram.size() < 100)
			result_two_gram = this.queryOR(query_words.get(0), query_words.get(1));
		List<Document> document_final_list = new ArrayList<Document>(result_two_gram.values());
		// compute cosine for every document
		for(Document doc: document_final_list){
			double cosine_score = computeSimilarity(getQueryTdIdf(query_word_fre), doc.getTfIdfs());
			doc.setCosine(cosine_score);
		}
		// sort the list by cosine and get the top 100
		// sortByCosine(document_final_list);
		// document_final_list  = document_final_list.size() < 100 ? document_final_list: document_final_list.subList(0, 100);
		// get the page_rank for top 100
		for(Document document: document_final_list){
			try{
				double pagerank = dbManagerPageRank.getScoreByDocId(document.getDocId());
				document.setPagerank(pagerank);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		// sort by page_rank and cosine
		sortByPageRankAndCosine(document_final_list);
		return document_final_list;
	}
	
	/* 
	 * queryMoreGram: return sorted list of documents: two gram query
	 * 				  first: term1 AND term2 AND term3 AND term4 AND ...	  		 
	 * 				  second: term1 OR term2 OR term3 OR term4 OR ...
	 * 				  sorted by page_rank and cosine
	 * 				  if none of the terms exist ,return an Empty List
	 */
	private List<Document> queryMoreGram(List<String> query_words){
		// get the frequency of the query
		List<Frequency> query_word_fre = WordFrequencyCounter.computeWordFrequencies(query_words);
		WordFrequencyCounter.printFrequencies(query_word_fre);
		// term1 AND term2 AND term3 AND ...
		Map<Integer, Document> documentResultSet = lookupDocumentSet(query_words.get(0));
		for(int i = 1; i < query_words.size() - 1; i++){
			documentResultSet = queryANDDocument(documentResultSet, lookupDocumentSet(query_words.get(i)));
		}
		// if number of documents less than 100, term1 OR term2 OR term3 OR ...
		if(documentResultSet.size() < 100){
			for(int i = 1; i < query_words.size() - 1; i++){
				documentResultSet = queryORDocument(documentResultSet, lookupDocumentSet(query_words.get(i)));
			}
		}
		List<Document> document_final_list = new ArrayList<Document>(documentResultSet.values());
		// compute cosine for every document
		for(Document doc: document_final_list){
			double cosine_score = computeSimilarity(getQueryTdIdf(query_word_fre), doc.getTfIdfs());
			doc.setCosine(cosine_score);
		}
		// sort the list by cosine and get the top 100
		// sortByCosine(document_final_list);
		// document_final_list  = document_final_list.size() < 100 ? document_final_list: document_final_list.subList(0, 100);
		// get the page_rank
		for(Document document: document_final_list){
			try{
				double pagerank = dbManagerPageRank.getScoreByDocId(document.getDocId());
				document.setPagerank(pagerank);
			}catch(SQLException e){
				e.printStackTrace();
			}
		}
		// sort by page_rank and cosine
		sortByPageRankAndCosine(document_final_list);
		return document_final_list;
	}
	
	/*
	 * queryTextOptimized
	 * 
	 */
	public List<Document> queryTextOptimized(String queryText){
		// parse the query
		List<String> query_words = parseQuery(queryText);
		// check all the terms if they exist in index, if not delete it in the query
		for(Iterator<String> it = query_words.iterator(); it.hasNext();){
			Map<Integer,Document> resultSet = lookupDocumentSet(it.next());
			if(resultSet.size() == 0)
				it.remove();
		}
		List<Document> document_final_list = new ArrayList<Document>();
		if(query_words.size() == 1){
			document_final_list = queryOneGram(query_words);
		}
		if(query_words.size() == 2){
			document_final_list = queryTwoGram(query_words);
		}
		if(query_words.size() > 2){
			document_final_list = queryMoreGram(query_words);
		}
		return document_final_list.size() < 30 ? document_final_list: document_final_list.subList(0, 30);
	}
	
	public static void main(String[] args) {
		QueryOptimized test = new QueryOptimized();
		String query = "mondego";
		System.out.println(query+": ");
		long start = System.currentTimeMillis();
		List<Document> resultSet = test.queryTextOptimized(query);
		for(int i = 0;i < resultSet.size() && i<30;i++){
			System.out.println("Url:" + i + "," + resultSet.get(i).getDocId());
		}
		long stop = System.currentTimeMillis();
		long time = stop - start;
		System.out.println("time: "+time);
	}
}	


