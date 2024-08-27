package interpreter;

import java.io.*;
import java.util.Random;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import parser.ParserWrapper;
import ast.*;

public class Interpreter {

    // Process return codes
    public static final int EXIT_SUCCESS = 0;
    public static final int EXIT_PARSING_ERROR = 1;
    public static final int EXIT_STATIC_CHECKING_ERROR = 2;
    public static final int EXIT_DYNAMIC_TYPE_ERROR = 3;
    public static final int EXIT_NIL_REF_ERROR = 4;
    public static final int EXIT_QUANDARY_HEAP_OUT_OF_MEMORY_ERROR = 5;
    public static final int EXIT_DATA_RACE_ERROR = 6;
    public static final int EXIT_NONDETERMINISM_ERROR = 7;

    static private Interpreter interpreter;
    //static private Boolean retFlag = false;
    //static private Boolean isRet = false;

    //static private Map<String, Long> map = new HashMap<>();
    static private Map<String, FuncDef> funcDefMap = new HashMap<>();

    public static Interpreter getInterpreter() {
        return interpreter;
    }

    public static void main(String[] args) {
        String gcType = "NoGC"; // default for skeleton, which only supports NoGC
        long heapBytes = 1 << 14;
        int i = 0;
        String filename;
        long quandaryArg;
        try {
            for (; i < args.length; i++) {
                String arg = args[i];
                if (arg.startsWith("-")) {
                    if (arg.equals("-gc")) {
                        gcType = args[i + 1];
                        i++;
                    } else if (arg.equals("-heapsize")) {
                        heapBytes = Long.valueOf(args[i + 1]);
                        i++;
                    } else {
                        throw new RuntimeException("Unexpected option " + arg);
                    }
                } else {
                    if (i != args.length - 2) {
                        throw new RuntimeException("Unexpected number of arguments");
                    }
                    break;
                }
            }
            filename = args[i];
            quandaryArg = Long.valueOf(args[i + 1]);
        } catch (Exception ex) {
            System.out.println("Expected format: quandary [OPTIONS] QUANDARY_PROGRAM_FILE INTEGER_ARGUMENT");
            System.out.println("Options:");
            System.out.println("  -gc (MarkSweep|Explicit|NoGC)");
            System.out.println("  -heapsize BYTES");
            System.out.println("BYTES must be a multiple of the word size (8)");
            return;
        }

        Program astRoot = null;
        Reader reader;
        try {
            reader = new BufferedReader(new FileReader(filename));
        } catch (IOException ex) {
            throw new RuntimeException(ex);
        }
        try {
            astRoot = ParserWrapper.parse(reader);
        } catch (Exception ex) {
            ex.printStackTrace();
            Interpreter.fatalError("Uncaught parsing error: " + ex, Interpreter.EXIT_PARSING_ERROR);
        }
        //astRoot.println(System.out);
        interpreter = new Interpreter(astRoot);
        interpreter.initMemoryManager(gcType, heapBytes);
        String returnValueAsString = interpreter.executeRoot(astRoot, quandaryArg).toString();
        System.out.println("Interpreter returned " + returnValueAsString);
    }

    final Program astRoot;
    final Random random;

    private Interpreter(Program astRoot) {
        this.astRoot = astRoot;
        this.random = new Random();
    }

    void initMemoryManager(String gcType, long heapBytes) {
        if (gcType.equals("Explicit")) {
            throw new RuntimeException("Explicit not implemented");            
        } else if (gcType.equals("MarkSweep")) {
            throw new RuntimeException("MarkSweep not implemented");            
        } else if (gcType.equals("RefCount")) {
            throw new RuntimeException("RefCount not implemented");            
        } else if (gcType.equals("NoGC")) {
            // Nothing to do
        }
    }

    QVal executeRoot(Program astRoot, long arg) {
        return evaluate(astRoot.getFuncDefList(), arg);
    }

    QVal evaluate(FuncDefList funcDefList, long arg) {
        ArrayList<QVal> args = new ArrayList<>();
        QInt cpArg = new QInt(arg);
        args.add(cpArg);
        FuncDef mainFunc = funcDefList.getFuncDef();
        String id = funcDefList.getFuncDef().getVarDecl().getId();
        if (id.equals("main")) {
            mainFunc = funcDefList.getFuncDef();
        }
        funcDefMap.put(id, funcDefList.getFuncDef());
        while (funcDefList.getFuncDefList() != null) {
            funcDefList = funcDefList.getFuncDefList();
            id = funcDefList.getFuncDef().getVarDecl().getId();
            if (id.equals("main")) {
                mainFunc = funcDefList.getFuncDef();
            }
            funcDefMap.put(id, funcDefList.getFuncDef());
        }
        if (mainFunc.getVarDecl().getId().equals("main")) {
            Map<String, QVal> map = new HashMap<>();
            boolean[] isRet = {false};
            return evaluate(mainFunc, args, map, isRet);
        } else {
            throw new RuntimeException("No main method found");
        }
        
    }

