package parser;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.ReturnStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.microsoft.z3.*;
import common.functions.FunctionContext;
import graph.IfStateNode;
import graph.Node;
import graph.StateNode;

import z3.Z3Solver;

import java.util.*;

import common.functions.Path;

// This is a visitor class that visits the AST nodes and builds the variable map
public class VariableVisitor extends VoidVisitorAdapter<Node> {

    // need to keep the current state of the node instead of passing it as an argument
    // this is because when VoidVisitorAdapter visits nodes other than VariableDeclartion and Assignemnt,
    // the argument is always be initialized to null
    private Node initialNode;
    private Node previousNode;
    private Expression previousCondition;
    private Z3Solver z3Solver;
    // Initialize Z3 context
    Context ctx = new Context();
    // Map for keeping track of parameters for Symbolic Execution
    private static Map<String, Expr> parameterSymbols = new HashMap<>();

    private Path path;
    private FunctionContext functionCtx;

    private ArrayList<Integer> conditionalBlocks;
    Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths;
    Stack<ArrayList<Integer>> outerConditionalPath;

    private List<Integer> returnLines = new ArrayList<>();

    // Keep track of visited lines to avoid overwriting the state updated from if statements by the assignment statements
    private HashSet<Integer> visitedLine = new HashSet<>();


    public VariableVisitor(Node initialNode, Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths) {
        this.initialNode = initialNode;
        this.z3Solver = new Z3Solver();
        conditionalBlocks = new ArrayList<>();
        functionCtx = new FunctionContext();
        path = new Path();
        this.paths = paths;
        outerConditionalPath = new Stack<>();
    }

    public List<Integer> getReturnLines() {
        return returnLines;
    }

    private void addBinaryExpressionDependencies(BinaryExpr binaryExpr, List<Set<Integer>> dependencies) {
        Set<Integer> binaryExprDependencies = new HashSet<>();
        binaryExpr.getLeft().ifNameExpr(nameExpr ->
                binaryExprDependencies.addAll(this.previousNode.getState().get(nameExpr.getNameAsString())));
        binaryExpr.getRight().ifNameExpr(nameExpr ->
                binaryExprDependencies.addAll(this.previousNode.getState().get(nameExpr.getNameAsString())));
        dependencies.add(binaryExprDependencies);
    }

    public void thenHelper(IfStmt n, Node arg, boolean thenCondition, IfStateNode conditionalNode,  List<Set<Integer>> dependencies) {
        if (thenCondition) {
            StatementVisitor statementVisitor = new StatementVisitor();
            n.getThenStmt().accept(statementVisitor, arg);
            returnLines.add(statementVisitor.getReturnLine());

            int ifBeginLine = n.getThenStmt().getBegin().get().line;
            int ifEndLine = n.getThenStmt().getEnd().get().line;
            if (paths.empty()) {
                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> pathMap = new HashMap<>();
                ArrayList<Integer> ifLines = new ArrayList<>();
                ifLines.add(ifBeginLine);
                ifLines.add(ifEndLine);

                ArrayList<Integer> path = new ArrayList<>();
                statementVisitor.getPath().forEach(line -> path.add(line));
                ArrayList<ArrayList<Integer>> pathList = new ArrayList<>();
//                System.out.println("If OuterConditional"+path);//[17, 43] vs [17, 43]
                outerConditionalPath.push(new ArrayList<>(path));
                pathList.add(path);

                pathMap.put(ifLines, pathList);
                paths.push(pathMap); // not executed after first
            } else {
                updateConditionalPath(ifBeginLine, ifEndLine, statementVisitor , "if");
            }

            // Process the 'then' part of the if statement.
            StateNode thenNode = new StateNode(conditionalNode.getState(), dependencies, n.getThenStmt().getBegin().get().line);
            this.previousNode = thenNode;
//            this.previousCondition = thenCondition;
            // Visit the 'then' part of the if statement.

            //TODO: This is trying to get the line numbers of the else statement
            n.getThenStmt().accept(this, arg);

            conditionalNode.setThenNode(thenNode);
        }
    }

