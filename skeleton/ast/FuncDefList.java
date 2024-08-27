package ast;

public class FuncDefList extends ASTNode {
    final FuncDef funcDef;
    final FuncDefList funcDefList;

    public FuncDefList(FuncDef funcDef, FuncDefList funcDefList, Location loc) {
        super(loc);
        this.funcDef = funcDef;
        this.funcDefList = funcDefList;
    }

    public FuncDef getFuncDef() {
        return funcDef;
    }

    public FuncDefList getFuncDefList() {
        return funcDefList;
    }

    @Override
    public String toString() {
        return funcDef + "\n" + funcDefList.toString();
    }
}
