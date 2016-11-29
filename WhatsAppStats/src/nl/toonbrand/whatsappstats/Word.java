package nl.toonbrand.whatsappstats;

public class Word{
	String word;
	int occurances;
	
	public Word(String word, int occurances) {
		super();
		this.word = word;
		this.occurances = occurances;
	}
	
	public String getWord() {
		return word;
	}
	public void setWord(String word) {
		this.word = word;
	}
	public int getOccurances() {
		return occurances;
	}
	public void setOccurances(int occurances) {
		this.occurances = occurances;
	}

}
