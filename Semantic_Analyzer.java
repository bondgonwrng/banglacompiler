public class Semantic_Analyzer {
    private final Symbol_Table symbolTable;

    public Semantic_Analyzer(Symbol_Table symbolTable) {
        this.symbolTable = symbolTable;
    }

    public void checkTypeMismatch(TokenType expected, double value, int line) {
        if (expected == TokenType.DATATYPE_INT && (value % 1 != 0)) {
            throw new RuntimeException("Line " + line + ": Type Mismatch! Cannot assign decimal to 'সংখ্যা'.");
        }
    }

    public void checkVariableDeclaration(String varName, int line) {
        if (!symbolTable.exists(varName)) {
            throw new RuntimeException("Line " + line + ": Undeclared variable '" + varName + "'.");
        }
    }
}