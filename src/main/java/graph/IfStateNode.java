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

        if (this.getThenNode() != null) {
            System.out.println(indentation + this.getThenNode().getLineNumber() + ": Then");
            this.getThenNode().visualize(depth + 1);
        } else {
            System.out.println(indentation  + "Then is unsatisfiable" );
        }

        // If there's an 'else' branch, visualize it as well
        if (this.getElseNode() != null) {
            System.out.println(indentation + this.getElseNode().getLineNumber() + ": Else");
            this.getElseNode().visualize(depth + 1);
        } else {
            System.out.println(indentation + "Else is unsatisfiable");
        }

        if (this.getChild() != null) {
            this.getChild().visualize(depth);
        }
    }
}