package graph;

import java.util.*;

public class StateNode extends Node{
    private Map<String, Set<Integer>> state;
    private List<Set<Integer>> dependencies;
    private Node child;

    private int lineNum;

    public StateNode(){
        super();
    }

    public StateNode(Map<String, Set<Integer>> prevState, List<Set<Integer>> dependencies, int lineNum) {
        super(prevState, dependencies, lineNum);
    }

    public Map<String, Set<Integer>> getState() {
        return state;
    }

    public void setState(Map<String, Set<Integer>> state) {
        this.state = state;
    }

    public List<Set<Integer>> getDependencies() {
        return dependencies;
    }

    public void setDependencies(List<Set<Integer>> dependencies) {
        this.dependencies = dependencies;
    }

    public int getLineNumber() {
        return lineNum;
    }

    void setLineNumber(int lineNum) {
        this.lineNum = lineNum;
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