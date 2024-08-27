package ast;

public abstract class QVal {
    @Override
    public String toString() {
        if (this instanceof QRef) {
            return ((QRef)this).toString();
        } else {
            return ((QInt)this).toString();
        }
    }
}
