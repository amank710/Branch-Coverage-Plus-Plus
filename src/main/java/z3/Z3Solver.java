package z3;

import com.github.javaparser.ast.expr.*;
import com.microsoft.z3.BoolExpr;
import com.microsoft.z3.Context;
import com.microsoft.z3.Solver;
import com.microsoft.z3.Status;

import java.util.HashMap;

public class Z3Solver {

    private Expression condition;

    public Z3Solver(Expression condition) {
        this.condition = condition;
    }
    public boolean solve() {
        try {
            HashMap<String, String> cfg = new HashMap<>();
            cfg.put("model", "true");
            Context ctx = new Context(cfg);
            BoolExpr conditionExpr = this.toZ3Expr(ctx);

            Solver solver = ctx.mkSolver();
            solver.add(conditionExpr);

            if (solver.check() == Status.SATISFIABLE) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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