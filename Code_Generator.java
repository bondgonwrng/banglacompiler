import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;

public class Code_Generator {
    private final StringBuilder pythonCode;
    private int indentLevel = 0; 

    public Code_Generator() {
        this.pythonCode = new StringBuilder();
        this.pythonCode.append("#Auto-Generated Python Code from Bangla Compiler\n\n");
    }

    public void generate(AST_Node root, String outputFileName) {
        if (root == null) return;
        System.out.println("--- Starting Code Generation ---");
        traverse(root);
        writeToFile(outputFileName);
    }

    private void writeIndent() {
        for (int i = 0; i < indentLevel; i++) {
            pythonCode.append("    "); 
        }
    }

    private void traverse(AST_Node node) {
        if (node.nodeType.equals("Program")) {
            for (AST_Node child : node.children) {
                traverse(child);
            }
            
        } else if (node.nodeType.equals("IfStatement")) {
            writeIndent();
            pythonCode.append("if ");
            traverse(node.children.get(0)); 
            pythonCode.append(":\n");
            
            traverse(node.children.get(1));
            
            if (node.children.size() > 2) {
                writeIndent();
                pythonCode.append("else:\n");
                traverse(node.children.get(2));
            }
            
        } else if (node.nodeType.equals("WhileLoop")) {
            // NEW: Python While Loop Generation
            writeIndent();
            pythonCode.append("while ");
            traverse(node.children.get(0)); // Condition
            pythonCode.append(":\n");
            
            traverse(node.children.get(1)); // Loop Body Block
            
        } else if (node.nodeType.equals("Block")) {
            indentLevel++; 
            for (AST_Node child : node.children) {
                traverse(child);
            }
            indentLevel--; 
            
        } else if (node.nodeType.equals("Assignment")) {
            writeIndent();
            pythonCode.append(node.value).append(" = ");
            traverse(node.children.get(0)); 
            pythonCode.append("\n");
            
            writeIndent();
            pythonCode.append("print('").append(node.value).append(" =', ").append(node.value).append(")\n");
            
        } else if (node.nodeType.equals("Condition") || node.nodeType.equals("BinaryOp")) {
            traverse(node.children.get(0));
            pythonCode.append(" ").append(node.value).append(" ");
            traverse(node.children.get(1));
            
        } else if (node.nodeType.equals("Number") || node.nodeType.equals("Identifier")) {
            pythonCode.append(node.value);
        }
    }

    private void writeToFile(String filename) {
        try {
            Files.write(Paths.get(filename), pythonCode.toString().getBytes(StandardCharsets.UTF_8));
            System.out.println("Success: Compiled Python code written to '" + filename + "'");
        } catch (IOException e) {
            System.out.println("Error writing generated code: " + e.getMessage());
        }
    }
}