package com.geeglo.servlets;

public class ResultDocument {
	private int docId;
	private String url;
	private String title;
	private String snippet;
	
	ResultDocument(int docId){
		this.docId = docId;
		this.url = "";
		this.title = "";
		this.snippet = "";
	}
	
	public int getDocId() {
		return docId;
	}
	public void setDocId(int docId) {
		this.docId = docId;
	}
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getSnippet() {
		return snippet;
	}
	public void setSnippet(String snippet) {
		this.snippet = snippet;
	}

}
