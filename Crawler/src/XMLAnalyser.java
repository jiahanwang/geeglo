package ir.assignment.crawler;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.*;

public class XMLAnalyser {
	//
	static final String PAGE = "page";
	static final String DOC_ID = "doc_id";
	static final String URL = "url";
	static final String DOMAIN = "domain";
	static final String SUB_DOMAIN = "sub_domain";
	static final String PATH = "path";
	static final String PARENT_PATH = "parent_path";
	static final String ANCHOR_TEXT = "anchor_text";
	static final String TEXT_LENGTH = "text_length";
	static final String HTML_LENGTH = "html_length";
	static final String WORD_COUNT = "word_count";
	static final String NUMBER_OF_OUTGOING_LINKS = "number_of_outgoing_links";
	static final String RESPONSE_HEADER = "response_header";
	static final String CONTENTS = "contents";
	static final int FLUSH_LIMIT = 100;
	private int url_count;
	private HashMap<String, Integer> sub_domain_frequency_map;
	private int longest_page_in_word;
	private int longest_page_in_text;
	private int flush_count; 
	
	XMLAnalyser(){
		this.url_count = 0;
		this.sub_domain_frequency_map = new HashMap<String, Integer>();
		this.longest_page_in_text = 0;
		this.longest_page_in_word = 0;
		this.flush_count = 0;
	}
	
	public void outputResults(String path) throws IOException{
		Formatter formatter = new Formatter(new BufferedWriter(new FileWriter(path)));
		formatter.format("%-40s %d\n","The Count of URLs:",this.url_count);
		formatter.format("%-40s %d\n","The Longest Page in Word:",this.longest_page_in_word);
		formatter.format("%-40s %d\n\n","The Longest Page in Text:",this.longest_page_in_text);
		List<Map.Entry<String, Integer>> freqs = new ArrayList<Map.Entry<String, Integer>>();
		freqs.addAll(this.sub_domain_frequency_map.entrySet());
		Iterator<Map.Entry<String, Integer>> iterator = freqs.iterator();
		Collections.sort(freqs, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
				int differ = o1.getValue() - o2.getValue();
				if (differ != 0)
					return - differ;
				else 
					return o1.getKey().compareTo(o2.getKey());
		}});
		for (; iterator.hasNext();){
			Map.Entry<String, Integer> freq = iterator.next();
			formatter.format("%-40s %d\n", "http://" + freq.getKey() + ".uci.edu",freq.getValue());
		}
		formatter.flush();
		formatter.close();
	}
	
	public void analyseXML(String xml_path, String text_path){
		try{
			BufferedWriter textWriter = new BufferedWriter(new FileWriter(text_path));
			// create a new XMLInputFactory
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
			// setup a new eventReader
			XMLEventReader eventReader = inputFactory.createXMLEventReader(new FileInputStream(xml_path),"UTF-8");
			// read the XML document
			while (eventReader.hasNext()){
					XMLEvent event = eventReader.nextEvent();
					if (event.isStartElement()) {
					  String startElementName = event.asStartElement().getName().getLocalPart();
					  if (startElementName.equals(URL)) {
						  event = eventReader.nextEvent();
						  if(event.isEndElement())
							  continue;
						  // count the url
						  this.url_count++;
						  System.out.println(this.url_count + " : " + event.asCharacters().getData());
						  continue;
					  }
					  if (startElementName.equals(SUB_DOMAIN)) {
						  event = eventReader.nextEvent();
						  if(event.isEndElement())
							  continue;
						  // count the frequency sub_domain
						  String sub_domain = event.asCharacters().getData();
						  if (this.sub_domain_frequency_map.containsKey(sub_domain))
							  this.sub_domain_frequency_map.put(sub_domain, (int)this.sub_domain_frequency_map.get(sub_domain) + 1);
						  else
							  this.sub_domain_frequency_map.put(sub_domain, 1);
						  continue;
					  }
					  if (startElementName.equals(TEXT_LENGTH)) {
						  event = eventReader.nextEvent();
						  if(event.isEndElement())
							  continue;
						  // count TEXT_LENGTH
						  int text_length = Integer.parseInt(event.asCharacters().getData());
						  if(text_length > this.longest_page_in_text)
							  this.longest_page_in_text = text_length;
						  continue;
					  }
					  if (startElementName.equals(WORD_COUNT)) {
						  event = eventReader.nextEvent();
						  if(event.isEndElement())
							  continue;
						  // count WORD_COUNT
						  int word_count = Integer.parseInt(event.asCharacters().getData());
						  if(word_count > this.longest_page_in_word)
							  this.longest_page_in_word = word_count;
						  continue;
					  }
					  if (startElementName.equals(CONTENTS)) {				  
						  // write contents
						  event = eventReader.nextEvent();
						  if(event.isEndElement())
							  continue;
						  String contents = event.asCharacters().getData();
						  textWriter.write(contents);
						  textWriter.newLine();
						  this.flush_count++;
						  if(this.flush_count % FLUSH_LIMIT == 0){
							  textWriter.flush();
							  this.flush_count = 0;
						  }
						  continue;
					  }
				  }
			  }
		  	textWriter.flush();
		  	textWriter.close();
	}catch(Exception e){
		e.printStackTrace();
	}
}
}

