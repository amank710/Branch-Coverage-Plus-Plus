package graph;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;

// This class represents a method declaration node in the control flow graph
// It contains a method declaration and its body
public class MethodDeclarationNode extends ControlFlowNode {
    private Expression condition;
    private MethodDeclaration methodDeclaration;

    private ControlFlowNode[] neighbors;

    public MethodDeclarationNode(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
        this.neighbors = new ControlFlowNode[]{};
    }

    public void setBody(ControlFlowNode body) {
        appendNeighbors(body);
    }

    public void setMethodDeclaration(MethodDeclaration methodDeclaration) {
        this.methodDeclaration = methodDeclaration;
    }

    public ControlFlowNode[] getNeighbors() {
        return neighbors;
    }

    public void appendNeighbors(ControlFlowNode neighbor) {
        if (neighbors == null) {
            neighbors = new ControlFlowNode[1];
            neighbors[0] = neighbor;
        } else {
            ControlFlowNode[] newNeighbors = new ControlFlowNode[neighbors.length + 1];
            for (int i = 0; i < neighbors.length; i++) {
                newNeighbors[i] = neighbors[i];
            }
            newNeighbors[neighbors.length] = neighbor;
            neighbors = newNeighbors;
        }
    }
    @Override
    public void visualizer(int depth) {
        for (ControlFlowNode neighbor : getNeighbors()) {
            neighbor.visualizer(depth);
        }
    }
}