    private void elseHelper(IfStmt n, Node arg, boolean elseCondition, IfStateNode conditionalNode, List<Set<Integer>> dependencies, Node afterIfNode) {
        if (elseCondition) {
//            this.previousCondition = elseCondition;
            Statement elseStmt = n.getElseStmt().get();
            StatementVisitor statementVisitor = new StatementVisitor();
            if(elseStmt.isIfStmt()) {
                elseStmt.accept(this, arg);
            } else {
                elseStmt.accept(statementVisitor, arg);
            }
            returnLines.add(statementVisitor.getReturnLine());
            StateNode elseNode = new StateNode(conditionalNode.getState(), dependencies, elseStmt.getBegin().get().line);
            this.previousNode = elseNode;

            int elseBeginLine = n.getElseStmt().get().getBegin().get().line;
            int elseEndLine = n.getElseStmt().get().getEnd().get().line;

            if (paths.empty()) {
                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> pathMap = new HashMap<>();
                ArrayList<Integer> ifLines = new ArrayList<>();
                ifLines.add(elseBeginLine);
                ifLines.add(elseEndLine);

                ArrayList<Integer> path = new ArrayList<>();
                statementVisitor.getPath().forEach(line -> path.add(line));
                ArrayList<ArrayList<Integer>> pathList = new ArrayList<>();
//                System.out.println("If OuterConditional"+path);//[17, 43] vs [17, 43]

                outerConditionalPath.push(new ArrayList<>(path));
                pathList.add(path);


                pathMap.put(ifLines, pathList);
                paths.push(pathMap); // not executed after first
            } else {
                updateConditionalPath(elseBeginLine, elseEndLine, statementVisitor, "else");
            }

            //TODO: This is trying to get the line numbers of the else statement
            if(!elseStmt.isIfStmt()) {

                elseStmt.accept(this, arg);
            }


            conditionalNode.setElseNode(elseNode);
            // Merge 'then' and 'else' states.
            afterIfNode.setState(afterIfNode.mergeStates(this.previousNode.getState()));
        }
    }

    private void updateConditionalPath(int beginLine, int endLine, StatementVisitor statementVisitor, String pathType) {
        Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> newOuterConditionalMap = new HashMap<>();
        Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> outerConditional = paths.pop();
        ArrayList<Integer> key = new ArrayList<>(outerConditional.keySet()).get(0);

        System.out.println("Key:" + key.get(0) + " vs " + key.get(1));
        System.out.println("Key:" + beginLine + " vs " + endLine);
        System.out.println(key.get(0) < beginLine && key.get(1) > endLine);

        if (key.get(0) < beginLine && key.get(1) > endLine) {
            ArrayList<ArrayList<Integer>> pathList = outerConditional.get(key);
            int pathListSize = pathList.size();
            System.out.println("139" + pathList);

            ArrayList<Integer> parentPath;
            if (!outerConditionalPath.isEmpty()) {
                System.out.println("sss" + outerConditionalPath);
                System.out.println(pathType);
                parentPath = new ArrayList<>(pathType.equals("else") ? outerConditionalPath.pop() : outerConditionalPath.peek());
                System.out.println("178" + outerConditionalPath);
            } else {
                parentPath = new ArrayList<>();
            }
            System.out.println("150" + statementVisitor.getPath());
            for (int i = 0; i < statementVisitor.getPath().size(); i++) {
                if(!parentPath.contains(statementVisitor.getPath().get(i))) {
                    parentPath.add(statementVisitor.getPath().get(i));
                }
            }
//                System.out.println("statementVisitor.getPath()" + statementVisitor.getPath());
            System.out.println("149" + pathList);//[46, 19, 21, 28, 23]

            ArrayList<Integer> currentPath = new ArrayList<>(pathList.get(pathListSize - 1));
//            System.out.println("Current Path " + pathType + currentPath);
            System.out.println("179" + currentPath);
            System.out.println("179" + statementVisitor.getPath());
            for (int i = 0; i < statementVisitor.getPath().size(); i++) {
                if(!currentPath.contains(statementVisitor.getPath().get(i))) {
                    currentPath.add(statementVisitor.getPath().get(i));
                }
            }
           // currentPath.addAll(statementVisitor.getPath());
            System.out.println("Current Path " + pathType + currentPath);
            System.out.println("Parent Path " + pathType + parentPath);
            boolean pathsMatch = isPathMatch(parentPath, currentPath, true); // false

            System.out.println("Path Match" + pathsMatch);
            updateOuterConditional(pathsMatch, pathList, pathListSize, currentPath, parentPath, outerConditional, key);
//
//            if (statementVisitor.getSize() != 0) {
//                updateOuterConditional(pathsMatch, pathList, pathListSize, currentPath, parentPath, outerConditional, key);
//            }

            System.out.println(statementVisitor.getSize());

            System.out.println("outerConditional" + pathList);
            outerConditional.put(key, pathList);
        } else {
            // Branch specific to if or else based on pathType
            ArrayList<Integer> lines = new ArrayList<>();
            lines.add(beginLine);
            lines.add(endLine);
            ArrayList<Integer> path = new ArrayList<>();
            // Common logic for both if and else paths.
            System.out.println("Key" + path);
            System.out.println(statementVisitor.getPath());
            statementVisitor.getPath().forEach(line -> path.add(line));
            ArrayList<ArrayList<Integer>> pathList = new ArrayList<>();
            pathList.add(path);
            outerConditionalPath.push(new ArrayList<>(path)); // 45 vs 45
            newOuterConditionalMap.put(lines, pathList);
        }
        paths.push(outerConditional);
        //check the empty path if so ignore it
        if (!newOuterConditionalMap.isEmpty() &&!newOuterConditionalMap.values().toArray()[0].toString().equals("[[]]")){
            paths.push(newOuterConditionalMap);
        }
    }

