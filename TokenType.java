public enum TokenType {
    // Keywords (Data Types)
    DATATYPE_INT,    // সংখ্যা
    DATATYPE_FLOAT,  // দশমিক

    // Identifiers & Literals
    IDENTIFIER, NUMBER,

    // Operators
    ASSIGN,          // =
    PLUS, MINUS,     // + -
    MULTIPLY, DIVIDE,// * /

    // Punctuation
    SEMICOLON,       // ;
    EOF              // End of File
}