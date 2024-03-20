package parser;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import graph.CodeBlockNode;
import graph.ConditionalNode;
import graph.ControlFlowGraph;

public class StatementVisitor extends VoidVisitorAdapter<CodeBlockNode> {
    @Override
    public void visit(IfStmt n, CodeBlockNode root) {
        Expression expression = n.getCondition();
        ConditionalNode conditionalNode = new ConditionalNode(expression, null, null, n.getBegin().get().line);
        Statement thenStmt = n.getThenStmt();
        CodeBlockNode controlFlowGraph = new CodeBlockNode(thenStmt);
        conditionalNode.setTrueBranch(controlFlowGraph);
        Statement elseStmt = n.getElseStmt().orElse(null);
        if (elseStmt != null) {
            CodeBlockNode elseControlFlowGraph = new CodeBlockNode(elseStmt);
            conditionalNode.setFalseBranch(elseControlFlowGraph);
        }
        root.appendNeighbors(conditionalNode);
        super.visit(n, root);
    }

//    @Override
//    public void visit(MethodDeclaration n, CodeBlockNode currentParent) {
//        CodeBlockNode methodNode = new CodeBlockNode(n);
//        currentParent.appendNeighbors(currentParent);
//
//        super.visit(n, methodNode);
//    }
}
