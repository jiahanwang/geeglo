package ir.assignment.pr;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.*;
import java.util.regex.Pattern;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import edu.uci.ics.jung.algorithms.scoring.PageRank;
import edu.uci.ics.jung.graph.DirectedSparseGraph;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.util.EdgeType;

public class ICSPageRanker {
	
	private final static Pattern DOMAIN_FILTERS = Pattern.compile("http://((.*\\.ics\\.uci\\.edu)|(ics\\.uci\\.edu)).*");
	private PageRankerDBManager dbManager = new PageRankerDBManager();
	private Graph<String, String> graph;
	Logger logger = LogManager.getLogger(ICSPageRanker.class.getName());
	
	ICSPageRanker() throws ClassNotFoundException, SQLException{
		this.dbManager = new PageRankerDBManager();
		this.graph = new DirectedSparseGraph<String, String>();
	}
	
	private List<String> getLinksFromHTML(Document doc, String parent_url){
		List<String> outgoing_urls = new ArrayList<String>();
		Elements body = doc.getElementsByTag("body");
		if(body.first() == null){
			Elements frames = doc.getElementsByTag("frame");
			if(frames.first() == null){
				return null;
			}else{
				for(Element frame: frames){
					try {
						String source = frame.attr("src");
						if(source.equals("about:blank")) continue;
						List<String> child_urls = getLinksFromHTML(Jsoup.connect(parent_url + "/" + source).get(), parent_url + "/" + source);
						if(child_urls != null){
							logger.info("Page in frame " + parent_url + "/" + source + ": " + child_urls);
							outgoing_urls.addAll(child_urls);
						}
					} catch(Exception e){
						e.printStackTrace();
						continue;
					}
				} 
			}
		}else{
			Elements links = body.first().getElementsByTag("a");
			for (Element link : links) {
			  String linkHref = link.attr("href");
			  if (DOMAIN_FILTERS.matcher(linkHref).matches()) {
				  	outgoing_urls.add(linkHref);
				}
			}
		}
		return outgoing_urls;
	}
	
	public void buildGraph(String sourcePath){
		for(int i = 1; i <= 118000; i++){
			try {
				/*** Parse HTML ***/ 
				InputStream inputStream;
				try{
					inputStream = new FileInputStream(sourcePath + i + ".html");
				}catch(IOException e){
					logger.info("File " + i +".html is missing. Skipped");
					System.out.println("File " + i +".html is missing. Skipped");
					continue;
				}
				// read the database to get the parent url for this doc_id
				String parent_url = this.dbManager.getUrlByDocId(i);
				if(parent_url == null) continue;
				// get all the outgoing_urls
				Document doc = Jsoup.parse(inputStream, "UTF-8", parent_url);
				List<String> outgoing_urls = this.getLinksFromHTML(doc, parent_url);
				if(outgoing_urls == null)
					logger.info("No outgoing links in this page: " + parent_url);
				else
					logger.info(parent_url);
			
				/*** Update the Graph ***/
				if(!this.graph.containsVertex(parent_url)){
					this.graph.addVertex(parent_url);
				}
				// check if the url exists in the graph,if not add it into it; and add directed edge form parent url to outgoing urls
				int outgoing_urls_number = outgoing_urls.size();
				for(int j = 0; j < outgoing_urls_number; j++){
					// every outoing_url should be lower case
					String outgoing_url = outgoing_urls.get(j);
					if(!this.graph.containsVertex(outgoing_url))
						this.graph.addVertex(outgoing_url);
					this.graph.addEdge(parent_url + " -> " + outgoing_url, parent_url, outgoing_url, EdgeType.DIRECTED);
				}
			}catch (Exception e){
				e.printStackTrace();
				continue;
			}
		}
	}
	
	public void rank() throws SQLException{
		// page rank this graph
		PageRank<String, String> ranker = new PageRank<String, String>(this.graph, 0.15);
		ranker.evaluate();
		// save the ranking results into the database
	    double sum = 0;
        Set<String> verticesSet = new HashSet<String>(this.graph.getVertices());
        this.dbManager.initPageRankInsertBatch();
        for (String vertex : verticesSet) {
            double score = ranker.getVertexScore(vertex);
            try {
            	this.dbManager.addPageRankInsertBatch(vertex, score);
            }catch (Exception e){
            	e.printStackTrace();
            }
            sum += score;
        }
        this.dbManager.commitBatch();
        System.out.println("SUM = " + sum);
        System.out.println("Page Ranking Finished");
	}

}
