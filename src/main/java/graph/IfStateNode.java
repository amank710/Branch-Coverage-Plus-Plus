package graph;

import com.github.javaparser.ast.expr.Expression;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class IfStateNode extends Node{
    private Map<String, Set<Integer>> state;
    private int lineNum;

    private Node child;
    private Node thenNode;
    private Node elseNode;

    private Expression condition;

    public IfStateNode(Map<String, Set<Integer>> prevState, int lineNum) {
        this.state = prevState != null ? new HashMap<>(prevState) : new HashMap<>();
        this.lineNum = lineNum;
    }

    public Map<String, Set<Integer>> getState() {
        return state;
    }

    public void setThenNode(StateNode thenNode) {
        this.thenNode = thenNode;
    }

    public int getLineNumber() {
        return lineNum;
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

    public Map<String, Set<Integer>> getStateFromLine(int line) {
//        System.out.println("State: " + getState() + " getLineNumber: " + getLineNumber());
        if (line == this.lineNum) {
            return getState();
        } else {
            Map<String, Set<Integer>> childState;
            if (thenNode != null) {
                childState = thenNode.getStateFromLine(line);
                if (childState != null) {
                    return childState;
                }
            }
            if (elseNode != null) {
                childState = elseNode.getStateFromLine(line);
                if (childState != null) {
                    return childState;
                }
            }
            return null;
        }
    }


   public void visualize() {
        visualize(0); // Start visualization from the root with an indentation depth of 0
    }
    public void visualize(int depth) {
        String indentation = createIndentation(depth);
        System.out.println(indentation + "If Condition: " + this.getCondition());

        // Visualize the 'then' branch, increasing the depth for indentation
        System.out.println(indentation + "Then:");
        this.getThenNode().visualize(depth + 1);

        // If there's an 'else' branch, visualize it as well
        if (this.getElseNode() != null) {
            System.out.println(indentation + "Else:");
            this.getElseNode().visualize(depth + 1);
        }

        if (this.getChild() != null) {
            this.getChild().visualize(depth);
        }
    }
}