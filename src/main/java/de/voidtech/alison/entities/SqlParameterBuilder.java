package main.java.de.voidtech.alison.entities;

public class SqlParameterBuilder {

    private String query;

    public SqlParameterBuilder(String query) {
        this.query = query;
    }

    public String build() {
        System.out.println(this.query);
        return this.query;
    }

    public SqlParameterBuilder setParameter(String parameter, String value) {
        String parameterWithLocator = ":" + parameter;
        if (!query.contains(parameterWithLocator)) throw new RuntimeException("Parameter " + parameter + " was not found");
        this.query = query.replace(parameterWithLocator, "'" + value.replaceAll("'", "/@/") + "'");
        return this;
    }
}