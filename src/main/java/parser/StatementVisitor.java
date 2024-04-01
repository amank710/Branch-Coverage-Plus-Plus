package parser;

import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import graph.Node;
import common.functions.Path;

import java.util.ArrayList;
import java.util.List;

public class StatementVisitor extends VoidVisitorAdapter<Node> {

    private Path path;

    private List<Integer> lines;

    public StatementVisitor() {
        this.lines = new ArrayList<>();
    }

    private boolean isReturn = false;

    public boolean isReturn() {
        return isReturn;
    }
    @Override
    public void visit(BlockStmt n, Node arg) {
        n.getStatements().forEach(stmt -> {
//            System.out.println(stmt.getBegin().get().line);
            if(!stmt.isIfStmt()) {
//                System.out.println(stmt.getBegin().get().line);
                if (stmt.isReturnStmt()) {
                    isReturn = true;
                    setPath(stmt.getBegin().get().line);
                }

                if(!isReturn) {
                    setPath(stmt.getBegin().get().line);
                }
            }
        });

//        System.out.println(n.getStatements());
    }

    public List<Integer> getPath() {
        return lines;
    }
    public void setPath(int line) {
        lines.add(line);
    }

    public Integer getSize() {
        return lines.size();
    }
}
