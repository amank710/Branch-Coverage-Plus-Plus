package parser;

import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.resolution.UnsolvedSymbolException;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration;
import graph.CodeBlockNode;
import graph.ConditionalNode;
import graph.MethodDeclarationNode;

public class StatementVisitor extends VoidVisitorAdapter<CodeBlockNode> {

    public CodeBlockNode analyze(MethodDeclaration method, CodeBlockNode root) {
        // Visit the method and its body
        StatementVisitor statementVisitor = new StatementVisitor();
        statementVisitor.visit(method, root);
        return root;
    }

    public void visit(IfStmt n, CodeBlockNode root) {
        // Create a conditional node
        Expression condition = n.getCondition();
        ConditionalNode conditionalNode = new ConditionalNode(condition, null, null);
        root.appendNeighbors(conditionalNode); // Append the conditional node to the root

        // Visit the then and else branches
        Statement then = n.getThenStmt();
        CodeBlockNode thenBranch = new CodeBlockNode(then);
        conditionalNode.setTrueBranch(thenBranch); // Set the true branch of the conditional node

        // Visit the then branch
        then.accept(this, thenBranch);

        // Visit the else branch if it exists
        n.getElseStmt().ifPresent(elseStmt -> {
            CodeBlockNode elseBranch = new CodeBlockNode(elseStmt);
            conditionalNode.setFalseBranch(elseBranch);
            elseStmt.accept(this, elseBranch);
        });
    }

    @Override
    public void visit(MethodDeclaration n, CodeBlockNode root) {
        // Create a method declaration node
        MethodDeclarationNode methodNode = new MethodDeclarationNode(n);
        root.appendNeighbors(methodNode);
        CodeBlockNode methodBody = new CodeBlockNode(n.getBody().get());
        methodNode.setBody(methodBody);
        // Visit the method body
        n.getBody().ifPresent(body -> body.accept(this, methodBody));
    }

}