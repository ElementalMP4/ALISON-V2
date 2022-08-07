package main.java.de.voidtech.alison.entities;

import java.io.Serializable;

public class AlisonWord implements Serializable {
	
	private static final long serialVersionUID = -86154588887368562L;

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
}
