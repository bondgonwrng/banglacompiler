import java.util.ArrayList;
import java.util.List;

public class Lexer {
    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    public Lexer(String source) {
        this.source = source;
    }

    public List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }
        tokens.add(new Token(TokenType.EOF, "", line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '=': addToken(TokenType.ASSIGN); break;
            case '+': addToken(TokenType.PLUS); break;
            case '-': addToken(TokenType.MINUS); break;
            case '*': addToken(TokenType.MULTIPLY); break;
            case '/': addToken(TokenType.DIVIDE); break;
            case ';': addToken(TokenType.SEMICOLON); break;

            case ' ':
            case '\r':
            case '\t':
                break;

            case '\n':
                line++;
                break;

            default:
                if (isDigit(c) || isBanglaDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    System.err.println("Line " + line + ": Unexpected character '" + c + "'");
                }
                break;
        }
    }

    // ================= IDENTIFIER =================
    private void identifier() {
        while (isAlpha(peek()) || isDigit(peek()) || isBanglaDigit(peek())) {
            advance();
        }

        String text = source.substring(start, current);

        if (text.equals("সংখ্যা")) {
            addToken(TokenType.DATATYPE_INT);
        } else if (text.equals("দশমিক")) {
            addToken(TokenType.DATATYPE_FLOAT);
        } else {
            addToken(TokenType.IDENTIFIER);
        }
    }

    // ================= NUMBER =================
    private void number() {
        while (isDigit(peek()) || isBanglaDigit(peek())) {
            advance();
        }

        if (peek() == '.' && (isDigit(peekNext()) || isBanglaDigit(peekNext()))) {
            advance();
            while (isDigit(peek()) || isBanglaDigit(peek())) {
                advance();
            }
        }

        String raw = source.substring(start, current);

        // Convert Bangla digits → English digits
        StringBuilder converted = new StringBuilder();
        for (char c : raw.toCharArray()) {
            if (c >= '\u09E6' && c <= '\u09EF') {
                converted.append(c - '\u09E6');
            } else {
                converted.append(c);
            }
        }

        tokens.add(new Token(TokenType.NUMBER, converted.toString(), line));
    }

    // ================= HELPERS =================
    private boolean isBanglaDigit(char c) {
        return c >= '\u09E6' && c <= '\u09EF';
    }

    private boolean isAlpha(char c) {
        return Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BENGALI
                || Character.isLetter(c);
    }

    private boolean isDigit(char c) {
        return Character.isDigit(c);
    }

    private char advance() {
        return source.charAt(current++);
    }

    private char peek() {
        return isAtEnd() ? '\0' : source.charAt(current);
    }

    private char peekNext() {
        return current + 1 >= source.length() ? '\0' : source.charAt(current + 1);
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private void addToken(TokenType type) {
        tokens.add(new Token(type, source.substring(start, current), line));
    }
}