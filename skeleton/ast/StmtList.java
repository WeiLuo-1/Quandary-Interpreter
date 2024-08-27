package ast;

public class StmtList extends Stmt{
    final Stmt stmt;
    final StmtList stmtList;

    public StmtList(Stmt stmt, StmtList stmtList, Location loc) {
        super(loc);
        this.stmt = stmt;
        this.stmtList = stmtList;
    }

    public Stmt getStmt() {
        return stmt;
    }

    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public String toString() {
        String s = null;
        if (stmtList.equals(null)) {
            s = "";
        } else if (stmt.equals(null)) {
            s = "{\n" + stmtList.toString() + "\n}";
        } else {
            s = stmt + "\n" + stmtList.toString();
        }
        return s;
    }
}
