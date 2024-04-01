package parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import common.functions.Path;
import graph.StateNode;
import graph.Node;

import java.nio.file.Paths;
import java.util.*;


public class VariableMapBuilder {
    private SourceRoot sourceRoot;
    private Node variableMapRoot;
    private String fileName;

    public VariableMapBuilder(String sourcePath, String fileName) {
        this.sourceRoot = new SourceRoot(Paths.get(sourcePath));
        this.variableMapRoot = new StateNode();
        this.fileName = fileName;
    }
    public Node build() {
        try {
            CompilationUnit cu = sourceRoot.parse("", fileName);
            Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths = new Stack<>();
            VariableVisitor variableVisitor = new VariableVisitor(this.variableMapRoot, paths);
            cu.accept(variableVisitor, null);
            System.out.println("actual");
            System.out.println(paths);
            System.out.println("expected");
            System.out.println("[{[16, 44]=[[17, 43, 19, 21, 23], [17, 43, 19, 21, 25], [17, 43, 19, 38, 34, 30], [17, 43, 19, 38, 36], [17, 43, 41]]}, {[44, 56]=[[45, 47], [45, 49, 51], [45, 49, 53]]}]");
            System.out.println(paths.toString().equals("[{[16, 44]=[[17, 43, 19, 21, 23], [17, 43, 19, 21, 25], [17, 43, 19, 38, 34, 30], [17, 43, 19, 38, 36], [17, 43, 41]]}, {[44, 56]=[[45, 47], [45, 49, 51], [45, 49, 53]]}]"));

            return this.variableMapRoot;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }



    public void printFlattenPath (Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths) {
        Set<Integer> flattenPath = new HashSet<>();
        boolean isIf = true;
        int indent = 0;
        for (Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> path : paths) {
            for (Map.Entry<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> entry : path.entrySet()) {
                ArrayList<ArrayList<Integer>> value = entry.getValue();
                System.out.println(entry.getKey());
                for (ArrayList<Integer> line : value) {
                    if(isIf) {
                        System.out.print(" ".repeat(indent));
                        System.out.println("If: " + line);
                        isIf = false;
                    } else {
                        System.out.print(" ".repeat(indent));
                        System.out.println("Else: " + line);
                        isIf = true;
                    }
                    indent += 4;
                }
            }
        }
    }
}