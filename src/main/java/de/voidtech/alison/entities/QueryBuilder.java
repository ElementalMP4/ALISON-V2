package main.java.de.voidtech.alison.entities;

public class QueryBuilder {

    private String query;

    public QueryBuilder(String query) {
        this.query = query;
    }

    public String build() {
        return this.query;
    }

    public QueryBuilder setParameter(String parameter, String value) {
        String parameterWithLocator = ":" + parameter;
        if (!query.contains(parameterWithLocator)) throw new RuntimeException("Parameter " + parameter + " was not found");
        this.query = query.replace(parameterWithLocator, "'" + value.replaceAll("'", "/@/") + "'");
        return this;
    }
}