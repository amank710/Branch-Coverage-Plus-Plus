package graph;

import com.github.javaparser.ast.stmt.Statement;

// This class represents a generic code block of statements that will be executed
public class CodeBlockNode extends ControlFlowNode {
    private Statement statement;

    private ControlFlowNode[] neighbors;

    public CodeBlockNode() {
        this.neighbors = new ControlFlowNode[]{};
    }

    public CodeBlockNode(Statement statement) {
        this.statement = statement;
        this.neighbors = new ControlFlowNode[]{};
    }

    public ControlFlowNode[] getNeighbors() {
        return neighbors;
    }

    public void appendNeighbors(ControlFlowNode neighbor) {
        ControlFlowNode[] newNeighbors = new ControlFlowNode[this.neighbors.length + 1];
        System.arraycopy(this.neighbors, 0, newNeighbors, 0, this.neighbors.length);
        newNeighbors[this.neighbors.length] = neighbor;
        this.neighbors = newNeighbors;
    }

    public void visualizer(int depth) {
        String padding = " ".repeat(depth * 4); // 4 spaces per depth level
        if (statement != null) {
            int startLine = statement.getBegin().map(p -> p.line).orElse(-1); // Safely get start line
            int endLine = statement.getEnd().map(p -> p.line).orElse(-1); // Safely get end line

            System.out.println(padding + String.format("Block Node - Start: %d, End: %d", startLine, endLine));
        } else {
            System.out.println(padding + "Generic Code Block Node");
        }
        for (ControlFlowNode neighbor : getNeighbors()) {
            System.out.println(padding + "  execute:");
            neighbor.visualizer(depth + 1); // Increment depth for nested nodes
        }
    }
}
