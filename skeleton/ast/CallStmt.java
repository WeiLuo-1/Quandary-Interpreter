package ast;

public class CallStmt extends Stmt{
    final String id;
    final ExprList exprList;

    public CallStmt(String id, ExprList exprList, Location loc) {
        super(loc);
        this.id = id;
        this.exprList = exprList;
    }

    public String getId() {
        return id;
    }

    public ExprList getExprList() {
        return exprList;
    }

    @Override
    public String toString() {
        return id + "(" + exprList.toString() + ");";
    }
}