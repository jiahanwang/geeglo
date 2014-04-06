package ir.assignment.query;

import ir.assignment.indexer.Posting;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class Document {
	private int docId;
	//private String url;
	private double cosine;
	private double pagerank;
	private Map<String, Double> tf_idfs;
	private Map<String, List<Integer>> positionList;
	
	public final double getCosine() {
		return cosine;
	}

	public final void setCosine(double cosine) {
		this.cosine = cosine;
	}
	
	public final List<Integer> getPositionList(String term) {
		return positionList.get(term);
	}

	public final void addPositionList(String term, List<Integer> positions) {
		positionList.put(term, positions);
	}

	public final int getDocId() {
		return docId;
	}

	public final void setDocId(int docId) {
		this.docId = docId;
	}

	/*public final String getUrl() {
		return url;
	}

	public final void setUrl(String url) {
		this.url = url;
	}*/

	public final double getPagerank() {
		return pagerank;
	}

	public final void setPagerank(double pagerank) {
		this.pagerank = pagerank;
	}
	
	public Document(){
		this.docId = 0;
		//this.url = null;
		this.tf_idfs = new TreeMap<String, Double>();
		this.positionList = new TreeMap<String, List<Integer>>();
		this.cosine = 0.0;
		this.pagerank = 0.0;
	}
	
	public Document(Posting pos){
		this.docId = pos.getDocId();
		this.tf_idfs = new TreeMap<String, Double>();
		this.positionList = new TreeMap<String, List<Integer>>();
		this.cosine = 0.0;
		this.pagerank = 0.0;
	}
	
	public Document(Document doc){
		this.docId = doc.getDocId();
		//this.url = doc.getUrl();
		this.tf_idfs = new TreeMap<String, Double>(doc.tf_idfs);
		this.positionList = new TreeMap<String, List<Integer>>(doc.positionList);
		this.cosine = doc.getCosine();
		this.pagerank = doc.getPagerank();
	}
	//add term's tf_idf into the Tree Map according to the alphabetical order
	public void addTf_idf(String term, Double tf_idf){
		tf_idfs.put(term, tf_idf);
	}
	
	public double getTfIdf(String term){
		return tf_idfs.get(term);
	}
	
	public Double getFirstTfIdf(){
		List<Double> values_list = new ArrayList<Double>(this.tf_idfs.values());
		if(values_list.size() > 0)
			return values_list.get(0);
		else
			return 0.0;
	}
	
	public Map<String, Double> getTfIdfs(){
		return this.tf_idfs;
	}
	
	public Map<String, List<Integer>> positionListMap(){
		return positionList;
	}
	
	public Map<String, Double> tfIdfMap(){
		return tf_idfs;
	}
	
	public void addPositionLists(Map<String, List<Integer>> m){
		this.positionList.putAll(m);
	}
	
	public void addtfIdfs(Map<String, Double> m){
		this.tf_idfs.putAll(m);
	}
	
}
