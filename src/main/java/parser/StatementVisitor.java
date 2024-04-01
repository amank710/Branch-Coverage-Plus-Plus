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
    @Override
    public void visit(BlockStmt n, Node arg) {
        n.getStatements().forEach(stmt -> {
            if(!stmt.isIfStmt()) {
//                System.out.println(stmt.getBegin().get().line);
                setPath(stmt.getBegin().get().line);
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
