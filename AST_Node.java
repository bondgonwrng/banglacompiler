import java.util.ArrayList;
import java.util.List;

public class AST_Node {
    public String nodeType;
    public String value;
    public List<AST_Node> children;

    public AST_Node(String nodeType, String value) {
        this.nodeType = nodeType;
        this.value = value;
        this.children = new ArrayList<>();
    }

    public void addChild(AST_Node child) {
        children.add(child);
    }

    public void printTree(String prefix, boolean isTail) {
        System.out.println(prefix + (isTail ? "└── " : "├── ") + nodeType + (value.isEmpty() ? "" : " : " + value));
        for (int i = 0; i < children.size() - 1; i++) {
            children.get(i).printTree(prefix + (isTail ? "    " : "│   "), false);
        }
        if (children.size() > 0) {
            children.get(children.size() - 1).printTree(prefix + (isTail ? "    " : "│   "), true);
        }
    }
}
