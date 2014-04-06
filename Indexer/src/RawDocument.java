package ir.assignment.indexer;

public class RawDocument {
	private int doc_id;
	private String title;
	private String header_text;
	private String contents;
	public int getDoc_id() {
		return doc_id;
	}
	public void setDoc_id(int doc_id) {
		this.doc_id = doc_id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getHeader_text() {
		return header_text;
	}
	public void setHeader_text(String header_text) {
		this.header_text = header_text;
	}
	public String getContents() {
		return contents;
	}
	public void setContents(String contents) {
		this.contents = contents;
	}
	
}
