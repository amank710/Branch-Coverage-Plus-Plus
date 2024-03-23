package parser;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.utils.SourceRoot;
import graph.StateNode;
import graph.Node;

import java.nio.file.Paths;


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
            VariableVisitor variableVisitor = new VariableVisitor(this.variableMapRoot);
            cu.accept(variableVisitor, null);
            return this.variableMapRoot;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}