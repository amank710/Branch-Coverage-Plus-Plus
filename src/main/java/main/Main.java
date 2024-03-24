package main;
import parser.VariableMapBuilder;
import graph.Node;

public class Main {
    public static void main(String[] args) {
        VariableMapBuilder variableMapBuilder = new VariableMapBuilder("src/main/java/source", "SourceCodeBoolean.java");
        Node root = variableMapBuilder.build();
        root.visualize();

        int line = 19;
        System.out.println(line);
        System.out.println(root.getStateFromLine(line)); // Should print the state of the variables at line 12
//        System.out.println(root.getDependenciesFromLine(line));
//        System.out.println(root.getDependenciesFromLine(20)); // Should print the dependencies of the variables at line 15
    }
}