    QVal evaluate(FuncDef funcDef, ArrayList<QVal> args, Map<String, QVal> map, boolean[] isRet) {
        if (funcDef.getFormalDeclList() != null) {
            evaluate(funcDef.getFormalDeclList(), args, map);
        }
        return evaluate(funcDef.getStmtList(), map, isRet);
    }

    void evaluate(FormalDeclList formalDeclList, ArrayList<QVal> args, Map<String, QVal> map) {
        evaluate(formalDeclList.getNeFormalDeclList(), args, map);
    }

    void evaluate(NeFormalDeclList neFormalDeclList, ArrayList<QVal> args, Map<String, QVal> map) {
        int idx = 0;
        if (idx + 1 > args.size()) {
            throw new RuntimeException("Not parameter given");
        }
        map.put(neFormalDeclList.getVarDecl().getId(), args.get(idx));
        while (neFormalDeclList.getNeFormalDeclList() != null) {
            idx++;
            if (idx + 1 > args.size()) {
                throw new RuntimeException("Parameter not enough");
            }
            neFormalDeclList = neFormalDeclList.getNeFormalDeclList();
            map.put(neFormalDeclList.getVarDecl().getId(), args.get(idx));
        }
    }

    QVal evaluate(StmtList stmtList, Map<String, QVal> map, boolean[] isRet) {
        if (stmtList == null) return new QInt(0);
        QVal ret = evaluate(stmtList.getStmt(), map, isRet);
        if (isRet[0]) {
            //retFlag = false; 
            return ret;
        } else {
            while (stmtList.getStmtList() != null) {
                stmtList = stmtList.getStmtList();
                ret = evaluate(stmtList.getStmt(), map, isRet);
                if (isRet[0]) {
                    //retFlag = false;
                    return ret;
                }
            }
        }
        return ret;
    }

    QVal evaluate(Stmt stmt, Map<String, QVal> map, boolean[] isRet) {
        if (stmt instanceof IfStmt) {
            if (evaluate(((IfStmt)stmt).getCond(), map, isRet)) {
                QVal ret = evaluate(((IfStmt)stmt).getStmt(), map, isRet);
                return ret;
            }
            return new QInt(1);
        } else if (stmt instanceof IfElseStmt) {
            if (evaluate(((IfElseStmt)stmt).getCond(), map, isRet)) {
                QVal ret = evaluate(((IfElseStmt)stmt).getStmt1(), map, isRet);
                return ret;
            } else {
                QVal ret = evaluate(((IfElseStmt)stmt).getStmt2(), map, isRet);
                return ret;
            }
        } else if (stmt instanceof WhileStmt) {
            QVal ret = new QInt(0);
            while (evaluate(((WhileStmt)stmt).getCond(), map, isRet)) {
                ret = evaluate(((WhileStmt)stmt).getStmt(), map, isRet);
                if (isRet[0]) {
                    break;
                }
            }
            return ret;
        } else if (stmt instanceof PrintStmt) {
            //isRet = true;
            QVal ret = evaluate(((PrintStmt)stmt).getExpr(), map, isRet);
            //isRet = false;
            System.out.println(ret);
            return ret;
        } else if (stmt instanceof ReturnStmt) {
            QVal ret = evaluate(((ReturnStmt)stmt).getExpr(), map, isRet);
            //retFlag = true;
            isRet[0] = true;
            return ret;
        } else if (stmt instanceof DeclStmt) {
            map.put(((DeclStmt)stmt).getVarDecl().getId(), evaluate(((DeclStmt)stmt).getExpr(), map, isRet));
            return new QInt(1);
        } else if (stmt instanceof AssignStmt) {
            map.put(((AssignStmt)stmt).getId(), evaluate(((AssignStmt)stmt).getExpr(), map, isRet));
            return new QInt(1);
        } else if (stmt instanceof BlockStmt) {
            //retFlag = false;
            QVal ret = evaluate(((BlockStmt)stmt).getStmtList(), map, isRet);
            // boolean old = isRet;
            // isRet = false;
            //retFlag = true;
            // isRet = old;
            return ret;
        } else if (stmt instanceof CallStmt) {
            ArrayList<Expr> args = new ArrayList<>();
            if (((CallStmt)stmt).getExprList() != null) {
                NeExprList neExprList = ((CallStmt)stmt).getExprList().getNeExprList();
                args.add(neExprList.getExpr());
                while (neExprList.getNeExprList() != null) {
                    neExprList = neExprList.getNeExprList();
                    args.add(neExprList.getExpr());
                }
            }
            if (((CallStmt)stmt).getId().equals("setLeft")) {
                QRef r = (QRef)evaluate(((CallStmt)stmt).getExprList().getNeExprList().getExpr(), map, isRet);
                QVal value = (QVal)evaluate(((CallStmt)stmt).getExprList().getNeExprList().getNeExprList().getExpr(), map, isRet);
                r.referent.left = value;
                return new QInt(1);
            }
            if (((CallStmt)stmt).getId().equals("setRight")) {
                QRef r = (QRef)evaluate(((CallStmt)stmt).getExprList().getNeExprList().getExpr(), map, isRet);
                QVal value = (QVal)evaluate(((CallStmt)stmt).getExprList().getNeExprList().getNeExprList().getExpr(), map, isRet);
                r.referent.right = value;
                return new QInt(1);
            }
            if (((CallStmt)stmt).getId().equals("acq")) {
                while (!((QRef)evaluate(args.get(0), map, isRet)).referent.lock()) {

                }
                return new QInt(1);
            }
            if (((CallStmt)stmt).getId().equals("rel")) {
                ((QRef)evaluate(args.get(0), map, isRet)).referent.unlock();
                return new QInt(1);
            }
            Map<String, QVal> tempMap = new HashMap<>(map);
            boolean[] tempIsRet = {false};
            ArrayList<QVal> qArgs = new ArrayList<>();
            for (Expr e : args) {
                qArgs.add(evaluate(e, map, isRet));
            }
            return evaluate(funcDefMap.get(((CallStmt)stmt).getId()), qArgs, tempMap, tempIsRet);
        } else {
            throw new RuntimeException("Unhandled Stmt type");
        }
    }

