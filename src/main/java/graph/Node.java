package graph;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.HashMap;
import java.util.HashSet;

// Abstract class: Node
// This class represents a node that tracks the current line number, variables, and their dependencies up to that line in code.
// Example 1:
// Given the code:
// x = 1
// y = x + 1
// The representation would be:
// node1 ({x=[1]}) -> node2 ({x=[1], y=[2]})
// Example 2:
// Given the code:
// x = 2
// if (x > 0) {
//     y = 3
// } else {
//     z = 4
// }
// x = y
// The representation would detail how variables and their dependencies are tracked through conditions.
// node1 (variables: {x=[2]}) -> conditionNode (condition: x > 0) -> thenNode (variables: {x=[2], y=[3]})
//                                                                -> elseNode (variables: {x=[2], z=[4]})
//                                                                -> node2 (variables: {x=[2, 3], y=[3], z=[4]})
abstract public class Node {

    abstract public Map<String, Set<Integer>> getState();

    abstract public void setChild(Node child);

    abstract public Node getChild();

    abstract public int getLineNumber();

    public Map<String, Set<Integer>> getStateFromLine(int line) {
        Node child = getChild();
//        System.out.println("State: " + getState() + " getLineNumber: " + getLineNumber());
        if (line == getLineNumber()) {
            return getState();
        } else if (child != null) {
            Map<String, Set<Integer>> childState = child.getStateFromLine(line);
            if (childState != null) {
                return childState;
            } else {
                return child.getStateFromLine(line - 1);
            }
        }
        return null;
    }

    abstract public void visualize();

    public void visualize(int depth) {
        String indentation = createIndentation(depth);
        System.out.println(indentation  + formatState(this.getState()));
        if (this.getChild() != null) {
            this.getChild().visualize(depth );
        }
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


    protected String formatState(Map<String, Set<Integer>> state) {
        return getLineNumber() + ": " + state.entrySet().stream()
                .map(entry -> entry.getKey() + "=" + entry.getValue())
                .collect(Collectors.joining(", ", "{", "}"));
    }

    protected String createIndentation(int depth) {
        return  " ".repeat(depth * 2);
    }

}