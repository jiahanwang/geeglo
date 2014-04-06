package ir.assignment.crawler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLEventWriter;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartDocument;
import javax.xml.stream.events.XMLEvent;

public class StaxWriter {
	
	private XMLEventFactory eventFactory;
	private XMLEventWriter eventWriter;
	private XMLEvent end;
	private XMLEvent tab;
	private static StaxWriter _instance = null; 
	
	public static StaxWriter instance(String path) throws FileNotFoundException, XMLStreamException{
		if(_instance == null)
			_instance = new StaxWriter(path);
		return  _instance;
	}
	
	private StaxWriter(String path) throws FileNotFoundException, XMLStreamException{
		// create an XMLOutputFactory
		XMLOutputFactory outputFactory = XMLOutputFactory.newInstance();
		// create XMLEventWriter
		this.eventWriter = outputFactory.createXMLEventWriter(new FileOutputStream(path, true));
		// create an EventFactory
		this.eventFactory = XMLEventFactory.newInstance();
		this.end = this.eventFactory.createDTD("\n");
		this.tab = this.eventFactory.createDTD("\t");
		// detect if this file is empty. If it is, write the start tag
		XMLInputFactory inputFactory = XMLInputFactory.newInstance();
		XMLEventReader eventReader = inputFactory.createXMLEventReader(new FileInputStream(path));
		try{
			XMLEvent event = eventReader.nextEvent();
			event = eventReader.nextEvent();
		}catch(Exception e){
			//add start document tag
			StartDocument startDocument = this.eventFactory.createStartDocument("UTF-8","1.0");
			this.eventWriter.add(startDocument);
			this.eventWriter.add(this.end);
			// add pages open tag
			this.eventWriter.add(this.eventFactory.createStartElement("", "", "pages"));
			this.eventWriter.add(this.end);
		}
		eventReader.close();
	}
	
	public void saveXMLPageElement(XMLPageElement page) throws Exception {
		// create page open tag
		this.eventWriter.add(this.eventFactory.createStartElement("", "", "page"));
		//this.eventWriter.add(this.end);
		// write the different nodes
		createNode("url", page.getUrl());
		createNode("domain", page.getDomain());
		createNode("sub_domain", page.getSub_domain());
		createNode("path", page.getPath());
		createNode("parent_path", page.getParent_page());
		createNode("anchor_text", page.getAnchor_text());
		createNode("text_length", Integer.toString(page.getText_length()));
		createNode("html_length", Integer.toString(page.getHtml_length()));
		createNode("number_of_outgoing_links", Integer.toString(page.getNumber_of_outgoing_links()));
		createNode("response_header", page.getResponse_header());
		createNode("contents", page.getContents());
		// create page end tag
		this.eventWriter.add(this.eventFactory.createEndElement("", "", "page"));
		this.eventWriter.add(this.end);
		this.eventWriter.flush();
	  }
	
	  private void createNode(String name,String value) throws XMLStreamException {
		// create Start node
		//this.eventWriter.add(this.tab);
		this.eventWriter.add(this.eventFactory.createStartElement("", "", name));
		// create Content
		Characters characters = this.eventFactory.createCharacters(value);
		this.eventWriter.add(characters);
		// create End node
		EndElement eElement = this.eventFactory.createEndElement("", "", name);
		this.eventWriter.add(eElement);
		//this.eventWriter.add(this.end);
	  }
	  
	  public void closeFile() throws XMLStreamException{
		this.eventWriter.flush();
		this.eventWriter.close();
	  }
	  
	  public void finalClose() throws XMLStreamException{
		  System.out.print("Final");
		// add pages end tag
		this.eventWriter.add(this.eventFactory.createEndElement("", "", "pages"));
		this.eventWriter.add(this.end);
		// add end document tag
		this.eventWriter.add(this.eventFactory.createEndDocument());
		this.eventWriter.flush();
		this.eventWriter.close();
	  }
} 