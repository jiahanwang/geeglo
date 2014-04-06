package ir.assignment.indexer;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class OneGramIndexer extends Indexer{
	
	public OneGramIndexer() throws ClassNotFoundException,SQLException {
		super("one_gram");
	}
	
	@Override
	public void buildIndexFromDataBase() {
		try{
			// read the stop_words file 
			ArrayList<String> stopWords = new ArrayList<String>(Arrays.asList((new String(Files.readAllBytes(Paths.get("stop_words.txt", new String[]{})))).toLowerCase().split("[,]")));
			for(char c = 'a'; c <= 'z'; c++)
				stopWords.add(Character.toString(c));
			// read the text data from database by 1000(doc_id from 0 to 118000)
			for(int i = 0; i <= 118; i++){
				Index index = new Index();
				// read text data
				List<RawDocument> raw_documents = this.dbManager.getTextData(i*1000);
				// analyze every document
				for(RawDocument document: raw_documents){
					Map<String, Posting> wordFreqs = new HashMap<String, Posting>();
					List<String> words = null;
					System.out.println("Document: " + document.getDoc_id());
					// analyze the contents
					words = new ArrayList<String>(Arrays.asList((document.getContents().trim().replaceAll("[\\W]+", " ").toLowerCase().split("[\\s+]"))));
					for(int n = 0; n < words.size(); n++){
						String word = words.get(n);
						if(stopWords.contains(word) || word.length() == 0) continue; // stop words also count into positions
						if (wordFreqs.containsKey(word)){
							Posting postingItem = wordFreqs.get(word);
							postingItem.incrementTf();
							postingItem.addPosition(n);
							wordFreqs.put(word, postingItem);
						}else{
							Posting postingItem = new Posting();
							postingItem.setDocId(document.getDoc_id());
							postingItem.setTf(1);
							postingItem.addPosition(n);
							wordFreqs.put(word, postingItem);
						}
					}
					/*****************/
					//Posting temp = wordFreqs.get("information");
					/****************/
					// analyze the title and header_text
					String priority_text = document.getTitle() + " " + document.getHeader_text();
					words = new ArrayList<String>(Arrays.asList(priority_text.trim().replaceAll("[\\W]+", " ").toLowerCase().split("[\\s+]")));
					for(int n = 0; n < words.size(); n++){
						String word = words.get(n);
						if(stopWords.contains(word) || word.length() == 0) continue;
						if (wordFreqs.containsKey(word)){
							Posting postingItem = wordFreqs.get(word);
							// every word will get 5 times tf
							postingItem.incrementPriorityTf();
							wordFreqs.put(word, postingItem);
						}else{
							Posting postingItem = new Posting();
							postingItem.setDocId(document.getDoc_id());
							postingItem.setTf(5);
							wordFreqs.put(word, postingItem);
						}
					}
					/*****************/
					//temp = wordFreqs.get("information");
					/****************/
				    // update the index by add posting item
				    Iterator<Map.Entry<String, Posting>> wordFreqsIterator = wordFreqs.entrySet().iterator();
				    while(wordFreqsIterator.hasNext()){
				    	Map.Entry<String, Posting> pair = wordFreqsIterator.next();
				    	if(index.containsTerm(pair.getKey()))
				    		// if term exists in the index, add a postingItem into this index item
				    		index.updateIndexItemByAddingPosting(pair.getKey(), pair.getValue());
				    	else{
				    		// if term doesn't exist in the index. add a new index item
				    		index.addIndexItem(pair.getKey(), pair.getValue());
				    	}
				    }
				    words = null;
				    wordFreqs = null;
				}
				// save this index into database
				System.out.println("Saving into database: " + i);
				this.saveIntoDataBase(index);
				System.out.println("Saved into database: " + i);
			    // clean up
				index = null;
			    System.gc();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void buildIndexFromFiles(String sourcePath) {
		Index index = new Index();
		BufferedReader reader = null;
		ArrayList<String> words = null;
		Map<String, Posting> wordFreqs = new HashMap<String, Posting>();
		try{
			// read the stop_words file 
			ArrayList<String> stopWords = new ArrayList<String>(Arrays.asList((new String(Files.readAllBytes(Paths.get("stop_words.txt", new String[]{})))).toLowerCase().split("[,]")));
			for(char c = 'a'; c <= 'z'; c++)
				stopWords.add(Character.toString(c));
			// read the data files one by one(file name starts with 1)
			for(int i = 1; i <= 120000; i++){
				if( i % 1000 == 0){
					System.out.println("Saving into database: " + i);
					this.saveIntoDataBase(index);
					System.out.println("Saved into database: " + i);
					index.clear();
					System.gc();
				}
				try{
					reader = new BufferedReader(new FileReader(sourcePath + Integer.toString(i) + ".txt"));
				}catch(IOException e){
					System.out.println("File " + i +".txt is missing. Skipped");
					continue;
				}
				String line = "";
				String url = "";
				int positionBase = 0;
				// get the first line of the file(url)
				if((line = reader.readLine()) != null){
					url = line;
				}
				else{
					System.out.println("File " + i +".txt is empty. Skipped");
					continue;
				}
				// read the contents of the file and get all term frequencies in this single file
			    while ((line = reader.readLine()) != null){
					words = new ArrayList<String>(Arrays.asList((line.trim().replaceAll("[\\W]+", " ").toLowerCase().split("[\\s+]"))));
					for(int n = 0; n < words.size(); n++){
						String word = words.get(n);
						if(stopWords.contains(word)) continue; // stop words also count into positions
						if(word.length() == 0){
							positionBase --;
							continue;
						}
						if (wordFreqs.containsKey(word)){
							Posting postingItem = wordFreqs.get(word);
							postingItem.incrementTf();
							postingItem.addPosition(n + positionBase);
							wordFreqs.put(word, postingItem);
						}else{
							Posting postingItem = new Posting();
							postingItem.setDocId(i);
							postingItem.setTf(1);
							postingItem.addPosition(n + positionBase);
							wordFreqs.put(word, postingItem);
						}
					}
					positionBase += words.size();
					words = null;
			    }
			    // analyze the title and headers of this document
			    // get the tile and 
			    
			    
			    // update the index by add posting item
			    Iterator<Map.Entry<String, Posting>> wordFreqsIterator = wordFreqs.entrySet().iterator();
			    while(wordFreqsIterator.hasNext()){
			    	Map.Entry<String, Posting> pair = wordFreqsIterator.next();
			    	if(index.containsTerm(pair.getKey()))
			    		// if term exists in the index, add a postingItem into this index item
			    		index.updateIndexItemByAddingPosting(pair.getKey(), pair.getValue());
			    	else{
			    		// if term doesn't exist in the index. add a new index item
			    		index.addIndexItem(pair.getKey(), pair.getValue());
			    	}
			    }
			    // clean up
			    words = null;
			    wordFreqs.clear();
			    reader.close();
			    reader = null;
			    System.gc();

			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}

}
