package graph;

import com.github.javaparser.ast.stmt.Statement;

public class CodeBlockNode extends ControlFlowGraph{
    private int lineNumber;

    private Statement statement;

    private ControlFlowGraph[] neighbors;

    public CodeBlockNode(Statement statement) {
        this.statement = statement;
        this.neighbors = new ControlFlowGraph[]{};
    }

    public ControlFlowGraph[] getNeighbors() {
        return neighbors;
    }

    public void setNeighbors(ControlFlowGraph[] neighbors) {
        this.neighbors = neighbors;
    }

    public void appendNeighbors(ControlFlowGraph[] neighbors) {
        ControlFlowGraph[] newNeighbors = new ControlFlowGraph[this.neighbors.length + neighbors.length];
        System.arraycopy(this.neighbors, 0, newNeighbors, 0, this.neighbors.length);
        System.arraycopy(neighbors, 0, newNeighbors, this.neighbors.length, neighbors.length);
        this.neighbors = newNeighbors;
    }

    public void appendNeighbors(ControlFlowGraph neighbor) {
        ControlFlowGraph[] newNeighbors = new ControlFlowGraph[this.neighbors.length + 1];
        System.arraycopy(this.neighbors, 0, newNeighbors, 0, this.neighbors.length);
        newNeighbors[this.neighbors.length] = neighbor;
        this.neighbors = newNeighbors;
    }

    public void visualizer(int depth) {
        String padding = " ".repeat(depth * 4); // 4 spaces per depth level

        if (statement == null) {
            System.out.println(padding + "Root node 0");
        } else {
            int startLine = statement.getBegin().get().line;
            int endLine = statement.getEnd().get().line;
            System.out.println(padding + String.format("Block Node - Start: %d, End: %d ", startLine, endLine));
        }

        for (ControlFlowGraph neighbor : neighbors) {
            System.out.println(padding + " execute:");
            neighbor.visualizer(depth + 1); // Increment depth for nested nodes
        }
    }
}