    private void updateOuterConditional(boolean pathesMatch, ArrayList<ArrayList<Integer>> pathList, int pathListSize, ArrayList<Integer> currentPath, ArrayList<Integer> parentPath, Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> outerConditional, ArrayList<Integer> key) {
        if (pathesMatch) {
            pathList.remove(pathListSize -1);
            pathList.add(pathListSize -1, currentPath);
            outerConditionalPath.push(new ArrayList<>(pathList.get(pathListSize -1)));
        }  else {
            pathList.add(parentPath);
            outerConditionalPath.push(new ArrayList<>(parentPath)); // [45, 19, 21, 28] not here
        }
        outerConditional.put(key, pathList);
    }

    private static boolean isPathMatch(ArrayList<Integer> parentPath, ArrayList<Integer> currentPath, boolean pathesMatch) {
        if (parentPath.size() == currentPath.size()) {
            for (int i = 0; i< parentPath.size(); i++) {
                if (parentPath.get(i) != currentPath.get(i)) {
                    pathesMatch = false;
                    break;
                }
            }
        } else {
            pathesMatch = false;
        }
        return pathesMatch;
    }

    @Override
    public void visit(IfStmt n, Node arg) {
        Expression originalCondition =previousCondition;
        Expression thenCondition = n.getCondition();
        System.out.println("test");
//        System.out.println(n.getThenStmt());
//        System.out.println(n.getElseStmt().get().isIfStmt());
//        if (previousCondition == null) {
//            thenCondition = n.getCondition();
//        } else {
//            thenCondition = new BinaryExpr(previousCondition, n.getCondition(), BinaryExpr.Operator.AND);
//        }
        List<Set<Integer>> dependencies = new ArrayList<>(this.previousNode.getDependencies());

        thenCondition.ifBinaryExpr(binaryExpr -> {
            addBinaryExpressionDependencies(binaryExpr, dependencies);
        });

        // Create a new IfStateNode, encapsulating the current state and the extracted condition.
        IfStateNode conditionalNode = new IfStateNode(
                this.previousNode.getState(), dependencies, n.getBegin().get().line, thenCondition
        );
        this.previousNode.setChild(conditionalNode);
        Expr thenExpr = evaluateExpression(thenCondition, parameterSymbols, ctx);
        Solver solver = ctx.mkSolver();
        solver.add((BoolExpr) thenExpr);
//        this.z3Solver.setCondition(thenCondition);

        boolean thenConditionResult = solver.check() == Status.SATISFIABLE;

        Expression elseCondition = new UnaryExpr(n.getCondition(), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
        Expr elseExpr = evaluateExpression(elseCondition, parameterSymbols, ctx);
        solver.reset();
        solver.add((BoolExpr) elseExpr);
//        this.z3Solver.setCondition(elseCondition);
        boolean elseConditionResult = solver.check() == Status.SATISFIABLE;
        System.out.println("thenConditionResult" + thenConditionResult);

        thenHelper(n, arg, thenConditionResult, conditionalNode, dependencies);


        // Create a copy of the 'then' state to potentially merge with the 'else' state.
        Node afterIfNode = new StateNode();
        afterIfNode.setState(this.previousNode.getState());

//        System.out.println("Step 6 Else Condition");
        // If an 'else' part exists, process it similarly.
        if(n.getElseStmt().isPresent()) {
            if(!outerConditionalPath.isEmpty()) {
                outerConditionalPath.pop();
            }
            System.out.println("outerConditionalPath" + paths   );
//            System.out.println( outerConditionalPath.pop());

            elseHelper(n, arg, elseConditionResult, conditionalNode, dependencies, afterIfNode);
        }

        // Clean up by removing the last set of dependencies after leaving the if statement.
        List<Set<Integer>> originalDependencies = new ArrayList<>(this.previousNode.getDependencies());
        if (!originalDependencies.isEmpty()) {
            originalDependencies.remove(conditionalNode.getDependencies().size() - 1);
        }

//        System.out.println("Step 9 Visited Line");
        // Add the lines visited by the if statement to the visitedLine set to avoid overwriting the state
        for (int i = n.getBegin().get().line; i <= n.getEnd().get().line; i++) {
            visitedLine.add(i);
        }

        // Set the dependencies of the afterIfNode to the original dependencies
        afterIfNode.setDependencies(originalDependencies);
        afterIfNode.setLineNumber(n.getEnd().get().line);
        conditionalNode.setChild(afterIfNode);

        this.previousCondition = originalCondition;
        this.previousNode = afterIfNode;
//        System.out.println("Path " + paths);
    }


    @Override
    public void visit(VariableDeclarationExpr n, Node arg) {
        // Process the node to update the state with variable declarations
        n.getVariables().forEach(var -> {
            ArrayList<Integer> lines = new ArrayList<Integer>();
            String variableName = var.getNameAsString();

            Expr rhsExpr = null;
            if (var.getInitializer().isPresent()) {
                // Evaluate the expression and update the map
                rhsExpr = evaluateExpression(var.getInitializer().get(), parameterSymbols, this.ctx);
                parameterSymbols.put(variableName, rhsExpr);
                System.out.println("!!!!!!" + parameterSymbols);
            }

            int line = var.getBegin().map(pos -> pos.line).orElse(-1); // Use -1 to indicate unknown line numbers
            lines.add(line);
//            processAssignStaticValue(variableName, var.getInitializer().get());
            previousNode = processNode(variableName, lines, previousNode);
        });
    }

    @Override
    public void visit(AssignExpr n, Node arg) {
        ArrayList<Integer> lines = new ArrayList<Integer>();
        String variableName = n.getTarget().toString();
        String valueName = n.getValue().toString();
        String op = n.getOperator().toString();


        updateSymbolMapWithAssignment(n, parameterSymbols, ctx);

        System.out.println("!!!!!!" + variableName + n.getValue().toString());
        int line = n.getBegin().map(pos -> pos.line).orElse(-1); // Same use of -1 for unknown line numbers
        lines.add(line);
        Set<Integer> valLineNumbers = this.assignValLineNumbers(lines, valueName);
        if (!valLineNumbers.isEmpty()) {
            lines.addAll(valLineNumbers);
        }
//        processAssignStaticValue(variableName, n.getValue());
        previousNode = processNode(variableName, lines, previousNode);
    }

    private void updateSymbolMapWithAssignment(AssignExpr assignExpr, Map<String, Expr> symbolMap, Context ctx) {


        String targetVar = assignExpr.getTarget().toString();
        Expr currentExpr = symbolMap.getOrDefault(targetVar, null);

        switch (assignExpr.getOperator()) {
            case ASSIGN:
                Expr evaluatedExpr = evaluateExpression(assignExpr.getValue(), symbolMap, ctx);
                symbolMap.put(targetVar, evaluatedExpr); // Update the map with the new or updated symbolic expression
                break;
            case PLUS:
                if (currentExpr == null) currentExpr = ctx.mkIntConst(targetVar); // Fallback if not in map
                Expr additionResult = ctx.mkAdd(new Expr[]{currentExpr, evaluateExpression(assignExpr.getValue(), symbolMap, ctx)});
                symbolMap.put(targetVar, additionResult);
                break;
            case MINUS:
                if (currentExpr == null) currentExpr = ctx.mkIntConst(targetVar); // Fallback if not in map
                Expr subtractionResult = ctx.mkSub(new Expr[]{currentExpr, evaluateExpression(assignExpr.getValue(), symbolMap, ctx)});
                symbolMap.put(targetVar, subtractionResult);
                break;
            case MULTIPLY:
                if (currentExpr == null) currentExpr = ctx.mkIntConst(targetVar); // If the variable isn't in the map, initialize it
                Expr multiplicationResult = ctx.mkMul(new Expr[]{currentExpr, evaluateExpression(assignExpr.getValue(), symbolMap, ctx)});
                symbolMap.put(targetVar, multiplicationResult);
                break;
            case DIVIDE:
                if (currentExpr == null) currentExpr = ctx.mkIntConst(targetVar); // If the variable isn't in the map, initialize it
                Expr divisionResult = ctx.mkDiv((ArithExpr)currentExpr, (ArithExpr)evaluateExpression(assignExpr.getValue(), symbolMap, ctx));
                symbolMap.put(targetVar, divisionResult);
                break;
            default:
                Expr evaluatedExprDefault = evaluateExpression(assignExpr.getValue(), symbolMap, ctx);
                symbolMap.put(targetVar, evaluatedExprDefault); // Update the map with the new or updated symbolic expression
                break;
        }


    }

    private Expr evaluateExpression(Expression expr, Map<String, Expr> symbolMap, Context ctx) {
        if (expr instanceof IntegerLiteralExpr) {
            int value = ((IntegerLiteralExpr) expr).asInt();
            return ctx.mkInt(value); // Direct static value
        } else if (expr instanceof BooleanLiteralExpr) {
            boolean value = ((BooleanLiteralExpr) expr).getValue();
            return value ? ctx.mkTrue() : ctx.mkFalse(); // Direct static value
        } else if (expr instanceof NameExpr) {
            String varName = ((NameExpr) expr).getNameAsString();
            return symbolMap.getOrDefault(varName, ctx.mkIntConst(varName)); // Existing symbol or new symbol for uninitialized variable
        } else if(expr instanceof UnaryExpr) {
            UnaryExpr unaryExpr = (UnaryExpr) expr;
            Expr unary = evaluateExpression(unaryExpr.getExpression(), symbolMap, ctx);
            switch (unaryExpr.getOperator()) {
                case LOGICAL_COMPLEMENT:
                    return ctx.mkNot((BoolExpr) unary);
            }
        }  else if (expr instanceof MethodCallExpr) {
            MethodCallExpr methodCall = (MethodCallExpr) expr;
            String methodName = methodCall.getNameAsString();
            // Generate a unique symbol for this function call
            // For simplicity, using method name with a counter or UUID can be a starting point
            String uniqueSymbolName = methodName + "_" + UUID.randomUUID().toString();
            // Depending on the expected return type, create a new symbolic constant
            // Here assuming an integer return type for simplicity
            Expr newSymbol = ctx.mkIntConst(uniqueSymbolName);
            // Optionally, you might want to store some additional metadata about the function call
            // e.g., its arguments, to use later in your analysis or when refining your symbolic execution model
            symbolMap.put(uniqueSymbolName, newSymbol);
            return newSymbol;
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr) expr;
            Expr left = evaluateExpression(binaryExpr.getLeft(), symbolMap, ctx);
            Expr right = evaluateExpression(binaryExpr.getRight(), symbolMap, ctx);
            switch (binaryExpr.getOperator()) {
                case PLUS:
                    return ctx.mkAdd(new Expr[]{left, right});
                case MINUS:
                    return ctx.mkSub(new Expr[]{left, right});
                case MULTIPLY:
                    return ctx.mkMul(new Expr[]{left, right});
                case DIVIDE:
                    return ctx.mkDiv((ArithExpr)left, (ArithExpr)right);
                // Handle other operators as needed
                case EQUALS:
                    return ctx.mkEq(left, right);
                case GREATER:
                    return ctx.mkGt((ArithExpr) left, (ArithExpr) right);
                case LESS:
                    return ctx.mkLt((ArithExpr) left, (ArithExpr) right);
                case GREATER_EQUALS:
                    return ctx.mkGe((ArithExpr) left, (ArithExpr) right);
                case LESS_EQUALS:
                    return ctx.mkLe((ArithExpr) left, (ArithExpr) right);
                case NOT_EQUALS:
                    return ctx.mkNot(ctx.mkEq(left, right));
                case AND:
                    return ctx.mkAnd((BoolExpr) left, (BoolExpr) right);
                case OR:
                    return ctx.mkOr((BoolExpr) left, (BoolExpr) right);


            }
        }
        System.out.println("Unsupported expression type: " + expr.getClass());
        // Extend to handle more expression types as needed
        return null; // Placeholder to satisfy return requirement
    }

