package ast;

import java.util.Map;

import interpreter.Interpreter;

public class MyThread extends Thread{
    final Expr expr;
    QVal val;
    final Map<String, QVal> map;
    final boolean[] isRet;
    public MyThread(Expr expr, Map<String, QVal> map, boolean[] isRet) {
        this.expr = expr;
        this.map = map;
        this.isRet = isRet;
    }
    @Override
    public void run() {
        Interpreter interpreter = Interpreter.getInterpreter();
        val = interpreter.evaluate(expr, map, isRet);
    }

    public QVal getVal() {
        return val;
    }
}
