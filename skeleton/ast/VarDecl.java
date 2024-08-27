package ast;

public class VarDecl extends ASTNode {
    public static final boolean ISMUTABLE = true;
    public static final boolean NOTMUTABLE = false;

    final Type type;
    final String id;
    final boolean isMutable;

    public VarDecl(Type type, String id, boolean isMutable, Location loc) {
        super(loc);
        this.type = type;
        this.id = id;
        this.isMutable = isMutable;
    }

    public Type getType() {
        return type;
    }

    public String getId() {
        return id;
    }

    public boolean isMutable() {
        return isMutable;
    }

    @Override
    public String toString() {
        return type + " " + id;
    }
}
