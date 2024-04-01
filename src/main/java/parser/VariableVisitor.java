package parser;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr.Operator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import common.functions.FunctionContext;
import graph.IfStateNode;
import graph.Node;
import graph.StateNode;

import com.github.javaparser.ast.expr.BinaryExpr;

import org.checkerframework.checker.units.qual.A;
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

    private Path path;
    private FunctionContext functionCtx;

    private ArrayList<Integer> conditionalBlocks;
    Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths;
    Stack<ArrayList<Integer>> outerConditionalPath;

    // Keep track of visited lines to avoid overwriting the state updated from if statements by the assignment statements
    private HashSet<Integer> visitedLine = new HashSet<>();

    public VariableVisitor(Node initialNode) {
        this.initialNode = initialNode;
        this.z3Solver = new Z3Solver();
        conditionalBlocks = new ArrayList<>();
        functionCtx = new FunctionContext();
        path = new Path();
        paths = new Stack<>();
        outerConditionalPath = new Stack<>();
    }



    @Override
    public void visit(IfStmt n, Node arg) {
//        System.out.println("Visit if stamement");

//        System.out.println("Set new path");
//        System.out.println("Whole if lines start:" + n.getThenStmt().getBegin().get().line);
//        System.out.println("Whole if lines end: " + n.getThenStmt().getEnd().get().line);


//        System.out.println(n.getThenStmt().getBegin().get());
//        n.getThenStmt().getChildNodes().forEach(System.out::println);
//        System.out.println(n.getThenStmt().getEnd().get().line);
        // Start by capturing the condition of the if statement.
        Expression originalCondition =previousCondition;
        Expression thenCondition = null;
        if (previousCondition == null) {
            thenCondition = n.getCondition();
        } else {
            thenCondition = new BinaryExpr(previousCondition, n.getCondition(), BinaryExpr.Operator.AND);
        }
//        System.out.println("Step 2");
        // Prepare a list to hold dependencies from the binary expression in the condition,
        // assuming we're not dealing with arithmetic expressions for simplicity.
        List<Set<Integer>> dependencies = new ArrayList<>(this.previousNode.getDependencies());
        Set<Integer> binaryExprDependencies = new HashSet<>();

        thenCondition.ifBinaryExpr(binaryExpr -> {
            binaryExpr.getLeft().ifNameExpr(nameExpr ->
                    binaryExprDependencies.addAll(this.previousNode.getState().get(nameExpr.getNameAsString())));
            binaryExpr.getRight().ifNameExpr(nameExpr ->
                    binaryExprDependencies.addAll(this.previousNode.getState().get(nameExpr.getNameAsString())));
            dependencies.add(binaryExprDependencies);
        });

        // Create a new IfStateNode, encapsulating the current state and the extracted condition.
        IfStateNode conditionalNode = new IfStateNode(
                this.previousNode.getState(), dependencies, n.getBegin().get().line, thenCondition
        );
//        System.out.println("Step 3");
        conditionalNode.setCondition(thenCondition);
        this.previousNode.setChild(conditionalNode);
        this.z3Solver.setCondition(thenCondition);

        ArrayList<Integer> p = new ArrayList<>();
//        System.out.println("Step 4");
        if (this.z3Solver.solve()) {
            StatementVisitor statementVisitor = new StatementVisitor();
            n.getThenStmt().accept(statementVisitor, arg);

            int ifBeginLine = n.getThenStmt().getBegin().get().line;
            int ifEndLine = n.getThenStmt().getEnd().get().line;
            if (paths.empty()) {
                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> pathMap = new HashMap<>();
                ArrayList<Integer> ifLines = new ArrayList<>();
                ifLines.add(ifBeginLine);
                ifLines.add(ifEndLine);

                ArrayList<Integer> path = new ArrayList<Integer>();
                statementVisitor.getPath().forEach(line -> path.add(line));



                ArrayList<ArrayList<Integer>> pathList = new ArrayList<>();

                outerConditionalPath.push(new ArrayList<>(path));
                pathList.add(path);



                pathMap.put(ifLines, pathList);
                paths.push(pathMap);
            } else {
                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> newOuterConditionalMap = new HashMap<>();
                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> outerConditional = paths.pop();
                ArrayList<Integer> key = new ArrayList<>(outerConditional.keySet()).get(0);
                if (key.get(0) < ifBeginLine && key.get(1) > ifEndLine) {

                    ArrayList<ArrayList<Integer>> pathList = outerConditional.get(key);
                    int pathListSize = pathList.size();
                    System.out.println("key" +key);
                    System.out.println("value" +pathList);
                    System.out.println("Here 1"+outerConditionalPath);

                    ArrayList<Integer> parentPath = new ArrayList<>(outerConditionalPath.peek());
                    parentPath.addAll(statementVisitor.getPath());
                    System.out.println("Parent Path If" + parentPath);

                    ArrayList<Integer> currentPath = new ArrayList<>(pathList.get(pathListSize-1));
                    currentPath.addAll(statementVisitor.getPath());
                    System.out.println("Current Path If" + currentPath);
                    boolean pathesMatch = true;
                    if (parentPath.size() == currentPath.size()) {
                        for (int i=0; i<parentPath.size();i++) {
                            if (parentPath.get(i) != currentPath.get(i)) {
                                pathesMatch = false;
                                break;
                            }
                        }
                    } else {
                        pathesMatch = false;
                    }

                    if (pathesMatch) {
                        pathList.remove(pathListSize-1);
                        pathList.add(pathListSize-1, currentPath);
                        outerConditionalPath.push(new ArrayList<>(pathList.get(pathListSize-1)));
                    } else {
                        pathList.add(parentPath);
                        outerConditionalPath.push(new ArrayList<>(parentPath));
                    }
                    outerConditional.put(key, pathList);
                    System.out.println("Here 2"+outerConditionalPath);
                } else {
                    ArrayList<Integer> ifLines = new ArrayList<>();
                    ifLines.add(ifBeginLine);
                    ifLines.add(ifEndLine);

                    ArrayList<Integer> path = new ArrayList<>();
                    statementVisitor.getPath().forEach(line -> path.add(line));

                    ArrayList<ArrayList<Integer>> pathList = new ArrayList<>();
                    pathList.add(path);

                    outerConditionalPath.push(new ArrayList<>(path));

                    newOuterConditionalMap.put(ifLines, pathList);
                }

                paths.push(outerConditional);
                if (!newOuterConditionalMap.isEmpty()) {
                    paths.push(newOuterConditionalMap);
                }
                System.out.println("Path"+ paths);
                System.out.println("OuterConditional"+outerConditionalPath);
            }

            // Process the 'then' part of the if statement.
            StateNode thenNode = new StateNode(conditionalNode.getState(), dependencies, n.getThenStmt().getBegin().get().line);
            this.previousNode = thenNode;
            this.previousCondition = thenCondition;
            // Visit the 'then' part of the if statement.

//            System.out.println("Step 5");
            //TODO: This is trying to get the line numbers of the else statement
            n.getThenStmt().accept(this, arg);


//            statementVisitor.getPath().forEach(line -> path.addLine(line));
            // Update the state of the 'then' node with the state after visiting the 'then' part.
            conditionalNode.setThenNode(thenNode);
        }

//        System.out.println("Step 5");
        // Create a copy of the 'then' state to potentially merge with the 'else' state.
        Node afterIfNode = new StateNode();
        afterIfNode.setState(this.previousNode.getState());

//        System.out.println("Step 6 Else Condition");
        // If an 'else' part exists, process it similarly.
        if(n.getElseStmt().isPresent()) {
            outerConditionalPath.pop();

            Expression elseCondition = null;
            if(originalCondition == null) {
                elseCondition = new UnaryExpr(n.getCondition(), UnaryExpr.Operator.LOGICAL_COMPLEMENT);
            } else {
                elseCondition = new BinaryExpr(originalCondition, new UnaryExpr(n.getCondition(), UnaryExpr.Operator.LOGICAL_COMPLEMENT), BinaryExpr.Operator.AND);
            }
            this.z3Solver.setCondition(elseCondition);

//            System.out.println("Step 7");
            if (this.z3Solver.solve()) {
                this.previousCondition = elseCondition;
                Statement elseStmt = n.getElseStmt().get();
                StateNode elseNode = new StateNode(conditionalNode.getState(), dependencies, elseStmt.getBegin().get().line);
                this.previousNode = elseNode;

                StatementVisitor statementVisitor = new StatementVisitor();
                elseStmt.accept(statementVisitor, arg);

                int elseBeginLine = n.getElseStmt().get().getBegin().get().line;
                int elseEndLine = n.getElseStmt().get().getEnd().get().line;

                System.out.println("Else OuterConditional"+outerConditionalPath);
                System.out.println("Else Begin Line" + elseBeginLine);
                System.out.println("Else End Line" + elseEndLine);


                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> newOuterConditionalMap = new HashMap<>();
                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> outerConditional = paths.pop();
                ArrayList<Integer> key = new ArrayList<>(outerConditional.keySet()).get(0);
                if (key.get(0) < elseBeginLine && key.get(1) > elseEndLine) {

                    ArrayList<ArrayList<Integer>> pathList = outerConditional.get(key);
                    int pathListSize = pathList.size();

                    System.out.println("else key" +key);
                    System.out.println("else value" +pathList);
                    System.out.println("Here 1"+outerConditionalPath);
                    ArrayList<Integer> parentPath;
                    if (!outerConditionalPath.isEmpty()) {
                        parentPath = new ArrayList<>(outerConditionalPath.pop());
                    } else {
                        parentPath = new ArrayList<>();
                    }

                    parentPath.addAll(statementVisitor.getPath());
                    System.out.println("Parent Path Else" + parentPath);

                    ArrayList<Integer> currentPath = new ArrayList<>(pathList.get(pathListSize-1));
                    currentPath.addAll(statementVisitor.getPath());
                    System.out.println("Current Path Else" + currentPath);
                    boolean pathesMatch = true;
                    if (parentPath.size() == currentPath.size()) {
                        for (int i=0; i<parentPath.size();i++) {
                            if (parentPath.get(i) != currentPath.get(i)) {
                                pathesMatch = false;
                                break;
                            }
                        }
                    } else {
                        pathesMatch = false;
                    }

                    if (statementVisitor.getSize() != 0) {
                        if (pathesMatch) {
                            pathList.remove(pathListSize-1);
                            pathList.add(pathListSize-1, currentPath);
                            outerConditionalPath.push(new ArrayList<>(pathList.get(pathListSize-1)));
                        } else {
                            pathList.add(parentPath);
                            outerConditionalPath.push(new ArrayList<>(parentPath));
                        }
                    }
                    outerConditional.put(key, pathList);
                    System.out.println("Here 2"+outerConditionalPath);
                } else {
                    ArrayList<Integer> ifLines = new ArrayList<>();
                    ifLines.add(elseBeginLine);
                    ifLines.add(elseEndLine);

                    ArrayList<Integer> path = new ArrayList<>();
                    statementVisitor.getPath().forEach(line -> path.add(line));

                    ArrayList<ArrayList<Integer>> pathList = new ArrayList<>();
                    pathList.add(path);

                    outerConditionalPath.push(new ArrayList<>(path));

                    newOuterConditionalMap.put(ifLines, pathList);
                }

                paths.push(outerConditional);
                if (!newOuterConditionalMap.isEmpty()) {
                    paths.push(newOuterConditionalMap);
                }
                System.out.println("Path"+ paths);
                System.out.println("OuterConditional"+outerConditionalPath);


                // Visit the 'else' part of the if statement.

                //TODO: This is trying to get the line numbers of the else statement
                elseStmt.accept(this, arg);
//                StatementVisitor statementVisitor = new StatementVisitor();
//                elseStmt.accept(statementVisitor, arg);
//                statementVisitor.getPath().forEach(line -> path.addLine(line));


                conditionalNode.setElseNode(elseNode);
                // Merge 'then' and 'else' states.
                afterIfNode.setState(afterIfNode.mergeStates(this.previousNode.getState()));
            }
        }

        // Clean up by removing the last set of dependencies after leaving the if statement.
        List<Set<Integer>> originalDependencies = new ArrayList<>(this.previousNode.getDependencies());
//        System.out.println("Original Dependencies"+ originalDependencies);
//        System.out.println("Current Dependencies"+ conditionalNode.getDependencies().toString());
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
        System.out.println("Path " + paths);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Node arg) {
        // Process the node to update the state with variable declarations
        n.getVariables().forEach(var -> {
            ArrayList<Integer> lines = new ArrayList<Integer>();
            String variableName = var.getNameAsString();
            int line = var.getBegin().map(pos -> pos.line).orElse(-1); // Use -1 to indicate unknown line numbers
            lines.add(line);
            processAssignStaticValue(variableName, var.getInitializer().get());
            previousNode = processNode(variableName, lines, previousNode);
        });
    }

    @Override
    public void visit(AssignExpr n, Node arg) {
        ArrayList<Integer> lines = new ArrayList<Integer>();
        String variableName = n.getTarget().toString();
        String valueName = n.getValue().toString();
//        System.out.println(variableName + n.getValue().toString());
        int line = n.getBegin().map(pos -> pos.line).orElse(-1); // Same use of -1 for unknown line numbers
        lines.add(line);
        Set<Integer> valLineNumbers = this.assignValLineNumbers(lines, valueName);
        if (!valLineNumbers.isEmpty()) {
            lines.addAll(valLineNumbers);
        }
        processAssignStaticValue(variableName, n.getValue());
        previousNode = processNode(variableName, lines, previousNode);
    }

    @Override
    public void visit(MethodDeclaration n, Node arg) {
        ArrayList<Integer> lines = new ArrayList<Integer>();
        n.getParameters().forEach(parameter -> {
            String variableName = parameter.getNameAsString();
            int line = n.getBegin().get().line;
            lines.add(line);
            previousNode = processNode(variableName, lines, previousNode);
        });
        n.getBody().ifPresent(body -> body.accept(this, arg));
    }

    private void processAssignStaticValue(String variableName, Expression value) {
        if (value.isBooleanLiteralExpr() || value.isIntegerLiteralExpr() || value.isUnaryExpr()) {
            this.z3Solver.addStaticVariableValues(variableName, value);
            System.out.println(this.z3Solver.getStaticVariableValues());
        } else {
            // means the value is a variable
            if(this.z3Solver.isVariableValueKnown(value.toString())){
                this.z3Solver.addStaticVariableValues(variableName, this.z3Solver.getVariableValue(value.toString()));
            } else {
                System.out.println("Variable value is not known");
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
