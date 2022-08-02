package main.java.de.voidtech.alison.entities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "alisonword")
public class AlisonWord {
	
	@Id
	@Column
	private String word;
	
	@Column
	private String next;
	
	@Deprecated
	AlisonWord() {
	}
	
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
