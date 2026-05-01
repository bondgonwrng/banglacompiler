import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "input.bng";
        String sourceCode = "";

        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            sourceCode = new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("ERROR: Could not find '" + filePath + "'.");
            return;
        }

        System.out.println("=== BANGLA COMPILER===\n");
        System.out.println("Reading from: " + filePath + "\n");
        System.out.println("--- Source Code ---");
        System.out.println(sourceCode);
        System.out.println("-------------------\n");

        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.scanTokens();
        
        Parser parser = new Parser(tokens);
        parser.parse();
    }
}