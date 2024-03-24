package parser;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import graph.IfStateNode;
import graph.Node;
import graph.StateNode;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

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

        // Prepare a list to hold dependencies from the binary expression in the condition,
        // assuming we're not dealing with arithmetic expressions for simplicity.
        List<Set<Integer>> dependencies = new ArrayList<>(this.previousNode.getDependencies());
        Set<Integer> binaryExprDependencies = new HashSet<>();

        // Extract dependencies from both sides of a binary expression if present.
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
        originalDependencies.remove(conditionalNode.getDependencies().size() - 1);

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
            String variableName = var.getNameAsString();
            int line = var.getBegin().map(pos -> pos.line).orElse(-1); // Use -1 to indicate unknown line numbers
            previousNode = processNode(variableName, line, previousNode);
        });
    }

    @Override
    public void visit(AssignExpr n, Node arg) {
        String variableName = n.getTarget().toString();
        int line = n.getBegin().map(pos -> pos.line).orElse(-1); // Same use of -1 for unknown line numbers
        previousNode = processNode(variableName, line, previousNode);
    }

    private Node processNode(String variableName, int line, Node parent) {
        if (parent == null) {
            parent = initialNode;
        }
        if (visitedLine.contains(line)) {
            return parent;
        }
        Map<String, Set<Integer>> currentState = parent.getState();

        if (!currentState.containsKey(variableName) || !currentState.get(variableName).contains(line)) {
            Map<String, Set<Integer>> newState = new HashMap<>();
            for (Map.Entry<String, Set<Integer>> entry : currentState.entrySet()) {
                newState.put(entry.getKey(), new HashSet<>(entry.getValue()));
            }
            // Add the new variable assignment to the state
            // computeIfAbsent is a method that returns the value of the specified key in the map
            newState.computeIfAbsent(variableName, k -> new HashSet<>()).add(line);
            Node newNode = new StateNode(newState, parent.getDependencies(), line);
            parent.setChild(newNode);
            return newNode;
        }

        return parent;
    }

}
