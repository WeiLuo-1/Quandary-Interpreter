package ast;

public class BlockStmt extends Stmt{
    final StmtList stmtList;

    public BlockStmt(StmtList stmtList, Location loc) {
        super(loc);
        this.stmtList = stmtList;
    }

    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public String toString() {
        return "{" + stmtList.toString() + "}";
    }
}
