package ast;

import java.util.concurrent.atomic.AtomicBoolean;

public class QObj {
    public AtomicBoolean isLocked = new AtomicBoolean(false);
    public QVal left;
    public QVal right;

    public QObj(QVal left, QVal right) {
        this.left = left;
        this.right = right;
    }

    public QVal getLeft() {
        return left;
    }

    public QVal getRight() {
        return right;
    }

    public boolean lock() {
        return isLocked.compareAndSet(false, true);
    }

    public boolean unlock() {
        return isLocked.compareAndSet(true, false);
    }

    @Override
    public String toString() {
        return left.toString() + " . " + right.toString();
    }
}
