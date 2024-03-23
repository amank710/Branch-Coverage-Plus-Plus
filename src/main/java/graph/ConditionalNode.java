package graph;

import com.github.javaparser.ast.expr.*;
import com.microsoft.z3.*;

import java.util.HashMap;

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

        try {
            HashMap<String, String> cfg = new HashMap<>();
            cfg.put("model", "true");
            Context ctx = new Context(cfg);
            BoolExpr conditionExpr = this.toZ3Expr(ctx);
            Solver solver = ctx.mkSolver();
            solver.add(conditionExpr);

            if (solver.check() == Status.SATISFIABLE) {
                System.out.println(padding + "Condition at line " + this.condition.getBegin().get().line + " is satisfiable.");
            } else {
                System.out.println(padding + "Condition at line " + this.condition.getBegin().get().line + " is unsatisfiable.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        if (getNeighbors().length > 0) {
            System.out.println(padding + "  True branch execute:");
            getNeighbors()[0].visualizer(depth + 1);
        }

        if (getNeighbors().length > 1 && getNeighbors()[1] != null) {
            System.out.println(padding + "  False branch execute:");
            getNeighbors()[1].visualizer(depth + 1);
        }
    }

    public BoolExpr toZ3Expr(Context ctx) throws Exception {
        return parseExpression(this.condition, ctx);
    }

    private BoolExpr parseExpression(Expression expr, Context ctx) throws Exception {
        if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expr;
            BoolExpr left = parseExpression(binaryExpr.getLeft(), ctx);
            BoolExpr right = parseExpression(binaryExpr.getRight(), ctx);

            switch (binaryExpr.getOperator()) {
                case AND:
                    return ctx.mkAnd(left, right);
                case OR:
                    return ctx.mkOr(left, right);
                // Handle other binary operators...
                default:
                    throw new UnsupportedOperationException("Unsupported operator: " + binaryExpr.getOperator());
            }
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) expr;
            BoolExpr inner = parseExpression(unaryExpr.getExpression(), ctx);

            switch (unaryExpr.getOperator()) {
                case LOGICAL_COMPLEMENT:
                    return ctx.mkNot(inner);
                // Handle other unary operators...
                default:
                    throw new UnsupportedOperationException("Unsupported operator: " + unaryExpr.getOperator());
            }
        } else if (expr instanceof BooleanLiteralExpr) {
            BooleanLiteralExpr booleanExpr = (BooleanLiteralExpr) expr;
            return ctx.mkBool(booleanExpr.getValue());
        } else if (expr instanceof NameExpr) {
            NameExpr nameExpr = (NameExpr) expr;
            // This might indicate a simple variable condition.
            return (BoolExpr) ctx.mkBoolConst(nameExpr.getNameAsString());
        }
        // Handle other expression types...
        throw new IllegalArgumentException("Unsupported expression type: " + expr.getClass());
    }
}
