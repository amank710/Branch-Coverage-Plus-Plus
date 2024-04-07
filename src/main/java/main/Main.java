package main;
import parser.VariableMapBuilder;
import graph.Node;

import java.util.*;

public class Main {
    public static void main(String[] args) {
        VariableMapBuilder variableMapBuilder = new VariableMapBuilder("resources/test_classes/", "Demo.java");
        Node root = variableMapBuilder.build();
//        System.out.println("BUild");
//        root.visualize();
//        System.out.println("Build");
        System.out.println("Path");
        System.out.println(variableMapBuilder.getPath());
        for (int i = 0; i <variableMapBuilder.getPath().size(); i++) {
            System.out.println("If block: " + variableMapBuilder.getPath().get(i).keySet() + ":");
            Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> map = variableMapBuilder.getPath().get(i);
            ArrayList<ArrayList<Integer>> value = map.get(map.keySet().toArray()[0]);
            for (int j = 0; j < value.size(); j++) {
                System.out.println("Path(" + j + "): " + Arrays.toString(Arrays.stream(value.get(j).toArray()).sorted().toArray(Object[]::new)));
            }
        }

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