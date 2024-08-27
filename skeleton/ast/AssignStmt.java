package ast;

public class AssignStmt extends Stmt{
    final String id;
    final Expr expr;

    public AssignStmt(String id, Expr expr, Location loc) {
        super(loc);
        this.id = id;
        this.expr = expr;
    }

    public String getId() {
        return id;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return id + " = " + expr + ";";
    }
}
