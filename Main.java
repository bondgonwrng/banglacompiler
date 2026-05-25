import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        String filePath = "input.bng";
        String outputFilePath = "output.py"; // The Python file we will generate
        String sourceCode = "";

        try {
            byte[] encoded = Files.readAllBytes(Paths.get(filePath));
            sourceCode = new String(encoded, StandardCharsets.UTF_8);
        } catch (IOException e) {
            System.out.println("ERROR: Could not find '" + filePath + "'.");
            return;
        }

        System.out.println("=== BANGLA COMPILER ===\n");
        System.out.println("Reading from: " + filePath + "\n");
        System.out.println("--- Source Code ---");
        System.out.println(sourceCode);
        System.out.println("-------------------\n");

        // 1. Lexical Analysis
        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.scanTokens();
        
        // 2. Syntax & Semantic Analysis (Building AST)
        Parser parser = new Parser(tokens);
        AST_Node root = parser.parse();
        
        // Print the Abstract Syntax Tree visually to the console
        System.out.println("\n--- Abstract Syntax Tree (AST) ---");
        if (root != null) {
            root.printTree("", true);
        }

        // 3. Code Generation
        System.out.println("\n");
        if (root != null) {
            Code_Generator generator = new Code_Generator();
            generator.generate(root, outputFilePath);
        }
    }
}