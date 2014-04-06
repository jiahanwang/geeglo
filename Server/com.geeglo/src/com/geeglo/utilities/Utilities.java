package com.geeglo.utilities;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * A collection of utility methods for text processing.
 */
public class Utilities {
	
	private final static List<String> defaultStopWords = new ArrayList<String>(
			Arrays.asList(
			"a","about","above","after","again","against","all","am","an","and","any","are","aren","as","at",
			"b","be","because","been","before","being","below","between","both","but","by","c","can","cannot","could","couldn",
			"d","did","didn","do","does","doesn","don","down","during","d","e","each","f","few","for","from","further","g",
			"h","had","hadn","has","hasn","have","haven","having","he","her","here","hers","herself","him","himself","his","how",
			"i","if","in","into","is","isn","it","its","itself","j","k","l","let","ll","m","me","more","most","mustn","my","myself",
			"n","no","nor","not","o","of","off","on","once","only","or","other","ought","our","ours","ourselves","out","over","own","p","q","r","re","s",
			"same","shan","she","should","shouldn","so","some","such","t","than","that","the","their","theirs","them","themselves","then",
			"there","these","they","this","those","through","to","too","u","under","until","up","v","ve","very","w","was","wasn","we","were","weren",
			"what","when","where","which","while","who","whom","why","with","won","would","wouldn","x","y","you","your","yours","yourself","yourselves","z"));
	
	public final static boolean contains(String str){
		
		return defaultStopWords.contains(str.toLowerCase());	
		
	}
	
	
	/**
	 * Reads the input text file and splits it into alphanumeric tokens.
	 * Returns an ArrayList of these tokens, ordered according to their
	 * occurrence in the original text file.
	 * 
	 * Non-alphanumeric characters delineate tokens, and are discarded.
	 *
	 * Words are also normalized to lower case. 
	 * 
	 * Example:
	 * 
	 * Given this input string
	 * "An input string, this is! (or is it?)"
	 * 
	 * The output list of strings should be
	 * ["an", "input", "string", "this", "is", "or", "is", "it"]
	 * 
	 * @param input The file to read in and tokenize.
	 * @return The list of tokens (words) from the input file, ordered by occurrence.
	 */
	
	public static ArrayList<String> tokenizeFile(File input){
		// TODO Write body!
		FileReader fr;
		BufferedReader br;		
		
		String delimitersRegex = "[^1-9a-zA-Z]";
//		String delimitersRegex = "\n";
//		String delimitersRegex = "[.]";
		String nextline = null;
	
		ArrayList<String> tokenFile = new ArrayList<String>();
		if (input.exists()) {
			try {
				fr = new FileReader(input);
				br = new BufferedReader(fr);
				
				while ((nextline = br.readLine()) != null){
					//nextline = br.readLine();
					//String[] str = nextline.split(delimitersRegex);
					//sr = new StringTokenizer(nextline, delimiters);
					//int i = 0;
					//if(nextline.contains(".")){
						for (String str: nextline.split(delimitersRegex)){					
							if(str.length() > 0 && (contains(str)==false)){
								//System.out.println(str);//for test
								tokenFile.add(str.toLowerCase());
							}
							//break;
						}
					//}

					
				}
							
				br.close();
				
			}catch(FileNotFoundException e)
			{
				System.out.println(e);
			}catch(IOException e)
			{
				System.out.println(e);
			}

		}		
		return tokenFile;
	}
	
	/**
	 * Takes a list of {@link Frequency}s and prints it to standard out. It also
	 * prints out the total number of items, and the total number of unique items.
	 * 
	 * Example one:
	 * 
	 * Given the input list of word frequencies
	 * ["sentence:2", "the:1", "this:1", "repeats:1",  "word:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total item count: 6
	 * Unique item count: 5
	 * 
	 * sentence	2
	 * the		1
	 * this		1
	 * repeats	1
	 * word		1
	 * 
	 * 
	 * Example two:
	 * 
	 * Given the input list of 2-gram frequencies
	 * ["you think:2", "how you:1", "know how:1", "think you:1", "you know:1"]
	 * 
	 * The following should be printed to standard out
	 * 
	 * Total 2-gram count: 6
	 * Unique 2-gram count: 5
	 * 
	 * you think	2
	 * how you		1
	 * know how		1
	 * think you	1
	 * you know		1
	 * 
	 * @param frequencies A list of frequencies.
	 */
	public static void printFrequencies(List<Frequency> frequencies) {
		// TODO Write body!
		//int total_count = 0;
//		try{
//			FileWriter file = new FileWriter("Subdomain.txt");
//			BufferedWriter bf = new BufferedWriter(file);
//			for(Frequency fre:frequencies){
//				//	total_count +=fre.getFrequency();
//				String str = fre.getText()+".uci.ed:"+fre.getFrequency();
//				bf.write(str);
//				bf.newLine();
//				System.out.println(str);
//				}
//			bf.close();
//			file.close();
//		}catch(IOException e){
//			e.printStackTrace();
//		}
		
		for(Frequency fre:frequencies){
			//	total_count +=fre.getFrequency();
			//String str = fre.getText()+".uci.ed:"+fre.getFrequency();
			System.out.println(fre.toString());
			}
		
		/*if(frequencies.get(0).getText().contains(" ")){
			System.out.println("Total 2-gram count:\t"+total_count);
			System.out.println("Unique 2-gram count:\t"+frequencies.size());
		}else{
			System.out.println("Total item count:\t"+total_count);
			System.out.println("Unique item count:\t"+frequencies.size());
		}*/
	}
}
