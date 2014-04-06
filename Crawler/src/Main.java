package ir.assignment.crawler;

public class Main {
	
	  public static void main(String[] args) {
		  try{
			  /*
			  // STEP 1: analyze the XML file, extract all the text into another file
			  XMLAnalyser xmlAnalyser = new XMLAnalyser();
			  xmlAnalyser.analyseXML("files/crawlData.xml","contents.txt");
			  xmlAnalyser.outputResults("subdomain_list.txt");
			  
			  // STEP 2: Analyze the contents file to get one-grams and two-grams 
			  TextAnalyser textAnalyser = new TextAnalyser();
			  // Get the most common one gram
			  //textAnalyser.computeOneGramFrequencies("contents.txt", "one_gram_list.txt");
			  // Get the most common two gram
			  textAnalyser.computeTwoGramFrequencies("contents.txt", "two_gram_list.txt");
			  */
			  //TextDB analyser = new TextDB();
			  //analyser.saveTextToDB("txtfile/");
			  //analyser.updateTextFromHTML();
			  HTMLDB analyser = new HTMLDB();
			  //analyser.saveHTMLToDB("htmlfile/");
			  analyser.updateHTMLFromText();
			  analyser.closeDB();
			 
		  }catch(Exception e){
			  e.printStackTrace();
		  }
	  }
}