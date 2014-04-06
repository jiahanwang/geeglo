package ir.assignment.indexer;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

public class Index {
	
	private TreeMap<String, IndexItem> index; //TreeMap:terms are stored in alphabetical order
	
	// contructor
	public Index(){
		this.index = new TreeMap<String, IndexItem>();
	}
	
	// index getter
	public TreeMap<String, IndexItem> getIndex() {
		return index;
	}

	// check if term exists in this index
	public boolean containsTerm(String term){
		if(index.isEmpty())
			return false;
		return index.containsKey(term);
	}
	
	// get a term-posting pair from this index
	public IndexItem getIndexItem(String term){
		if(this.containsTerm(term))
			return index.get(term);
		else
			return null;
	}
	
	// add new term-posting pair into the index
	public boolean addIndexItem(String term, Posting newPosting){
		// if this term already exists, return false
		if(this.containsTerm(term))
			return false;
		else{
			//every term is inserted into the index in alphabetical order
			IndexItem newIndexItem = new IndexItem();
			newIndexItem.setTerm(term);
			newIndexItem.addPosting(newPosting);
			index.put(term, newIndexItem);
			return true;
		}
	}
	
	// update term-posting pair in this index by adding new posting item to this pair
	public boolean updateIndexItemByAddingPosting(String term, Posting newPosting){
		return this.getIndexItem(term).addPosting(newPosting);
	}
	
	// update the whole index to calculate the tdf-idf
	public void updateIndexByTfIdf(){
		Iterator<Map.Entry<String, IndexItem>> indexIterator = this.index.entrySet().iterator();
		int totalNumber  = this.index.size();
	    while (indexIterator.hasNext()) {
	    	Map.Entry<String, IndexItem> pair = (Map.Entry<String, IndexItem>)indexIterator.next();
	        pair.getValue().updateTfIdf(totalNumber);
	    }
	}
	
	// clear this index
	public void clear(){
		this.index = null;
		this.index = new TreeMap<String, IndexItem>();
	}
}
