package ast;

public class FuncDef extends ASTNode {
    final VarDecl varDecl;
    final FormalDeclList formalDeclList;
    final StmtList stmtList;

    public FuncDef(VarDecl varDecl, FormalDeclList formalDeclList, StmtList stmtList, Location loc) {
        super(loc);
        this.varDecl = varDecl;
        this.formalDeclList = formalDeclList;
        this.stmtList = stmtList;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public FormalDeclList getFormalDeclList() {
        return formalDeclList;
    }

    public StmtList getStmtList() {
        return stmtList;
    }

    @Override
    public String toString() {
        return varDecl.toString() + " (" + formalDeclList.toString() + ") {" + stmtList.toString() + "}";
    }
}
