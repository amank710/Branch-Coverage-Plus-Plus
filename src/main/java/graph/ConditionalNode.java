package graph;

import com.github.javaparser.ast.expr.Expression;

// This class represents a conditional node in the control flow graph
public class ConditionalNode extends ControlFlowNode {
    private Expression condition;
    private CodeBlockNode trueBranch;
    private CodeBlockNode falseBranch;

    public ConditionalNode(Expression condition, CodeBlockNode trueBranch, CodeBlockNode falseBranch) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
    }

    private ControlFlowNode[] getNeighbors() {
        return new ControlFlowNode[]{trueBranch, falseBranch};
    }

    public void setTrueBranch(CodeBlockNode trueBranch) {
        this.trueBranch = trueBranch;
    }

    public void setFalseBranch(CodeBlockNode falseBranch) {
        this.falseBranch = falseBranch;
    }

    @Override
    public void visualizer(int depth) {
        String padding = " ".repeat(depth * 4);
        System.out.println(padding + "Condition: - " + condition.toString() + " (line:" + condition.getBegin().get().line + ")");

        if (getNeighbors().length > 0) {
            System.out.println(padding + "  True branch execute:");
            getNeighbors()[0].visualizer(depth + 1);
        }

        if (getNeighbors().length > 1 && getNeighbors()[1] != null) {
            System.out.println(padding + "  False branch execute:");
            getNeighbors()[1].visualizer(depth + 1);
        }
    }

}
