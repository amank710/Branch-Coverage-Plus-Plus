package graph;

import com.github.javaparser.ast.expr.Expression;

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
        // Wrapper method to control the search flow
        Node node = dfsSearchByLine(line, true);
        if (node != null) {
            return node.getState();
        }
        // If no result found and line is still positive, decrement and search again
        if (line > 1) {
            return getStateFromLine(line - 1);
        }
        return null;
    }

    @Override
    public List<Set<Integer>> getDependenciesFromLine(int line) {
        Node node = dfsSearchByLine(line, true);
        if (node != null) {
            return node.getDependencies();
        }
        if (line > 1) {
            return getDependenciesFromLine(line - 1);
        }
        return null;
    }

    @Override
    protected Node dfsSearchByLine(int line, boolean decrement) {
        if (line < 0) {
            return null;
        }
        if (line == this.lineNum) {
            return this;
        }
        if (thenNode != null) {
            Node node = thenNode.dfsSearchByLine(line, false);
            if (node != null) {
                return node;
            }
        }
        if (elseNode != null) {
            Node node = elseNode.dfsSearchByLine(line, false);
            if (node != null) {
                return node;
            }
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
}