    @Override
    public void visit(MethodDeclaration n, Node arg) {
        ArrayList<Integer> lines = new ArrayList<Integer>();
        n.getParameters().forEach(parameter -> {
            String variableName = parameter.getNameAsString();
            System.out.println("!!!!!!!!!" + variableName);
            Sort paramSort = getTypeSort(parameter.getType(), ctx);
            // Create a Z3 symbol for the parameter name
            Symbol paramSymbol = ctx.mkSymbol(variableName);
            // Create a Z3 constant (symbolic variable) for the parameter
            Expr paramExpr = ctx.mkConst(paramSymbol, paramSort);
            parameterSymbols.put(variableName, paramExpr);
            int line = n.getBegin().get().line;
            lines.add(line);
            previousNode = processNode(variableName, lines, previousNode);
        });
        n.getBody().ifPresent(body -> body.accept(this, arg));
    }

    private static Sort getTypeSort(com.github.javaparser.ast.type.Type type, Context ctx) {
        // Example: Determine the sort based on the simple name of the type
        String typeName = type.toString();
        switch (typeName) {
            case "int":
                return ctx.getIntSort();
            case "boolean":
                return ctx.getBoolSort();
            // Add more cases for other types
            default:
                throw new IllegalArgumentException("Unsupported type: " + typeName);
        }
    }

