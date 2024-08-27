package ast;

public class QInt extends QVal{
    final long val;

    public QInt(long val) {
        this.val = val;
    }

    public long getVal() {
        return val;
    }

    @Override
    public String toString() {
        return val + "";
    }
}
