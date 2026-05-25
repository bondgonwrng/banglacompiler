public enum TokenType {
    // Keywords (Data Types & Control Flow)
    DATATYPE_INT,    // সংখ্যা
    DATATYPE_FLOAT,  // দশমিক
    KEYWORD_IF,      // যদি
    KEYWORD_ELSE,    // নাহলে
    KEYWORD_WHILE,   // যতক্ষণ

    // Identifiers & Literals
    IDENTIFIER, NUMBER,

    // Operators & Logic
    ASSIGN,          // =
    PLUS, MINUS,     // + -
    MULTIPLY, DIVIDE,// * /
    GREATER, LESS,   // > <
    EQUAL_EQUAL,     // ==

    // Punctuation
    SEMICOLON,       // ;
    LBRACE, RBRACE,  // { }
    EOF              // End of File
}