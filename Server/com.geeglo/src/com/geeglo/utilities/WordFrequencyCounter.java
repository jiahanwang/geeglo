package com.geeglo.utilities;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
//import java.util.Iterator;
import java.util.List;
//import java.util.Map;

/**
 * Counts the total number of words and their frequencies in a text file.
 */
public final class WordFrequencyCounter {
	/**
	 * This class should not be instantiated.
	 */
	private WordFrequencyCounter() {}
	
	/**
	 * Takes the input list of words and processes it, returning a list
	 * of {@link Frequency}s.
	 * 
	 * This method expects a list of lowercase alphanumeric strings.
	 * If the input list is null, an empty list is returned.
	 * 
	 * There is one frequency in the output list for every 
	 * unique word in the original list. The frequency of each word
	 * is equal to the number of times that word occurs in the original list. 
	 * 
	 * The returned list is ordered by decreasing frequency, with tied words sorted
	 * alphabetically.
	 * 
	 * The original list is not modified.
	 * 
	 * Example:
	 * 
	 * Given the input list of strings 
	 * ["this", "sentence", "repeats", "the", "word", "sentence"]
	 * 
	 * The output list of frequencies should be 
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 *  
	 * @param words A list of words.
	 * @return A list of word frequencies, ordered by decreasing frequency.
	 */
	public static List<Frequency> computeWordFrequencies(List<String> words) {
		// TODO Write body!		
		if (words == null){
			return new ArrayList<Frequency>();
		}
		
		HashMap<String, Frequency> map = new HashMap<String, Frequency>();
		for(String word: words){
			Frequency fr;
			if(map.containsKey(word)){
				fr = new Frequency(word, map.get(word).getFrequency()+1);		
			}else{
				fr = new Frequency(word, 1);
			}
			map.put(word, fr);
		}
		
		List<Frequency> wordfrequency = new ArrayList<Frequency>(map.values());
		
		Collections.sort(wordfrequency,  new Comparator<Frequency>(){
			public int compare(Frequency f1, Frequency f2){
				return f2.getFrequency() - f1.getFrequency();
			}
		});
		
		return wordfrequency;
	}
	
	
	/*
	 * 
	 * */
	
	
	
	/**
	 * a helper method
	 */
	public static int printFrequencies(List<Frequency> frequencies) {
		if(frequencies ==null)return 0;
		int total_count = 0;
//		try{
//			FileWriter fw = new FileWriter("Subdomain.txt");
//			BufferedWriter bf = new BufferedWriter(fw);
//			for(Frequency fre:frequencies){
//				bf.write(fre.getText()+".uci.edu: "+fre.getFrequency());
//				bf.newLine();
//				total_count +=fre.getFrequency();
//			}
//			bf.write("Total item count:\t"+total_count);
//			bf.newLine();
//			bf.write("Unique item count:\t"+frequencies.size());
//			bf.newLine();
//			bf.close();
//			fw.close();
//		}catch(IOException e){
//			e.printStackTrace();
//		}
		
		for(Frequency fre:frequencies){
			total_count +=fre.getFrequency();
		}
		
		System.out.println("Total item count:\t"+total_count);
		System.out.println("Unique item count:\t"+frequencies.size());
		Utilities.printFrequencies(frequencies);
		
		return total_count;
	}
	
	
	public static int findwordcount(List<Frequency> frequencies){
		int total_count = 0;
		
		for(Frequency fre:frequencies){
			total_count +=fre.getFrequency();
		}
		return total_count;
	}
	
	
	public static List<Frequency> compareTwoList(List<Frequency> lista, List<Frequency> listb){
		List<Frequency> top500 = new ArrayList<Frequency>();
		int i = 0;
		if(lista==null){
			//int i = 0;
			for(Frequency fre: listb){
				i++;
				top500.add(fre);
				if(i==500)break;
			}
			return top500;
		}
		int j = 0, k = 0;
		while(i<500 && j<lista.size() && k<listb.size()){
			Frequency frea = lista.get(j);
			Frequency freb = listb.get(k);
			if(frea.getFrequency()>freb.getFrequency()){
				top500.add(frea);
				j++;
			}else if(frea.getFrequency()<freb.getFrequency()){
				top500.add(freb);
				k++;
			}else{
				top500.add(freb);
				j++;
				k++;
			}
		}
		if(i==500)return top500;
		else if(j==lista.size()){
			while(k<listb.size() && i<500){
				Frequency freb = listb.get(k);
				top500.add(freb);
				i++;
			}
		}else if(k==listb.size()){
			while(j<lista.size() && i<500){
				Frequency frea = lista.get(j);
				top500.add(frea);
				i++;
			}
		}
		return top500;
	}
	
	/**
	 * Runs the word frequency counter. The input should be the path to a text file.
	 * 
	 * @param args The first element should contain the path to a text file.
	 */
	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();
		
		//File file = new File(args[0]);
		
//		int highestcount = 0;
		int index = 0;
		//File tyq = File(args[0]);
		
		
//		for(int i=0; i<126640; i++){
//			File file = new File(args[0]+'\\'+Integer.toString(i)+".txt");
//			
//			//System.out.println(file.getAbsolutePath());
//			
//			if(file.isFile()){
//				List<String> words = Utilities.tokenizeFile(file);
//				List<Frequency> frequencies = computeWordFrequencies(words);
//				int wordcount = findwordcount(frequencies);
//				if (highestcount < wordcount){
//					highestcount = wordcount;
//					index = i;
//				}
//				System.out.println("index: "+i+".txt: wordcount:"+wordcount);
//			}			
//	

		ArrayList<String> totalwords = new ArrayList<String>();
	
		for(int i=0; i<126640; i++){
			File file = new File(args[0]+'\\'+Integer.toString(i)+".txt");
			
			if(file.isFile()){
				List<String> words = Utilities.tokenizeFile(file);
				totalwords.addAll(words);

				index ++;
				if(index ==1000){
					System.out.println("No. "+i+"...");
					index = 0;
				}
			}			
		}
		List<Frequency> frequencies = computeWordFrequencies(totalwords);
		try{
			FileWriter fw = new FileWriter("topTerm.txt");
			BufferedWriter bf = new BufferedWriter(fw);
			int i =0;
			for(Frequency fre:frequencies){
				if(i==500)break;
				bf.write(fre.toString());
				bf.newLine();
			}
			
			bf.close();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
//		System.out.println("highestcount: " + highestcount);
//		System.out.println("index: "+index+".txt");
		long finishTime = System.currentTimeMillis();
		System.out.println("time:+ " + (finishTime-startTime) + "ms");
		
		//Utilities.printFrequencies(frequencies);
	}
	
}