    private void processAssignStaticValue(String variableName, Expression value) {
//        System.out.println(value);
//        this.z3Solver.setCondition(previousCondition);
//        boolean thenCondition = this.z3Solver.solve();
//        boolean else
//        if( this.z3Solver.solve()) {
//            this.z3Solver.removeStaticVariable(variableName);
//            return;
//        }
        if (value.isBooleanLiteralExpr() || value.isIntegerLiteralExpr() || value.isUnaryExpr() || value.isBinaryExpr()) {
            this.z3Solver.addStaticVariableValues(variableName, value);

//            System.out.println(this.z3Solver.getStaticVariableValues());
        } else {
            // means the value is a variable
            if(this.z3Solver.isVariableValueKnown(value.toString())){
                this.z3Solver.addStaticVariableValues(variableName, this.z3Solver.getVariableValue(value.toString()));
            } else {
                if (previousCondition != null) {
                    this.z3Solver.setCondition(previousCondition);
                    boolean thenCondition = this.z3Solver.solve();
                    System.out.println(new UnaryExpr(previousCondition, UnaryExpr.Operator.LOGICAL_COMPLEMENT));
                    this.z3Solver.setCondition(new UnaryExpr(previousCondition, UnaryExpr.Operator.LOGICAL_COMPLEMENT));
                    boolean elseCondition = this.z3Solver.solve();

                    // variableName is dynamically determined
                    if(thenCondition && elseCondition) {
                        this.z3Solver.removeStaticVariable(variableName);
                    }

                } else {
                    // variableName is dynamically determined
                    this.z3Solver.removeStaticVariable(variableName);
                }

            }
        }
    }