    boolean evaluate(Cond cond, Map<String, QVal> map, boolean[] isRet) {
        switch (cond.getOperator()) {
            case Cond.LESSEQUAL: return ((QInt)evaluate(cond.getExpr1(), map, isRet)).getVal() <= ((QInt)evaluate(cond.getExpr2(), map, isRet)).getVal();
            case Cond.GREATEREQUAL: return ((QInt)evaluate(cond.getExpr1(), map, isRet)).getVal() >= ((QInt)evaluate(cond.getExpr2(), map, isRet)).getVal();
            case Cond.LESS: return ((QInt)evaluate(cond.getExpr1(), map, isRet)).getVal() < ((QInt)evaluate(cond.getExpr2(), map, isRet)).getVal();
            case Cond.GREATER: return ((QInt)evaluate(cond.getExpr1(), map, isRet)).getVal() > ((QInt)evaluate(cond.getExpr2(), map, isRet)).getVal();
            case Cond.EQUAL: return ((QInt)evaluate(cond.getExpr1(), map, isRet)).getVal() == ((QInt)evaluate(cond.getExpr2(), map, isRet)).getVal();
            case Cond.NOTEQUAL: return ((QInt)evaluate(cond.getExpr1(), map, isRet)).getVal() != ((QInt)evaluate(cond.getExpr2(), map, isRet)).getVal();
            case Cond.NOT: return !evaluate((Cond)cond.getExpr1(), map, isRet);
            case Cond.AND: return evaluate((Cond)cond.getExpr1(), map, isRet) && evaluate((Cond)cond.getExpr2(), map, isRet);
            case Cond.OR: return evaluate((Cond)cond.getExpr1(), map, isRet) || evaluate((Cond)cond.getExpr2(), map, isRet);
            default: throw new RuntimeException("Unhandled operator");
        }
    }

