package z3;

import com.github.javaparser.ast.expr.*;
import com.microsoft.z3.*;

import java.util.HashMap;
import java.util.Map;

public class Z3Solver {

    private Expression condition;
    private Map<String, LiteralExpr> staticVariableValues;

    public Z3Solver() {
        this.staticVariableValues = new HashMap<>();
    }
    public Z3Solver(Expression condition, Map<String, LiteralExpr> staticVariableValues) {
        this.condition = condition;
        this.staticVariableValues = staticVariableValues;
    }

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
            Expression leftExpr = binaryExpr.getLeft();
            Expression rightExpr = binaryExpr.getRight();

            switch (binaryExpr.getOperator()) {
                case AND:
                case OR:
                    // Your existing code handles these cases
                    BoolExpr leftBool = parseExpression(leftExpr, ctx);
                    BoolExpr rightBool = parseExpression(rightExpr, ctx);
                    switch (binaryExpr.getOperator()) {
                        case AND:
                            return ctx.mkAnd(leftBool, rightBool);
                        case OR:
                            return ctx.mkOr(leftBool, rightBool);
                    }
                    break;
                case EQUALS:
                case GREATER:
                case GREATER_EQUALS:
                case LESS:
                case LESS_EQUALS:
                case NOT_EQUALS:
                    // Assuming the operands are integers for simplicity
                    IntExpr leftInt = (IntExpr) parseArithmeticExpression(leftExpr, ctx);
                    IntExpr rightInt = (IntExpr) parseArithmeticExpression(rightExpr, ctx);
                    switch (binaryExpr.getOperator()) {
                        case EQUALS:
                            return ctx.mkEq(leftInt, rightInt);
                        case GREATER:
                            return ctx.mkGt(leftInt, rightInt);
                        case GREATER_EQUALS:
                            return ctx.mkGe(leftInt, rightInt);
                        case LESS:
                            return ctx.mkLt(leftInt, rightInt);
                        case LESS_EQUALS:
                            return ctx.mkLe(leftInt, rightInt);
                        case NOT_EQUALS:
                            return ctx.mkNot(ctx.mkEq(leftInt, rightInt));
                    }
                    break;
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
                    throw new UnsupportedOperationException("Unsupported operator: !!!!!!" + unaryExpr.getOperator());
            }
        } else if (expr instanceof BooleanLiteralExpr) {
            BooleanLiteralExpr booleanExpr = (BooleanLiteralExpr) expr;
            return ctx.mkBool(booleanExpr.getValue());
        } else if (expr instanceof NameExpr) {
            NameExpr nameExpr = (NameExpr) expr;
            // Check if the variable value is statically determined
            if (isVariableValueKnown(nameExpr.getNameAsString())) {
                LiteralExpr value = getVariableValue(nameExpr.getNameAsString());
                if (value.isBooleanLiteralExpr()){
                    return ctx.mkBool(value.asBooleanLiteralExpr().getValue());
                }
                System.out.println("error");

            } else {
                // This might indicate a simple variable condition.
                return (BoolExpr) ctx.mkBoolConst(nameExpr.getNameAsString());
            }
        }
        // Handle other expression types...
        throw new IllegalArgumentException("Unsupported expression type: " + expr.getClass());
    }

    private Expr parseArithmeticExpression(Expression expr, Context ctx) throws Exception {
        if (expr instanceof IntegerLiteralExpr) {
            IntegerLiteralExpr intExpr = (IntegerLiteralExpr) expr;
            return ctx.mkInt(intExpr.asInt());
        } else if (expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) expr;
            // Check if the UnaryExpr is a negation
            if (unaryExpr.getOperator() == UnaryExpr.Operator.MINUS) {
                // Process the inner expression, assuming it's an integer
                Expr innerExpr = parseArithmeticExpression(unaryExpr.getExpression(), ctx);
                if (innerExpr instanceof IntExpr) {
                    // Apply negation
                    return ctx.mkUnaryMinus((IntExpr) innerExpr);
                } else {
                    throw new UnsupportedOperationException("Negation applied to a non-integer expression.");
                }
            } else {
                throw new UnsupportedOperationException("Unsupported unary operator: " + unaryExpr.getOperator());
            }
        } else if (expr instanceof NameExpr) {
            NameExpr nameExpr = (NameExpr) expr;
            // Handling integer variables
            if (isVariableValueKnown(nameExpr.getNameAsString())) {
                LiteralExpr value = getVariableValue(nameExpr.getNameAsString());
                if (value.isIntegerLiteralExpr()) {
                    return ctx.mkInt(value.asIntegerLiteralExpr().asInt());
                }
                // Add handling for boolean values if needed
            }
            return ctx.mkIntConst(nameExpr.getNameAsString());
        }
        // Extend to support more types as necessary
        throw new UnsupportedOperationException("Unsupported arithmetic expression type: " + expr.getClass());
    }


    public void addStaticVariableValues(String variableName, Expression value) {
        if (value.isBooleanLiteralExpr()) {
            this.staticVariableValues.put(variableName, (LiteralExpr) value);
        } else if (value.isIntegerLiteralExpr()) {
            this.staticVariableValues.put(variableName, (LiteralExpr) value);
        }
    }





    public Map<String, LiteralExpr> getStaticVariableValues() {
        return this.staticVariableValues;
    }

    public boolean isVariableValueKnown(String variableName) {
        return this.staticVariableValues.containsKey(variableName);
    }

    public LiteralExpr getVariableValue(String variableName) {
        if(isVariableValueKnown(variableName)) {
            return this.staticVariableValues.get(variableName);
        } else {
            return null;
        }
    }

//    public boolean getValueFromVariable(String variableName) {
//         boolean value = this.staticVariableValues.get(variableName);
//    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }
}
