import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    
    // --- Modular Architecture Integration ---
    private final Symbol_Table symbolTable = new Symbol_Table();
    private final Semantic_Analyzer analyzer = new Semantic_Analyzer(symbolTable);

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public void parse() {
        System.out.println("--- Starting Syntax & Semantic Analysis ---");
        while (!isAtEnd()) {
            try {
                declaration();
            } catch (RuntimeException e) {
                System.out.println("Syntax Error: " + e.getMessage());
                synchronize(); // Panic Mode Error Recovery
            }
        }
        System.out.println("\n Parsing Complete. Symbol Table: " + symbolTable);
    }

    private void declaration() {
        if (match(TokenType.DATATYPE_INT, TokenType.DATATYPE_FLOAT)) {
            Token typeToken = previous();
            Token name = consume(TokenType.IDENTIFIER, "Expected variable name.");
            
            consume(TokenType.ASSIGN, "Expected '=' after variable name.");
            
            double value = expression(); // Parses arithmetic
            
            // --- SEMANTIC ANALYSIS: Type Checking ---
            analyzer.checkTypeMismatch(typeToken.type, value, name.line);
            
            consume(TokenType.SEMICOLON, "Expected ';' at end of statement.");
            
            // --- SYMBOL TABLE: Memory Storage ---
            symbolTable.define(name.lexeme, typeToken.type);
            
            System.out.println("Line " + name.line + ": Valid -> " + typeToken.lexeme + " " + name.lexeme + " = " + value);
        } else {
            throw new RuntimeException("Line " + peek().line + ": Expected Data Type. Found: " + peek().lexeme);
        }
    }

    private double expression() {
        double result = term();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            double right = term();
            if (operator.type == TokenType.PLUS) result += right;
            else result -= right;
        }
        return result;
    }

    private double term() {
        double result = factor();
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token operator = previous();
            double right = factor();
            if (operator.type == TokenType.MULTIPLY) result *= right;
            else result /= right;
        }
        return result;
    }

    private double factor() {
        if (match(TokenType.NUMBER)) {
            return Double.parseDouble(previous().lexeme);
        }
        if (match(TokenType.IDENTIFIER)) {
            String varName = previous().lexeme;
            
            // --- SEMANTIC ANALYSIS: Undeclared Variable Check ---
            analyzer.checkVariableDeclaration(varName, previous().line);
            
            return 1.0; 
        }
        throw new RuntimeException("Line " + peek().line + ": Expected number or identifier.");
    }

    private boolean match(TokenType... types) {
        for (TokenType type : types) {
            if (check(type)) { advance(); return true; }
        }
        return false;
    }

    private Token consume(TokenType type, String message) {
        if (check(type)) return advance();
        throw new RuntimeException(message);
    }

    private boolean check(TokenType type) {
        if (isAtEnd()) return false;
        return peek().type == type;
    }

    private Token advance() {
        if (!isAtEnd()) current++;
        return previous();
    }

    private boolean isAtEnd() { return peek().type == TokenType.EOF; }
    private Token peek() { return tokens.get(current); }
    private Token previous() { return tokens.get(current - 1); }

    private void synchronize() {
        System.out.println("Recovering... Skipping to next statement.");
        advance();
        while (!isAtEnd()) {
            if (previous().type == TokenType.SEMICOLON) return;
            advance();
        }
    }
}