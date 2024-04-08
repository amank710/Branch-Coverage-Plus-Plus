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

    private String methodName;

    private Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths;

    public VariableMapBuilder(String sourcePath, String fileName, String methodName) {
        this.sourceRoot = new SourceRoot(Paths.get(sourcePath));
        this.variableMapRoot = new StateNode();
        this.fileName = fileName;
        this.methodName = methodName;
    }
    public Node build() {
        try {
            CompilationUnit cu = sourceRoot.parse("", fileName);
            Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths = new Stack<>();
            VariableVisitor variableVisitor = new VariableVisitor(this.variableMapRoot, paths, this.methodName);
            cu.accept(variableVisitor, null);


            List<Integer> lines = variableVisitor.getReturnLines();

            System.out.println(paths);

            Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> newPaths = new Stack<>();
            // sort the paths, remove the paths that contain the return line
            for (int i = 0; i < paths.size(); i++) {
                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> map = paths.get(i);
                Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> newMap = new HashMap<>();
                for (Map.Entry<ArrayList<Integer>, ArrayList<ArrayList<Integer>>> entry : map.entrySet()) {
                    ArrayList<Integer> key = entry.getKey();
                    ArrayList<ArrayList<Integer>> value = entry.getValue();
                    ArrayList<Integer> newKey = new ArrayList<>();
                    ArrayList<ArrayList<Integer>> newValue = new ArrayList<>();
                    for (int j = 0; j < key.size(); j++) {
                        newKey.add(key.get(j));
                    }
                    for (int j = 0; j < value.size(); j++) {
                        ArrayList<Integer> newPath = new ArrayList<>();
                        value.get(j).sort(Comparator.naturalOrder());

                        for (int k = 0; k < value.get(j).size(); k++) {
                            newPath.add(value.get(j).get(k));
                            if(lines.contains(value.get(j).get(k))) {
                                System.out.println("found");
                                break;
                            }
                        }
                        newValue.add(newPath);
                    }
                    newMap.put(newKey, newValue);
                }
                newPaths.push(newMap);
            }
            setPath(newPaths);
//            System.out.println("expected");
//            System.out.println("[{[16, 44]=[[17, 43, 19, 21, 23], [17, 43, 19, 21, 25], [17, 43, 19, 38, 34, 30], [17, 43, 19, 38, 36], [17, 43, 41]]}, {[44, 56]=[[45, 47], [45, 49, 51], [45, 49, 53]]}]");
//            System.out.println(paths.toString().equals("[{[16, 44]=[[17, 43, 19, 21, 23], [17, 43, 19, 21, 25], [17, 43, 19, 38, 34, 30], [17, 43, 19, 38, 36], [17, 43, 41]]}, {[44, 56]=[[45, 47], [45, 49, 51], [45, 49, 53]]}]"));

            return this.variableMapRoot;
        } catch (Exception e) {
            System.out.println("[VariableMapBuilder] Error: " + e.getMessage());
            //e.printStackTrace();
        }
        return null;
    }

    public void setPath(Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> paths) {
        this.paths = paths;
    }

    public Stack<Map<ArrayList<Integer>, ArrayList<ArrayList<Integer>>>> getPath() {
        return this.paths;
    }

}
