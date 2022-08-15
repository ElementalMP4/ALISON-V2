package main.java.de.voidtech.alison.commands;

public enum CommandCategory {
	TEXT_GENERATION("Generation", ":robot:"),
	PRIVACY("Privacy", ":gear:"),
	INFORMATION("Information", ":books:"),
    SENTIMENT_ANALYSIS("Sentiment", ":heart:");
 
    private final String category;
    private final String iconName;
 
    CommandCategory(String category, String iconName) {
    	this.category = category;
    	this.iconName = iconName;
	}
    
    public String getCategory() {
        return category;
    }
    
    public String getIcon() {
    	return iconName;
    }
}
