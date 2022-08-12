package main.java.de.voidtech.alison.entities;

import main.java.de.voidtech.alison.Alison;

public class AlisonMetadata {
	
	@Deprecated
	AlisonMetadata() {
	}
	
	private String name;
	private String iconUrl;
	
	public String getName() {
		return this.name == null ? "Unnamed Model" : this.name;
	}
	
	public String getIconUrl() {
		return this.iconUrl == null ? Alison.getBot().getJDA().getSelfUser().getAvatarUrl() : this.iconUrl;
	}
}
