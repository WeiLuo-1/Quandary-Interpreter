package ast;

public class IdExpr extends Expr{
    final String id;

    public IdExpr(String id, Location loc) {
        super(loc);
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return id.toString();
    }
}
