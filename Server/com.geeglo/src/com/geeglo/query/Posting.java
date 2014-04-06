package com.geeglo.query;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Posting {
	
	private int docId;
	private int tf;
	private double tfIdf;
	private List<Integer> positionList; //two position list: delta & absolute position

	public Posting() {
		this.docId = 0;
		this.tf = 0;
		this.tfIdf = 0;
		this.positionList = new ArrayList<Integer>();
		//this.deltaList = new ArrayList<Integer>();
	}
	
	// docId functions
	public int getDocId() {
		return docId;
	}
	
	public void setDocId(int docId) {
		this.docId = docId;
	}
	
	// tf functions
	public int getTf() {
		return tf;
	}

	public void setTf(int tdf) {
		this.tf = tdf;
	}
	
	public void incrementTf(){
		this.tf ++;
	}
	
	public void incrementPriorityTf(){
		this.tf += 5;
	}
	// tfIdf functions
	public double getTfIdf() {
		return tfIdf;
	}

	public void setTfIdf(double tfIdf) {
		this.tfIdf = tfIdf;
	}
	
	// positionList functions
	public List<Integer> getPositionList() {
		return positionList;
	}
	
	public void addPosition(int position){
		this.positionList.add(position);
	}
	
	// override toString
	@Override
	public String toString(){
		// no whitespaces
		
		String positionListInString = "[";
		Iterator<Integer> iterator = this.positionList.iterator();
		while(iterator.hasNext())
			positionListInString = positionListInString + iterator.next().toString() + ",";
		positionListInString = positionListInString.substring(0, positionListInString.length() - 1) + "]";			
		return "{doc_id:" + this.docId + ",tf:" + this.tf + ",tf-idf:" + this.tfIdf + ",position_list:" + positionListInString + "}"; 
	}
	/*
	 private void addDeltaPosition(int delta){
		this.deltaList.add(delta);}
	*/
}
