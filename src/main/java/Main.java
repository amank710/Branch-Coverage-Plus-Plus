import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.utils.SourceRoot;
import com.github.javaparser.ast.CompilationUnit;
import java.nio.file.Paths;
import java.util.List;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;

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
        System.out.println("aa");
        for (CompilationUnit cu : cus) {
            StatementVisitor statementVisitor = new StatementVisitor();
            statementVisitor.visit(cu, null);
        }
    }
    private static class StatementVisitor extends VoidVisitorAdapter<Void> {
        @Override
        public void visit(IfStmt n, Void arg) {
            System.out.println("Found an if statement at " + n.getBegin().get().line);
            super.visit(n, arg);
        }
    }
}
