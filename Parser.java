import java.util.List;

public class Parser {
    private final List<Token> tokens;
    private int current = 0;
    
    private final Symbol_Table symbolTable = new Symbol_Table();
    private final Semantic_Analyzer analyzer = new Semantic_Analyzer(symbolTable);

    public Parser(List<Token> tokens) {
        this.tokens = tokens;
    }

    public AST_Node parse() {
        System.out.println("--- Starting Syntax & Semantic Analysis ---");
        AST_Node programRoot = new AST_Node("Program", "Main");

        while (!isAtEnd()) {
            try {
                programRoot.addChild(statement());
            } catch (RuntimeException e) {
                System.out.println("Syntax Error: " + e.getMessage());
                synchronize();
            }
        }
        System.out.println("\n Parsing Complete. Symbol Table: " + symbolTable);
        return programRoot;
    }

    private AST_Node statement() {
        if (match(TokenType.KEYWORD_IF)) {
            return ifStatement();
        } else if (match(TokenType.KEYWORD_WHILE)) {
            return whileStatement(); 
        } else if (match(TokenType.DATATYPE_INT, TokenType.DATATYPE_FLOAT)) {
            return declaration(previous());
        } else if (match(TokenType.IDENTIFIER)) {
            return assignment(previous());
        } else {
            throw new RuntimeException("Line " + peek().line + ": Unexpected token -> " + peek().lexeme);
        }
    }

    private AST_Node whileStatement() {
        AST_Node whileNode = new AST_Node("WhileLoop", "While");

        AST_Node condition = comparison();
        whileNode.addChild(condition);

        consume(TokenType.LBRACE, "Expected '{' after while condition.");
        AST_Node loopBlock = new AST_Node("Block", "LoopBody");
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            loopBlock.addChild(statement());
        }
        consume(TokenType.RBRACE, "Expected '}' after while block.");
        whileNode.addChild(loopBlock);

        return whileNode;
    }

    private AST_Node ifStatement() {
        AST_Node ifNode = new AST_Node("IfStatement", "If");

        AST_Node condition = comparison();
        ifNode.addChild(condition);

        consume(TokenType.LBRACE, "Expected '{' after if condition.");
        AST_Node thenBranch = new AST_Node("Block", "Then");
        while (!check(TokenType.RBRACE) && !isAtEnd()) {
            thenBranch.addChild(statement());
        }
        consume(TokenType.RBRACE, "Expected '}' after if block.");
        ifNode.addChild(thenBranch);

        if (match(TokenType.KEYWORD_ELSE)) {
            consume(TokenType.LBRACE, "Expected '{' after else keyword.");
            AST_Node elseBranch = new AST_Node("Block", "Else");
            while (!check(TokenType.RBRACE) && !isAtEnd()) {
                elseBranch.addChild(statement());
            }
            consume(TokenType.RBRACE, "Expected '}' after else block.");
            ifNode.addChild(elseBranch);
        }

        return ifNode;
    }

    private AST_Node declaration(Token typeToken) {
        Token name = consume(TokenType.IDENTIFIER, "Expected variable name.");
        consume(TokenType.ASSIGN, "Expected '=' after variable name.");
        AST_Node expressionNode = expression(); 
        consume(TokenType.SEMICOLON, "Expected ';' at end of statement.");
        
        symbolTable.define(name.lexeme, typeToken.type);
        analyzer.checkTypeMismatch(typeToken.type, 0.0, name.line); 
        
        AST_Node assignNode = new AST_Node("Assignment", name.lexeme);
        assignNode.addChild(expressionNode);
        return assignNode;
    }

    private AST_Node assignment(Token name) {
        analyzer.checkVariableDeclaration(name.lexeme, name.line);
        consume(TokenType.ASSIGN, "Expected '='.");
        AST_Node expressionNode = expression();
        consume(TokenType.SEMICOLON, "Expected ';' at end of statement.");

        AST_Node assignNode = new AST_Node("Assignment", name.lexeme);
        assignNode.addChild(expressionNode);
        return assignNode;
    }

    private AST_Node comparison() {
        AST_Node left = expression();
        while (match(TokenType.GREATER, TokenType.LESS, TokenType.EQUAL_EQUAL)) {
            Token operator = previous();
            AST_Node right = expression();
            AST_Node parent = new AST_Node("Condition", operator.lexeme);
            parent.addChild(left);
            parent.addChild(right);
            left = parent;
        }
        return left;
    }

    private AST_Node expression() {
        AST_Node left = term();
        while (match(TokenType.PLUS, TokenType.MINUS)) {
            Token operator = previous();
            AST_Node right = term();
            AST_Node parent = new AST_Node("BinaryOp", operator.lexeme);
            parent.addChild(left);
            parent.addChild(right);
            left = parent;
        }
        return left;
    }

    private AST_Node term() {
        AST_Node left = factor();
        while (match(TokenType.MULTIPLY, TokenType.DIVIDE)) {
            Token operator = previous();
            AST_Node right = factor();
            AST_Node parent = new AST_Node("BinaryOp", operator.lexeme);
            parent.addChild(left);
            parent.addChild(right);
            left = parent;
        }
        return left;
    }

    private AST_Node factor() {
        if (match(TokenType.NUMBER)) {
            return new AST_Node("Number", previous().lexeme);
        }
        if (match(TokenType.IDENTIFIER)) {
            analyzer.checkVariableDeclaration(previous().lexeme, previous().line);
            return new AST_Node("Identifier", previous().lexeme);
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
            if (previous().type == TokenType.SEMICOLON || previous().type == TokenType.RBRACE) return;
            advance();
        }
    }
}