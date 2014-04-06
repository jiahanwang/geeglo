package ir.assignment.pr;

public class Main {

	public static void main(String[] args) {
		try {
			//ICSPageRanker ranker = new ICSPageRanker();
			//ranker.buildGraph("htmlfile/");
			//ranker.rank();
			PageRankerDBManager pageDB = new PageRankerDBManager();
			pageDB.updatePageRankFromHTML();
		} catch(Exception e){
			e.printStackTrace();
			
		}
	}

}
