package main;
import parser.VariableMapBuilder;
import graph.Node;

public class Main {
    public static void main(String[] args) {
        VariableMapBuilder variableMapBuilder = new VariableMapBuilder("src/main/java/source", "SourceCode.java");
        Node root = variableMapBuilder.build();
//        System.out.println("BUild");
//        root.visualize();
        System.out.println("Build");
        System.out.println(variableMapBuilder.getPath());

//        System.out.println("Visualize");
//
//        int line = 7;
//        System.out.println(line);
//        System.out.println(root.getStateFromLine(line)); // Should print the state of the variables at line


////        System.out.println(root.getDependenciesFromLine(line));
////        System.out.println(root.getDependenciesFromLine(20)); // Should print the dependencies of the variables at line 15
    }
}
//get line number for all if statement block