    public QVal evaluate(Expr expr, Map<String, QVal> map, boolean[] isRet) {
        if (expr instanceof ConstExpr) {
            return new QInt((Long)((ConstExpr)expr).getValue());
        } else if (expr instanceof BinaryExpr) {
            BinaryExpr binaryExpr = (BinaryExpr)expr;
            switch (binaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), map, isRet)).getVal() + ((QInt)evaluate(binaryExpr.getRightExpr(), map, isRet)).getVal());
                case BinaryExpr.MINUS: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), map, isRet)).getVal() - ((QInt)evaluate(binaryExpr.getRightExpr(), map, isRet)).getVal());
                case BinaryExpr.TIMES: return new QInt(((QInt)evaluate(binaryExpr.getLeftExpr(), map, isRet)).getVal() * ((QInt)evaluate(binaryExpr.getRightExpr(), map, isRet)).getVal());
                case BinaryExpr.DOT: return new QRef(new QObj((QVal)evaluate(binaryExpr.getLeftExpr(), map, isRet), (QVal)evaluate(binaryExpr.getRightExpr(), map, isRet)));
                default: throw new RuntimeException("Unhandled operator");
            }
        }else if (expr instanceof UnaryExpr) {
            Expr child = ((UnaryExpr)expr).getExpr();
            QInt value = new QInt(-((QInt)evaluate(child, map, isRet)).getVal());
            return value;
        } else if (expr instanceof IdExpr) {
            return map.get(((IdExpr)expr).getId());
        } else if (expr instanceof CallExpr) {
            ArrayList<Expr> args = new ArrayList<>();
            if (((CallExpr)expr).getExprList() != null) {
                NeExprList neExprList = ((CallExpr)expr).getExprList().getNeExprList();
                args.add(neExprList.getExpr());
                while (neExprList.getNeExprList() != null) {
                    neExprList = neExprList.getNeExprList();
                    args.add(neExprList.getExpr());
                }
            }
            if (((CallExpr)expr).getId().equals("randomInt")) {
                Random random = new Random();
                QInt randomInt = new QInt((long)random.nextInt((int)((QInt)(evaluate(args.get(0), map, isRet))).getVal()));
                return randomInt;
            }
            if (((CallExpr)expr).getId().equals("left")) {
                QRef r = (QRef)evaluate(((CallExpr)expr).getExprList().getNeExprList().getExpr(), map, isRet);
                return r.getReferent().getLeft();
            }
            if (((CallExpr)expr).getId().equals("right")) {
                QRef r = (QRef)evaluate(((CallExpr)expr).getExprList().getNeExprList().getExpr(), map, isRet);
                return r.getReferent().getRight();
            }
            if (((CallExpr)expr).getId().equals("isAtom")) {
                QVal r = (QVal)evaluate(((CallExpr)expr).getExprList().getNeExprList().getExpr(), map, isRet);
                if (r instanceof QInt || ((QRef)r).getReferent() == null) {
                    return new QInt(1);
                }
                return new QInt(0);
            }
            if (((CallExpr)expr).getId().equals("isNil")) {
                QVal r = (QVal)evaluate(((CallExpr)expr).getExprList().getNeExprList().getExpr(), map, isRet);
                if (r instanceof QRef && ((QRef)r).getReferent() == null) {
                    return new QInt(1);
                }
                return new QInt(0);
            }
            if (((CallExpr)expr).getId().equals("setLeft")) {
                QRef r = (QRef)evaluate(((CallExpr)expr).getExprList().getNeExprList().getExpr(), map, isRet);
                QVal value = (QVal)evaluate(((CallExpr)expr).getExprList().getNeExprList().getNeExprList().getExpr(), map, isRet);
                r.referent.left = value;
                return new QInt(1);
            }
            if (((CallExpr)expr).getId().equals("setRight")) {
                QRef r = (QRef)evaluate(((CallExpr)expr).getExprList().getNeExprList().getExpr(), map, isRet);
                QVal value = (QVal)evaluate(((CallExpr)expr).getExprList().getNeExprList().getNeExprList().getExpr(), map, isRet);
                r.referent.right = value;
                return new QInt(1);
            }
            Map<String, QVal> tempMap = new HashMap<>(map);
            boolean[] tempIsRet = {false};
            ArrayList<QVal> qArgs = new ArrayList<>();
            for (Expr e : args) {
                qArgs.add(evaluate(e, map, isRet));
            }
            return evaluate(funcDefMap.get(((CallExpr)expr).getId()), qArgs, tempMap, tempIsRet);
        } else if (expr instanceof TypeCastExpr) {
            return evaluate(((TypeCastExpr)expr).getExpr(), map, isRet);
        } else if (expr instanceof NilExpr) {
            return new QRef(null);
        } else if (expr instanceof ConcurrencyExpr) {
            BinaryExpr concurrentBinaryExpr = ((ConcurrencyExpr)expr).getBinaryExpr();
            QVal leftVal;
            QVal rightVal;
            MyThread t1 = new MyThread(concurrentBinaryExpr.getLeftExpr(), map, isRet);
            MyThread t2 = new MyThread(concurrentBinaryExpr.getRightExpr(), map, isRet);
            t1.start();
            t2.start();
            try {
                t1.join();
                t2.join();
            } catch (InterruptedException e) {
                System.out.println("Interrupted.");
            }
            leftVal = t1.getVal();
            rightVal = t2.getVal();
            switch (concurrentBinaryExpr.getOperator()) {
                case BinaryExpr.PLUS: return new QInt(((QInt)leftVal).getVal() + ((QInt)rightVal).getVal());
                case BinaryExpr.MINUS: return new QInt(((QInt)leftVal).getVal() - ((QInt)rightVal).getVal());
                case BinaryExpr.TIMES: return new QInt(((QInt)leftVal).getVal() * ((QInt)rightVal).getVal());
                case BinaryExpr.DOT: return new QRef(new QObj(leftVal, rightVal));
                default: throw new RuntimeException("Unhandled operator");
            }
        } else {
            throw new RuntimeException("Unhandled Expr type");
        }
    }

	public static void fatalError(String message, int processReturnCode) {
        System.out.println(message);
        System.exit(processReturnCode);
	}
}
