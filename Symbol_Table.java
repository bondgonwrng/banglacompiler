import java.util.HashMap;

public class Symbol_Table {
    private final HashMap<String, TokenType> table = new HashMap<>();

    public void define(String name, TokenType type) {
        table.put(name, type);
    }

    public TokenType resolve(String name) {
        return table.get(name);
    }

    public boolean exists(String name) {
        return table.containsKey(name);
    }

    @Override
    public String toString() {
        return table.toString();
    }
}