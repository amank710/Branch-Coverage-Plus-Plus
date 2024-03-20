package main;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Paths;
import java.util.List;

import graph.CodeBlockNode;
import graph.ControlFlowGraph;
import parser.StatementVisitor;

public class Main {
    public static void main(String[] args) {
        try{
            String sourcePath = "src/main/java/source";
            SourceRoot sr = new SourceRoot(Paths.get(sourcePath));
            sr.tryToParse();
            analyze(sr);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public static void analyze(SourceRoot sourceRoot) {
        List< CompilationUnit > cus = sourceRoot.getCompilationUnits();
        CodeBlockNode root = new CodeBlockNode(null);
        for (CompilationUnit cu : cus) {
            StatementVisitor statementVisitor = new StatementVisitor();
            statementVisitor.visit(cu, root);
        }
        root.visualizer(0);
    }
}
