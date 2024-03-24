package graph;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.*;
import com.microsoft.z3.*;
import java.util.HashMap;
import java.util.*;

public class IfStateNode extends Node{
    private Map<String, Set<Integer>> state;

    private List<Set<Integer>> dependencies;
    private int lineNum;
    private Node child;
    private Node thenNode;
    private Node elseNode;

    private Expression condition;


    public IfStateNode(Map<String, Set<Integer>> prevState, List<Set<Integer>> dependencies, int lineNum, Expression condition) {
        super(prevState, dependencies, lineNum);
    }

    public void setState(Map<String, Set<Integer>> state) {
        this.state = state;
    }
    public Map<String, Set<Integer>> getState() {
        return state;
    }

    public void setDependencies(List<Set<Integer>> dependencies) {
        this.dependencies = dependencies;
    }

    public List<Set<Integer>> getDependencies() {
        return dependencies;
    }

    public void setLineNumber(int lineNum) {
        this.lineNum = lineNum;
    }

    public int getLineNumber() {
        return lineNum;
    }

    public void setThenNode(StateNode thenNode) {
        this.thenNode = thenNode;
    }

    public Node getThenNode() {
        return thenNode;
    }

    public void setElseNode(StateNode elseNode) {
        this.elseNode = elseNode;
    }

    public Node getElseNode() {
        return elseNode;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    public Expression getCondition() {
        return condition;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public Node getChild() {
        return child;
    }

    @Override
    public Map<String, Set<Integer>> getStateFromLine(int line) {
       return searchByLine(line, true).getState();
    }

    @Override
    public List<Set<Integer>> getDependenciesFromLine(int line) {
        return searchByLine(line, true).getDependencies();
    }

    protected Node searchByLine(int line, boolean decrement) {
        if (line == getLineNumber()) {
            System.out.println("Found line " + line + " in " + this.getClass().getSimpleName());
            return this;
        } else {
            if (getChild() != null) {
                return getChild().searchByLine(line, false);
            }
        }
        if (decrement) {
            return searchByLine(line - 1, true);
        }
        return null;
    }

    @Override
   public void visualize() {
        visualize(0); // Start visualization from the root with an indentation depth of 0
    }
    @Override
    public void visualize(int depth) {
        String indentation = createIndentation(depth);
        System.out.println(indentation + getLineNumber() + ": Condition(" + this.getCondition() + ")");
        try {
            HashMap<String, String> cfg = new HashMap<>();
            cfg.put("model", "true");
            Context ctx = new Context(cfg);
            BoolExpr conditionExpr = this.toZ3Expr(ctx);
            Solver solver = ctx.mkSolver();
            solver.add(conditionExpr);

            if (solver.check() == Status.SATISFIABLE) {
                System.out.println("Condition at line " + this.condition.getBegin().get().line + " is satisfiable.");
            } else {
                System.out.println("Condition at line " + this.condition.getBegin().get().line + " is unsatisfiable.");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        // Visualize the 'then' branch, increasing the depth for indentation
        this.getThenNode().visualize(depth + 1);

        // If there's an 'else' branch, visualize it as well
        if (this.getElseNode() != null) {
            System.out.println(indentation + getLineNumber() + ": Else");
            this.getElseNode().visualize(depth + 1);
        }

        if (this.getChild() != null) {
            this.getChild().visualize(depth);
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