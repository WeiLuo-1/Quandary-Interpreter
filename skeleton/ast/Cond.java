package ast;

public class Cond extends Expr{
    public static final int LESSEQUAL = 1;
    public static final int GREATEREQUAL = 2;
    public static final int EQUAL = 3;
    public static final int NOTEQUAL = 4;
    public static final int LESS = 5;
    public static final int GREATER = 6;
    public static final int AND = 7;
    public static final int OR = 8;
    public static final int NOT = 9;

    final Expr expr1;
    final Expr expr2;
    final int operator;
    
    public Cond(Expr expr1, int operator, Expr expr2, Location loc) {
        super(loc);
        this.expr1 = expr1;
        this.expr2 = expr2;
        this.operator = operator;
    }

    public Expr getExpr1() {
        return expr1;
    }

    public Expr getExpr2() {
        return expr2;
    }

    public int getOperator() {
        return operator;
    }

    @Override
    public String toString() {
        String s = null;
        switch (operator) {
            case LESSEQUAL: s = "<="; break;
            case GREATEREQUAL: s = ">="; break;
            case EQUAL: s = "=="; break;
            case NOTEQUAL: s = "!="; break;
            case LESS: s = "<"; break;
            case GREATER: s = ">"; break;
            case AND: s = "&&"; break;
            case OR: s = "||"; break;
            case NOT: s = "!"; break;
        }
        if (operator == NOT) {
            return "(" + s + " " + expr2 + ")";
        }
        return "(" + expr1 + " " + s + " " + expr2 + ")";
    }
}
