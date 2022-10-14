package main.java.de.voidtech.alison.entities;

public class QueryBuilder {

    private static final String APOSTROPHE_ESCAPE_STRING = "/@/";

    private String query;

    public QueryBuilder(String query) {
        this.query = query;
    }

    public static String unescapeString(String result) {
        return result.replaceAll(APOSTROPHE_ESCAPE_STRING, "'");
    }

    public static String escapeString(String input) {
        return input.replaceAll("'", APOSTROPHE_ESCAPE_STRING);
    }

    public String build() {
        return this.query;
    }

    //NOTE:
    //The ' character is automatically escaped here. It needs to be un-escaped manually when reading data back.
    public QueryBuilder setParameter(String parameter, String value) {
        String parameterWithLocator = ":" + parameter;
        if (!query.contains(parameterWithLocator)) throw new RuntimeException("Parameter " + parameter + " was not found");
        this.query = query.replace(parameterWithLocator, "'" + escapeString(value) + "'");
        return this;
    }

    @Override
    public String toString() {
        return this.build();
    }
}