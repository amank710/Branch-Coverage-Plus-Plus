package main;
import parser.VariableMapBuilder;
import graph.Node;

public class Main {
    public static void main(String[] args) {
        VariableMapBuilder variableMapBuilder = new VariableMapBuilder("src/main/java/source", "SourceCode.java");
        Node root = variableMapBuilder.build();
        root.visualize();
//        System.out.println(root.getStateFromLine(12)); // Should print the state of the variables at line 12
    }
}