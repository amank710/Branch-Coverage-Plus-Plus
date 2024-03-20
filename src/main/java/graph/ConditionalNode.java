package graph;

import com.github.javaparser.ast.expr.Expression;

public class ConditionalNode extends ControlFlowGraph {
    private Expression condition;
    private int lineNumbers;
    private CodeBlockNode trueBranch;
    private CodeBlockNode falseBranch;

    public ConditionalNode(Expression condition, CodeBlockNode trueBranch, CodeBlockNode falseBranch, int lineNumbers) {
        this.condition = condition;
        this.trueBranch = trueBranch;
        this.falseBranch = falseBranch;
        this.lineNumbers = lineNumbers;
    }

    public Expression getCondition() {
        return condition;
    }

    public CodeBlockNode getTrueBranch() {
        return trueBranch;
    }

    public CodeBlockNode getFalseBranch() {
        return falseBranch;
    }

    public void setTrueBranch(CodeBlockNode trueBranch) {
        this.trueBranch = trueBranch;
    }

    public void setFalseBranch(CodeBlockNode falseBranch) {
        this.falseBranch = falseBranch;
    }

    public void visualizer(int depth) {
        String padding = " ".repeat(depth * 4); // 4 spaces per depth level

        System.out.println(padding + "Conditional Node: " + lineNumbers);

        if (trueBranch != null) {
            System.out.println(padding + "  True branch execute:");
            trueBranch.visualizer(depth + 1); // Increment depth for nested nodes
        }

        if (falseBranch != null) {
            System.out.println(padding + "  False branch execute:");
            falseBranch.visualizer(depth + 1); // Increment depth for nested nodes
        }
    }

}
