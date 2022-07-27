package main.java.de.voidtech.alison.ephemeral;

public class AlisonWord {
	private String word;
	private String next;
	
	public AlisonWord(String word, String next) {
		this.word = word;
		this.next = next;
	}
	
	public boolean isStopWord() {
		return this.next.equals("StopWord");
	}
	
	public String getWord() {
		return this.word;
	}
	
	public String getNext() {
		return this.next;
	}

	public String toCreateStatement() {
		return String.format("INSERT INTO AlisonWord VALUES ('%s', '%s')", this.word, this.next);
	}
}
