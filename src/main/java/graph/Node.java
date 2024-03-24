package graph;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

// Abstract class: Node
// This class represents a node that tracks the current line number, variables, and their dependencies up to that line in code.
abstract public class Node {

    Map<String, Set<Integer>> state;
    List<Set<Integer>> dependencies;
    int lineNum;

    public Node() {
        this.setState(new HashMap<>());
        this.setDependencies(new ArrayList<>());
        this.setLineNumber(0);
    }

    public Node(Map<String, Set<Integer>> state, List<Set<Integer>> dependencies, int lineNum) {
        this.setState(state != null ? new HashMap<>(state) : new HashMap<>());
        this.setDependencies(dependencies != null ? new ArrayList<>(dependencies) : new ArrayList<>());
        this.setLineNumber(lineNum);
    }

    abstract public Map<String, Set<Integer>> getState();

    abstract public void setState(Map<String, Set<Integer>> state);

    abstract public List<Set<Integer>> getDependencies();

    abstract public void setDependencies(List<Set<Integer>> dependencies);

    abstract public void setChild(Node child);

    abstract public Node getChild();

    abstract public int getLineNumber();

    public void setLineNumber(int lineNum) {
        this.lineNum = lineNum;
    }

    public Map<String, Set<Integer>> getStateFromLine(int line) {

        return searchByLine(line, true).getState();
    }

    public List<Set<Integer>> getDependenciesFromLine(int line) {

        return searchByLine(line, true).getDependencies();
    }

    protected Node searchByLine(int line, boolean decrement) {
        if (line == getLineNumber()) {
            System.out.println("Found line " + line + " in " + this.getClass().getSimpleName());
            return this;
        } else {
            if (getChild() != null) {
                Node node =  getChild().searchByLine(line, false);
                if (node != null) {
                    return node;
                }
            }
        }
        if (decrement) {
            return searchByLine(line - 1, true);
        }
        return null;

    }

    public Map<String, Set<Integer>> mergeStates(Map<String, Set<Integer>> state) {
        Map<String, Set<Integer>> mergedState = new HashMap<>();
        // Combine keys from both states
        Set<String> allKeys = new HashSet<>(state.keySet());
        allKeys.addAll(this.getState().keySet());

        for (String key : allKeys) {
            Set<Integer> mergedLines = new HashSet<>();
            // If the key exists in state1, add all its lines to mergedLines
            if (state.containsKey(key)) {
                mergedLines.addAll(state.get(key));
            }
            // If the key exists in state2, add all its lines to mergedLines
            if (this.getState().containsKey(key)) {
                mergedLines.addAll(this.getState().get(key));
            }
            // Put the mergedLines set in the mergedState map
            mergedState.put(key, mergedLines);
        }
        return mergedState;
    }
    abstract public void visualize();

    public void visualize(int depth) {
        String indentation = createIndentation(depth);
        System.out.println(indentation  + formatState(this.getState()) + " " + formatDependencies(this.getDependencies()));
        if (this.getChild() != null) {
            this.getChild().visualize(depth );
        }
    }

    protected String formatState(Map<String, Set<Integer>> state) {
        return getLineNumber() + ": M = " + state.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    protected String formatDependencies(List<Set<Integer>> dependencies) {
        return ", L = " + dependencies.stream()
                .map(set -> set.stream().map(Object::toString).collect(Collectors.joining(", ", "{", "}")))
                .collect(Collectors.joining(", ", "[", "]"));
    }

    protected String createIndentation(int depth) {
        return  " ".repeat(depth * 2);
    }

}