    // REQUIRES: lines[0] is the current line number always
    private Node processNode(String variableName, ArrayList<Integer> lines, Node parent) {
        if (parent == null) {
            parent = initialNode;
        }
        if (visitedLine.contains(lines.get(0))) {
            return parent;
        }
        Map<String, Set<Integer>> currentState = parent.getState();
//        System.out.println("current state" + currentState);
        if (!currentState.containsKey(variableName) || !currentState.get(variableName).contains(lines.get(0))) {

            Map<String, Set<Integer>> newState = new HashMap<>();
            for (Map.Entry<String, Set<Integer>> entry : currentState.entrySet()) {
                newState.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            // Add the new variable assignment to the state
            // computeIfAbsent is a method that returns the value of the specified key in the map

            for (int l : lines) {
                newState.computeIfAbsent(variableName, k -> new HashSet<>()).add(l);
            }

            Node newNode = new StateNode();

            // If the variable has dependencies (so in if block), add the dependencies to the new node state
            if (parent.getDependencies().size() > 0) {
                List<Set<Integer>> dependencies = new ArrayList<>(parent.getDependencies());
                for (Set<Integer> dependency : dependencies) {
                    newState.computeIfAbsent(variableName, k -> new HashSet<>()).addAll(dependency);
                }
            }
            newNode.setState(newState);
            newNode.setLineNumber(lines.get(0));
            newNode.setDependencies(parent.getDependencies());

            parent.setChild(newNode);
            return newNode;
        }

        return parent;
    }

    private Set<Integer> assignValLineNumbers(ArrayList<Integer> lines, String valueName) {
        Map<String, Set<Integer>> currentState;
        if (previousNode == null) {
            currentState = initialNode.getState();
        } else {
            currentState = previousNode.getState();
        }
        if (currentState.containsKey(valueName)) {
            return currentState.get(valueName);
        }
        return new HashSet<>();
    }

}