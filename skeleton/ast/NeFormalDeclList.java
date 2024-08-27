package ast;

public class NeFormalDeclList extends ASTNode{
    final VarDecl varDecl;
    final NeFormalDeclList neFormalDeclList;

    public NeFormalDeclList(VarDecl varDecl, NeFormalDeclList neFormalDeclList, Location loc) {
        super(loc);
        this.varDecl = varDecl;
        this.neFormalDeclList = neFormalDeclList;
    }

    public VarDecl getVarDecl() {
        return varDecl;
    }

    public NeFormalDeclList getNeFormalDeclList() {
        return neFormalDeclList;
    }

    @Override
    public String toString() {
        if (neFormalDeclList.equals(null)) {
            return varDecl.toString();
        }
        return varDecl.toString() + ", " + neFormalDeclList.toString();
    }
}
