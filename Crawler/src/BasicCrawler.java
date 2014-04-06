package ir.assignment.crawler;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;
import java.util.List;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.http.Header;
import org.apache.log4j.*;

public class BasicCrawler extends WebCrawler {
	Logger logger = LogManager.getLogger(BasicCrawler.class.getName());
	private final static String XML_PATH = "crawlData.xml";
	private StaxWriter staxWriter;
	private final static Pattern FORMAT_FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
			+ "|wav|avi|mov|mpeg|ram|m4v|pdf|ppt.*|xls.*|doc.*" + "|rm|smil|wmv|swf|wma|zip|rar|gz|data|name))$");
	private final static Pattern DOMAIN_FILTERS = Pattern.compile("http://((.*\\.ics\\.uci\\.edu)|(ics\\.uci\\.edu)).*");
	// Lower Case!!!
	private final static String[] BLACK_LIST = {"http://calendar.ics.uci.edu/calendar.php?", 
												"http://archive.ics.uci.edu/ml/datasets.html?",
												"http://archive.ics.uci.edu/ml/machine-learning-databases/",
												//"http://fano.ics.uci.edu/ca/rules/b",
												"http://djp3-pc2.ics.uci.edu/lucicoderepository/nomaticim/browser/",
												"http://drzaius.ics.uci.edu/cgi-bin/",
												"http://wics.ics.uci.edu/events/2",
												//"http://wics.ics.uci.edu/events/upcoming/?"
												};
	@Override
	public void onStart(){
		try{
			this.staxWriter = StaxWriter.instance(XML_PATH);
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	@Override
	public boolean shouldVisit(WebURL url) {
		String href = url.getURL().toLowerCase();
		// if the domain is in the black list
		for(String black_domain : BLACK_LIST){
			if(href.startsWith(black_domain)){
				System.out.println("BLACK_LIST SKIP: " + href + "\n" + "=============");
				return false;
			}
		}
		// if the format of the file is wrong
		if (FORMAT_FILTERS.matcher(href).matches()) {
			//System.out.println("FORMAT SKIP: " + href + "\n" + "=============");
			return false;
		}
		// if the domain is not ics.uci.edu
		if (!DOMAIN_FILTERS.matcher(href).matches()) {
			//System.out.println("DOMAIN SKIP: " + href + "\n" + "=============");
			return false;
		}
		return true;
	}

	@Override
	public void visit(Page page) {
		logger.info("DOCID: " + page.getWebURL().getDocid() + "  URL: " + page.getWebURL().getURL());
		if (page.getParseData() instanceof HtmlParseData) {
			// Set basic path information
			XMLPageElement xmlPageElement = new XMLPageElement();
			xmlPageElement.setUrl(page.getWebURL().getURL());
			xmlPageElement.setDomain(page.getWebURL().getDomain());
			xmlPageElement.setSub_domain(page.getWebURL().getSubDomain());
			xmlPageElement.setPath(page.getWebURL().getPath());
			xmlPageElement.setAnchor_text(page.getWebURL().getAnchor());
			xmlPageElement.setParent_page(page.getWebURL().getParentUrl());
			// Set content information
			HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
			String text = htmlParseData.getText();
			String html = StringEscapeUtils.escapeHtml4(htmlParseData.getHtml().replaceAll("\\s+", " "));
			List<WebURL> links = htmlParseData.getOutgoingUrls();
			xmlPageElement.setNumber_of_outgoing_links(links.size());
			xmlPageElement.setText_length(text.length());
			xmlPageElement.setHtml_length(html.length());
			xmlPageElement.setContents(html);
			// Set response header information
			Header[] responseHeaders = page.getFetchResponseHeaders();
			String headerString = "";
			if (responseHeaders != null) {
				for (Header header : responseHeaders) {
					headerString  += header.getName() + ": " + header.getValue() + "; ";
				}
			}
			xmlPageElement.setResponse_header(headerString);
			// Write this element into the file
			try{
				staxWriter.saveXMLPageElement(xmlPageElement);
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}	
}
