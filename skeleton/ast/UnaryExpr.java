package ast;

public class UnaryExpr extends Expr {

    final Expr expr;

    public UnaryExpr(Expr expr, Location loc) {
        super(loc);
        this.expr = expr;
    }

    public Expr getExpr() {
        return expr;
    }

    @Override
    public String toString() {
        return "-(" + expr + ")";
    }
}
