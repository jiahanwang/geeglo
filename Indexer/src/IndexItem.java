package ir.assignment.indexer;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class IndexItem {
	
	private String term;
	private Map<Integer, Posting> postings;
	private int df; //document frequency

	public IndexItem(){
		this.term = null;
		this.postings = new HashMap<Integer, Posting>();
		this.df = 0;
	}
	
	// term functions
	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}
	
	// df functions 
	public int getDf() {
		return df;
	}
	public void setDf(int df) {
		this.df = df;
	}
	
	public void incrementDf(){
		this.df++;
	}
	
	// posting functions
	public Map<Integer, Posting> getPostings() {
		return this.postings;
	}
	
	//get posting: <doc,positionList>
	public Posting getPosting(int docId){
		return this.postings.get(docId);
	}
	public String getPostingsInString(){
		String output = "";
		Iterator<Map.Entry<Integer, Posting>> postingIterator = this.postings.entrySet().iterator();
	    while (postingIterator.hasNext()) {
	    	Map.Entry<Integer, Posting> pair = (Map.Entry<Integer, Posting>)postingIterator.next();
	    	output += pair.getValue().toString();
	    }
	    return output;
	}
	
	// check whether the new posting has existed in the posting
	public boolean containsPosting(int docId){
		if(this.postings.isEmpty())
			return false;
		return this.postings.containsKey(docId);
	}
	
	// add a new posting to the posting
	public boolean addPosting(Posting newPosting){
		// if this doc already exist in this posting, return false
		if(this.containsPosting(newPosting.getDocId())){
			return false;
		}else{
			this.postings.put(newPosting.getDocId(), newPosting);
			// increment df
			this.incrementDf();
			return true;
		}
	}
	
	// update td-idf
	public void updateTfIdf(int totalNumber){
		Iterator<Map.Entry<Integer, Posting>> postingIterator = this.postings.entrySet().iterator();
	    while (postingIterator.hasNext()) {
	    	Map.Entry<Integer, Posting> pair = (Map.Entry<Integer, Posting>)postingIterator.next();
	    	Posting postingItem = pair.getValue();
	    	int tf = postingItem.getTf();
	    	int df = this.getDf();
	        double tdfIdf = Math.log10(1+tf) * Math.log10(totalNumber/(df+1));
	        postingItem.setTfIdf(tdfIdf);
	    }
	}
	
	// toString
	@ Override
	public String toString(){
		String output = this.term + " df:" + Integer.toString(this.df) + " ";
		Iterator<Map.Entry<Integer, Posting>> postingIterator = this.postings.entrySet().iterator();
	    while (postingIterator.hasNext()) {
	    	Map.Entry<Integer, Posting> pair = (Map.Entry<Integer, Posting>)postingIterator.next();
	    	output = output + " " + pair.getValue().toString() + " ";
	    }
	    return output;
	}
	
}
