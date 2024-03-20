package main;
import com.github.javaparser.ParserConfiguration;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.symbolsolver.JavaSymbolSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver;
import com.github.javaparser.utils.SourceRoot;
import java.nio.file.Paths;
import graph.CodeBlockNode;
import parser.StatementVisitor;

public class Main {
    public static void main(String[] args) {
        try {
            // Set up a combined type solver (this is used for symbol resolution in the JavaParser library)
            String sourcePath = "src/main/java/source";
            CombinedTypeSolver combinedSolver = new CombinedTypeSolver(
                    new ReflectionTypeSolver(),
                    new JavaParserTypeSolver(Paths.get(sourcePath))
            );

            // Set up a parser configuration with the combined type solver
            ParserConfiguration parserConfiguration = new ParserConfiguration()
                    .setSymbolResolver(new JavaSymbolSolver(combinedSolver));

            // Parse the source code
            SourceRoot sr = new SourceRoot(Paths.get(sourcePath), parserConfiguration);
            CompilationUnit cu = sr.parse("", "SourceCode.java");

            // Analyze the source code
            cu.getClassByName("SourceCode").ifPresent(classDeclaration -> {
                // Get the method (checkMultiples in this case) and analyze it
                MethodDeclaration method = classDeclaration.getMethodsByName("checkMultiples").get(0);
                // Create a visitor and analyze the method
                StatementVisitor statementVisitor = new StatementVisitor();
                CodeBlockNode block = statementVisitor.analyze(method, new CodeBlockNode());
                // Visualize the control flow graph?
                block.visualizer(0);
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}