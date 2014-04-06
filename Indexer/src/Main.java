package ir.assignment.indexer;


public class Main {

	public static void main(String[] args) {
		try {
			Indexer oneGramIndexer = new OneGramIndexer();
			oneGramIndexer.buildIndexFromDataBase();
			//oneGramIndexer.buildIndexFromFiles("txtfile/");
			//Indexer twoGramIndexer = new TwoGramIndexer();
			//twoGramIndexer.buildIndexFromDataBase();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
