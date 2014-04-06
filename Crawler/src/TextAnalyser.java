package ir.assignment.crawler;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class TextAnalyser{
	
	private final static int ONE_GRAM_LIMIT = 500;
	private final static int TWO_GRAM_LIMIT = 20;
	private final static Pattern ONE_GRAM_FILTERS =  Pattern.compile("\\d{1,3}|\\d{5,}|\\s+");
	private final static Pattern TWO_GRAM_FILTERS =  Pattern.compile("\\d");
	private List<Map.Entry<String, Integer>> freqs;
	private Map<String, Integer> word_freqs;
	
	public TextAnalyser(){
		
	}

	public void computeOneGramFrequencies(String inputPath, String outputPath) throws IOException{
		ArrayList<String> stop_words = new ArrayList<String>(Arrays.asList((new String(Files.readAllBytes(Paths.get("stop_words.txt", new String[]{})))).toLowerCase().split("[,]")));
		BufferedReader reader = new BufferedReader(new FileReader(inputPath));
		freqs = new ArrayList<Map.Entry<String, Integer>>();
		word_freqs = new HashMap<String, Integer>();
		for(char i = 'a'; i <= 'z'; i++)
			stop_words.add(Character.toString(i));
		String line = "";
		int line_count = 0;
	    while ((line = reader.readLine()) != null){
	    	System.out.println("Line: " + line_count++);
			ArrayList<String> words = new ArrayList<String>(Arrays.asList((line.trim().replaceAll("[\\W]+", " ").toLowerCase().split("[\\s+]"))));
			for(Iterator<String> iterator = words.iterator(); iterator.hasNext();){
				String word = iterator.next();
			    if (stop_words.contains(word) || ONE_GRAM_FILTERS.matcher(word).matches())
			        iterator.remove();
			}
			for(int i = 0; i < words.size(); i++){
				String word = words.get(i);
				if (word_freqs.containsKey(word))
					word_freqs.put(word, word_freqs.get(word) + 1);
				else
					word_freqs.put(word, 1);
			}
	    }
	    freqs.addAll(word_freqs.entrySet());
		Collections.sort(freqs, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
				int differ = o1.getValue() - o2.getValue();
				if (differ != 0)
					return - differ;
				else 
					return o1.getKey().compareTo(o2.getKey());
		}});
		Iterator<Map.Entry<String, Integer>> iterator = freqs.iterator();
		int count = 0;
		Formatter formatter = new Formatter(new BufferedWriter(new FileWriter(outputPath)));
		for (; iterator.hasNext();count++){
			if(count >= ONE_GRAM_LIMIT)
				break;
			Map.Entry<String, Integer> freq = iterator.next();
			formatter.format("%-30s %d\n", freq.getKey(),freq.getValue());
			System.out.format("%-30s %d\n", freq.getKey(),freq.getValue());
		}
		formatter.flush();
		formatter.close();
		freqs = null;
		word_freqs = null;
	}
	
	public void computeTwoGramFrequencies(String inputPath, String outputPath) throws IOException {
		ArrayList<String> stop_words = new ArrayList<String>(Arrays.asList((new String(Files.readAllBytes(Paths.get("stop_words.txt", new String[]{})))).split("[,]")));
		BufferedReader reader = new BufferedReader(new FileReader(inputPath));
		freqs = new ArrayList<Map.Entry<String, Integer>>();
		word_freqs = new HashMap<String, Integer>();
		for(char i = 'a'; i <= 'z'; i++)
			stop_words.add(Character.toString(i));
		String line = "";
		int line_count = 0;
		word_freqs = new HashMap<String, Integer>();
	    while ((line = reader.readLine()) != null){
	    	System.out.println("Line: " + line_count++);
			ArrayList<String> words = new ArrayList<String>(Arrays.asList((line.trim().replaceAll("[\\W]+", " ").toLowerCase().split("[\\s+]"))));
			for(Iterator<String> iterator = words.iterator(); iterator.hasNext();)
			    if (stop_words.contains(iterator.next()))
			        iterator.remove();
			//ArrayList<String> two_grams = new ArrayList<String>();
			//for(int i = 0; i < words.size()- 1;)
				//two_grams.add(words.get(i) + " " + words.get(++i));
			for(int i = 0; i < words.size() - 1;){
				String word = words.get(i) + " " + words.get(++i);
				if (word_freqs.containsKey(word) && !TWO_GRAM_FILTERS.matcher(word).find())
					word_freqs.put(word, word_freqs.get(word) + 1);
				else
					word_freqs.put(word, 1);
			}
	    }
	    freqs.addAll(word_freqs.entrySet());
		Collections.sort(freqs, new Comparator<Map.Entry<String, Integer>>() {
			public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2){
				int differ = o1.getValue() - o2.getValue();
				if (differ != 0)
					return - differ;
				else 
					return o1.getKey().compareTo(o2.getKey());
		}});
		Iterator<Map.Entry<String, Integer>> iterator = freqs.iterator();
		int count = 0;
		Formatter formatter = new Formatter(new BufferedWriter(new FileWriter(outputPath)));
		for (; iterator.hasNext();count++){
			if(count >= TWO_GRAM_LIMIT)
				break;
			Map.Entry<String, Integer> freq = iterator.next();
			formatter.format("%-40s %d\n", freq.getKey(),freq.getValue());
			System.out.format("%-40s %d\n", freq.getKey(),freq.getValue());
		}
		formatter.flush();
		formatter.close();
		freqs = null;
		word_freqs = null;
	}
}
