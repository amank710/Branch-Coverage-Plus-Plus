package parser;

import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.VariableDeclarationExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import graph.IfStateNode;
import graph.Node;
import graph.StateNode;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class VariableVisitor extends VoidVisitorAdapter<Node> {

    // need to keep the current state of the node instead of passing it as an argument
    // this is because when VoidVisitorAdapter visits nodes other than VariableDeclartion and Assignemnt,
    // the argument is always be initialized to null
    private Node initialNode;
    private Node currentNode;

    public VariableVisitor(Node initialNode) {
        this.initialNode = initialNode;
    }

    @Override
    public void visit(IfStmt n, Node arg) {
        // Capture the condition of the if statement
        Expression condition = n.getCondition();

        // Create a conditional node based on the current state but with the condition captured
        IfStateNode conditionalNode = new IfStateNode(this.currentNode.getState(), condition.getBegin().get().line);
        conditionalNode.setCondition(condition);

        // Set the current node's next node to this conditional node to link them
        this.currentNode.setChild(conditionalNode);

        // Process the 'then' part of the if statement
        StateNode thenNode = new StateNode(conditionalNode.getState(), n.getThenStmt().getBegin().get().line);
        this.currentNode = thenNode; // Move into the 'then' branch
        n.getThenStmt().accept(this, arg); // Visit the 'then' branch
        conditionalNode.setThenNode(thenNode); // Link the processed 'then' branch
        Map<String, Set<Integer>> newState = new HashMap<>(this.currentNode.getState());

        // Process the 'else' part, if present
        if (n.getElseStmt().isPresent()) {
            StateNode elseNode = new StateNode(conditionalNode.getState(), n.getElseStmt().get().getBegin().get().line);
            this.currentNode = elseNode; // Move into the 'else' branch
            n.getElseStmt().get().accept(this, arg); // Visit the 'else' branch
            newState = this.currentNode.mergeStates(newState);
            conditionalNode.setElseNode(elseNode); // Link the processed 'else' branch
        }

        Node nextNode = new StateNode(newState, n.getEnd().get().line); // Move back to the parent node
        thenNode.setChild(nextNode);
        if (n.getElseStmt().isPresent()) {
            conditionalNode.getElseNode().setChild(nextNode);
        }
//        conditionalNode.setChild(nextNode);
        this.currentNode = nextNode;

        super.visit(n, arg);
    }

    @Override
    public void visit(VariableDeclarationExpr n, Node arg) {
        // Process the node to update the state with variable declarations
        n.getVariables().forEach(var -> {
            String variableName = var.getNameAsString();
            int line = var.getBegin().map(pos -> pos.line).orElse(-1); // Use -1 to indicate unknown line numbers
            currentNode = processNode(variableName, line, currentNode);
            super.visit(n, arg);
        });
    }

    @Override
    public void visit(AssignExpr n, Node arg) {
        // Process the node to update the state with variable assignments
        String variableName = n.getTarget().toString();
        int line = n.getBegin().map(pos -> pos.line).orElse(-1); // Same use of -1 for unknown line numbers
        currentNode = processNode(variableName, line, currentNode);
        super.visit(n, arg);
    }

    private Node processNode(String variableName, int line, Node parent) {
        if (parent == null) {
            parent = initialNode;
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
            Node newNode = new StateNode(newState, line);
            parent.setChild(newNode);
            return newNode;
        }

        return parent;
    }

}
