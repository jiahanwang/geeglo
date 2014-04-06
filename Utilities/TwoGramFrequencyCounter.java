package ir.assignment.utilities;


import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Count the total number of 2-grams and their frequencies in a text file.
 */
public final class TwoGramFrequencyCounter {
	/**
	 * This class should not be instantiated.
	 */
	private TwoGramFrequencyCounter() {}
	
	/**
	 * Takes the input list of words and processes it, returning a list
	 * of {@link Frequency}s.
	 * 
	 * This method expects a list of lowercase alphanumeric strings.
	 * If the input list is null, an empty list is returned.
	 * 
	 * There is one frequency in the output list for every 
	 * unique 2-gram in the original list. The frequency of each 2-grams
	 * is equal to the number of times that two-gram occurs in the original list. 
	 * 
	 * The returned list is ordered by decreasing frequency, with tied 2-grams sorted
	 * alphabetically. 
	 * 
	 * 
	 * 
	 * Example:
	 * 
	 * Given the input list of strings 
	 * ["you", "think", "you", "know", "how", "you", "think"]
	 * 
	 * The output list of 2-gram frequencies should be 
	 * ["you think:2", "how you:1", "know how:1", "think you:1", "you know:1"]
	 *  
	 * @param words A list of words.
	 * @return A list of two gram frequencies, ordered by decreasing frequency.
	 */
	public static List<Frequency> computeTwoGramFrequencies(List<String> words) {
		// TODO Write body!	
		if(words == null){
			return new ArrayList<Frequency>();
		}		
		
		List<String> twoGram = computeTwoGramList(words);	

		List<Frequency> twoGramFre = WordFrequencyCounter.computeWordFrequencies(twoGram);
		
		return twoGramFre;
		
	}
	
	/*
	 * helper method
	 * */
	private static List<String> computeTwoGramList(List<String> words) {
		// TODO Write body!	
		if(words == null)return new ArrayList<String>();		
		if(words.size()==0)return new ArrayList<String>();
		
		ArrayList<String> twoGram = new ArrayList<String>();		
		
		for(int i=0; i<words.size()-1; i++){
			twoGram.add(words.get(i)+" "+words.get(i+1));
		}
		
		return twoGram;
		
	}
	/*
	 * a helper method
	 */
	public static void printFrequencies(List<Frequency> frequencies) {
		int total_count = 0;
		for(Frequency fre:frequencies){
			total_count +=fre.getFrequency();
		}
		System.out.println("Total 2-gram count:\t"+total_count);
		System.out.println("Unique 2-gram count:\t"+frequencies.size());
		Utilities.printFrequencies(frequencies);
	}
	
	/*
	 *a helper method 
	 *
	 */
	public final static ArrayList<Frequency> merge(List<Frequency> a, List<String> words){
		if (a==null && words==null) return new ArrayList<Frequency>();
		else if (a==null){

			 return (ArrayList<Frequency>) WordFrequencyCounter.computeWordFrequencies(words);
		}
		else{
			HashMap<String, Frequency> map = new HashMap<String, Frequency>();
			for(Frequency frea: a){
				map.put(frea.getText(), frea);
			}
				
			for(String word: words){
				Frequency fr;
				if(map.containsKey(word)){
					fr = new Frequency(word, map.get(word).getFrequency()+1);		
				}else{
					fr = new Frequency(word, 1);
				}
				map.put(word, fr);
				
			}
			
			ArrayList<Frequency> mergedfrequency = new ArrayList<Frequency>(map.values());

//			Collections.sort(mergedfrequency, new Comparator<Frequency>() {
//				public int compare(Frequency f1, Frequency f2) {
//					return f2.getFrequency() - f1.getFrequency();
//				}
//			});
			
			return mergedfrequency;

		}
		
	}
	
	
	/**
	 * Runs the 2-gram counter. The input should be the path to a text file.
	 * 
	 * @param args The first element should contain the path to a text file.
	 */
	public static void main(String[] args) {
//		File file = new File(args[0]);
//		ArrayList<String> words = Utilities.tokenizeFile(file);
//		List<Frequency> frequencies = computeTwoGramFrequencies(words);
//		//Utilities.printFrequencies(frequencies);
//		printFrequencies(frequencies);
		
		long startTime = System.currentTimeMillis();
		
		int index = 0;
		
		ArrayList<String> twoGramTotal = new ArrayList<String>();
		
	
		
		for(int i=0; i<126640; i++){
			File file = new File(args[0]+'\\'+Integer.toString(i)+".txt");
			
			if(file.isFile()){
				ArrayList<String> words = Utilities.tokenizeFile(file);		
				//List<Frequency> sortedwords =  WordFrequencyCounter.computeWordFrequencies(words);
				
				List<String> twoGram = computeTwoGramList(words);
				
				//topTwoGramLast = merge(topTwoGramLast, twoGram);
				
				twoGramTotal.addAll(twoGram);
				
				index ++;
				if(index ==1000){
					System.out.println("No. "+i+"...");
					index = 0;
				}
			}			
		}
		
		
		List<Frequency> frequencies = WordFrequencyCounter.computeWordFrequencies(twoGramTotal);
		
		try{
			FileWriter fw = new FileWriter("topTwoGramTerm.txt");
			BufferedWriter bf = new BufferedWriter(fw);
			
			int i =0;
			
			for(Frequency fre:frequencies){
				if(i==100)break;
				bf.write(fre.toString());
				bf.newLine();
				i++;
			}		
			bf.close();
			fw.close();
		}catch(IOException e){
			e.printStackTrace();
		}
		
		long finishTime = System.currentTimeMillis();
		System.out.println("time:+ " + (finishTime-startTime) + "ms");
		
		
	}
}
