package parser;

import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr.Operator;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import graph.IfStateNode;
import graph.Node;
import graph.StateNode;

import z3.Z3Solver;

import java.util.*;

// This is a visitor class that visits the AST nodes and builds the variable map
public class VariableVisitor extends VoidVisitorAdapter<Node> {

    // need to keep the current state of the node instead of passing it as an argument
    // this is because when VoidVisitorAdapter visits nodes other than VariableDeclartion and Assignemnt,
    // the argument is always be initialized to null
    private Node initialNode;
    private Node previousNode;

    // Keep track of visited lines to avoid overwriting the state updated from if statements by the assignment statements
    private HashSet<Integer> visitedLine = new HashSet<>();

    public VariableVisitor(Node initialNode) {
        this.initialNode = initialNode;
    }

    @Override
    public void visit(IfStmt n, Node arg) {
        // Start by capturing the condition of the if statement.
        Expression condition = n.getCondition();
        Z3Solver checker = new Z3Solver(condition);
        if (!checker.solve()) return;

        Expression elseCondition =  new UnaryExpr(condition, Operator.LOGICAL_COMPLEMENT);
        Z3Solver elseChecker = new Z3Solver(elseCondition);
        if (!elseChecker.solve()) return;

        // Prepare a list to hold dependencies from the binary expression in the condition,
        // assuming we're not dealing with arithmetic expressions for simplicity.
        List<Set<Integer>> dependencies = new ArrayList<>(this.previousNode.getDependencies());
        Set<Integer> binaryExprDependencies = new HashSet<>();

        condition.ifBinaryExpr(binaryExpr -> {
            binaryExpr.getLeft().ifNameExpr(nameExpr ->
                    binaryExprDependencies.addAll(this.previousNode.getState().get(nameExpr.getNameAsString())));
            binaryExpr.getRight().ifNameExpr(nameExpr ->
                    binaryExprDependencies.addAll(this.previousNode.getState().get(nameExpr.getNameAsString())));
            dependencies.add(binaryExprDependencies);
        });

        // Create a new IfStateNode, encapsulating the current state and the extracted condition.
        IfStateNode conditionalNode = new IfStateNode(
                this.previousNode.getState(), dependencies, n.getBegin().get().line, condition
        );

        conditionalNode.setCondition(condition);
        this.previousNode.setChild(conditionalNode);

        // Process the 'then' part of the if statement.
        StateNode thenNode = new StateNode(conditionalNode.getState(), dependencies, n.getThenStmt().getBegin().get().line);
        this.previousNode = thenNode;
        // Visit the 'then' part of the if statement.
        n.getThenStmt().accept(this, arg);
        // Update the state of the 'then' node with the state after visiting the 'then' part.
        conditionalNode.setThenNode(thenNode);

        // Create a copy of the 'then' state to potentially merge with the 'else' state.
        Node afterIfNode = new StateNode();
        afterIfNode.setState(this.previousNode.getState());

        // If an 'else' part exists, process it similarly.
        if(n.getElseStmt().isPresent()) {
            Statement elseStmt = n.getElseStmt().get();
            StateNode elseNode = new StateNode(conditionalNode.getState(), dependencies, elseStmt.getBegin().get().line);
            this.previousNode = elseNode;
            // Visit the 'else' part of the if statement.
            elseStmt.accept(this, arg);
            conditionalNode.setElseNode(elseNode);

            // Merge 'then' and 'else' states.
            afterIfNode.setState(afterIfNode.mergeStates(this.previousNode.getState()));
        }

        // Clean up by removing the last set of dependencies after leaving the if statement.
        List<Set<Integer>> originalDependencies = new ArrayList<>(this.previousNode.getDependencies());

        if (!originalDependencies.isEmpty()) {
            originalDependencies.remove(conditionalNode.getDependencies().size() - 1);
        }


        // Add the lines visited by the if statement to the visitedLine set to avoid overwriting the state
        for (int i = n.getBegin().get().line; i <= n.getEnd().get().line; i++) {
            visitedLine.add(i);
        }

        // Set the dependencies of the afterIfNode to the original dependencies
        afterIfNode.setDependencies(originalDependencies);
        afterIfNode.setLineNumber(n.getEnd().get().line);
        conditionalNode.setChild(afterIfNode);

        this.previousNode = afterIfNode;

    }

    @Override
    public void visit(VariableDeclarationExpr n, Node arg) {
        // Process the node to update the state with variable declarations
        n.getVariables().forEach(var -> {
            ArrayList<Integer> lines = new ArrayList<Integer>();
            String variableName = var.getNameAsString();
            int line = var.getBegin().map(pos -> pos.line).orElse(-1); // Use -1 to indicate unknown line numbers
            lines.add(line);
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
