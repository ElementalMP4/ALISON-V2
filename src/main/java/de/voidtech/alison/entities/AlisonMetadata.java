package main.java.de.voidtech.alison.entities;

public class AlisonMetadata {
	
	@Deprecated
	AlisonMetadata() {
	}
	
	private String name;
	private String iconUrl;
	
	public String getName() {
		return this.name;
	}
	
	public String getIconUrl() {
		return this.iconUrl;
	}
}
