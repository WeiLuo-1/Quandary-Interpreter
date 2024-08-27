package ast;

public class QRef extends QVal{
    public QObj referent;

    public QRef(QObj referent) {
        this.referent = referent;
    }

    public QObj getReferent() {
        return referent;
    }

    @Override
    public String toString() {
        if (referent == null) {
            return "nil";
        } else if (referent.getLeft() == null && referent.getRight() == null) {
            return "nil";
        } else if (referent.getLeft() == null) {
            return "(nil" + " . " + referent.getRight().toString() + ")";
        } else if (referent.getRight() == null) {
            return "(" + referent.getLeft().toString() + " . " + "nil" + ")";
        }
        return "(" + referent.toString() + ")";
    }
}
