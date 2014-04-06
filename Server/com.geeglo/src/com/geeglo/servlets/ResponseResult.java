package com.geeglo.servlets;

import java.util.List;

public class ResponseResult {
	private List<String> terms;
	private float process_time;
	private List<ResultDocument> documents;
	private boolean success = false;
	
	
	public List<String> getTerms() {
		return terms;
	}
	public void setTerms(List<String> terms) {
		this.terms = terms;
	}
	public float getProcess_time() {
		return process_time;
	}
	public void setProcess_time(float process_time) {
		this.process_time = process_time;
	}
	public List<ResultDocument> getDocuments() {
		return documents;
	}
	public void setDocuments(List<ResultDocument> documents) {
		this.documents = documents;
	}
	public boolean getSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	

}
