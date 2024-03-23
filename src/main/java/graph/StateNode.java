package graph;

import java.util.*;

public class StateNode extends Node{
    private Map<String, Set<Integer>> state;
    private Node child;

    private int lineNum;

    public StateNode(Map<String, Set<Integer>> prevState, int lineNum) {
        this.state = prevState != null ? new HashMap<>(prevState) : new HashMap<>();
        this.lineNum = lineNum;
    }

    public Map<String, Set<Integer>> getState() {
        return state;
    }

    public int getLineNumber() {
        return lineNum;
    }

    public void setChild(Node child) {
        this.child = child;
    }

    public Node getChild() {
        return child;
    }

    public void visualize() {
        visualize(0);
    }

}