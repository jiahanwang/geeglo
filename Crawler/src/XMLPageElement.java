package ir.assignment.crawler;

public class XMLPageElement {
	private int doc_id;
	private String url;
	private String domain;
	private String sub_domain;
	private String path;
	private String parent_page;
	private String anchor_text;
	private int text_length;
	private int html_length;
	private int word_count;
	private int number_of_outgoing_links;
	private String response_header;
	private String contents;
	
	public int getDoc_id() {
		return doc_id;
	}
	public void setDoc_id(int doc_id) {
		this.doc_id = doc_id;
	}
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	
	public String getSub_domain() {
		return sub_domain;
	}
	public void setSub_domain(String sub_domain) {
		this.sub_domain = sub_domain;
	}
	
	public String getPath() {
		return path;
	}
	public void setPath(String path) {
		this.path = path;
	}
	
	public String getParent_page() {
		return parent_page;
	}
	public void setParent_page(String parent_page) {
		this.parent_page = parent_page;
	}
	
	public String getAnchor_text() {
		return anchor_text;
	}
	public void setAnchor_text(String anchor_text) {
		this.anchor_text = anchor_text;
	}
	
	public int getText_length() {
		return text_length;
	}
	public void setText_length(int text_length) {
		this.text_length = text_length;
	}
	
	public int getHtml_length() {
		return html_length;
	}
	public void setHtml_length(int html_length) {
		this.html_length = html_length;
	}
	
	public int getNumber_of_outgoing_links() {
		return number_of_outgoing_links;
	}
	public void setNumber_of_outgoing_links(int number_of_outgoing_links) {
		this.number_of_outgoing_links = number_of_outgoing_links;
	}
	
	public String getContents() {
		return contents;
	}
	public void setContents(String html_contents) {
		this.contents = html_contents;
	}
	
	public String getResponse_header() {
		return response_header;
	}
	public void setResponse_header(String response_header) {
		this.response_header = response_header;
	}
	public int getWord_count() {
		return word_count;
	}
	public void setWord_count(int word_count) {
		this.word_count = word_count;
	}

}
