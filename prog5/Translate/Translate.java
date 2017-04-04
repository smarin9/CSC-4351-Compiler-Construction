package Translate;
import Symbol.Symbol;
import Tree.BINOP;
import Tree.CJUMP;
import Temp.Temp;
import Temp.Label;

public class Translate {
  public Frame.Frame frame;
  public Translate(Frame.Frame f) {
    frame = f;
  }
  private Frag frags;
  public void procEntryExit(Level level, Exp body) {
    Frame.Frame myframe = level.frame;
    Tree.Exp bodyExp = body.unEx();
    Tree.Stm bodyStm;
    if (bodyExp != null)
      bodyStm = MOVE(TEMP(myframe.RV()), bodyExp);
    else
      bodyStm = body.unNx();
    ProcFrag frag = new ProcFrag(myframe.procEntryExit1(bodyStm), myframe);
    frag.next = frags;
    frags = frag;
  }
  public Frag getResult() {
    return frags;
  }

  private static Tree.Exp CONST(int value) {
    return new Tree.CONST(value);
  }
  private static Tree.Exp NAME(Label label) {
    return new Tree.NAME(label);
  }
  private static Tree.Exp TEMP(Temp temp) {
    return new Tree.TEMP(temp);
  }
  private static Tree.Exp BINOP(int binop, Tree.Exp left, Tree.Exp right) {
    return new Tree.BINOP(binop, left, right);
  }
  private static Tree.Exp MEM(Tree.Exp exp) {
    return new Tree.MEM(exp);
  }
  private static Tree.Exp CALL(Tree.Exp func, Tree.ExpList args) {
    return new Tree.CALL(func, args);
  }
  private static Tree.Exp ESEQ(Tree.Stm stm, Tree.Exp exp) {
    if (stm == null)
      return exp;
    return new Tree.ESEQ(stm, exp);
  }

  private static Tree.Stm MOVE(Tree.Exp dst, Tree.Exp src) {
    return new Tree.MOVE(dst, src);
  }
  private static Tree.Stm EXP(Tree.Exp exp) {
    return new Tree.EXP(exp);
  }
  private static Tree.Stm JUMP(Label target) {
    return new Tree.JUMP(target);
  }
  private static
  Tree.Stm CJUMP(int relop, Tree.Exp l, Tree.Exp r, Label t, Label f) {
    return new Tree.CJUMP(relop, l, r, t, f);
  }
  private static Tree.Stm SEQ(Tree.Stm left, Tree.Stm right) {
    if (left == null)
      return right;
    if (right == null)
      return left;
    return new Tree.SEQ(left, right);
  }
  private static Tree.Stm LABEL(Label label) {
    return new Tree.LABEL(label);
  }

  private static Tree.ExpList ExpList(Tree.Exp head, Tree.ExpList tail) {
    return new Tree.ExpList(head, tail);
  }
  private static Tree.ExpList ExpList(Tree.Exp head) {
    return ExpList(head, null);
  }
  private static Tree.ExpList ExpList(ExpList exp) {
    if (exp == null)
      return null;
    return ExpList(exp.head.unEx(), ExpList(exp.tail));
  }

  public Exp Error() {
    return new Ex(CONST(0));
  }

  public Exp SimpleVar(Access access, Level level) {
    Level l = level;
    Tree.Exp fp = TEMP(level.frame.FP());
    for (l; l!= access.home; l = l.parent) { fp = l.frame.formals.head.exp(fp); }
    return new Ex(access.acc.exp(fp));
  }

  public Exp FieldVar(Exp record, int index) {
   Label good = new Label();
   Label bad = frame.badPtr();
   Temp tmp = new Temp();
   index *= frame.wordSize();
   return new Ex(ESEQ(SEQ(MOVE(TEMP(tmp), record.unEx()), SEQ(CJUMP(0, TEMP(tmp), CONST(0), bad, good), LABEL(good))), MEM(BINOP(0, TEMP(tmp), CONST(index)))));
  }

  public Exp SubscriptVar(Exp array, Exp index) {
    Label maybe = new Label();
    Label bad = frame.badSub();
    Label good = new Label();
    Temp t1 = new Temp();
    Temp t2 = new Temp();
    int size = frame.wordSize();
    return new Ex(ESEQ(SEQ(MOVE(TEMP(t1), array.unEx()), SEQ(MOVE(TEMP(t2), index.unEx()), SEQ(CJUMP(2, TEMP(t2), CONST(0), bad, maybe), SEQ(LABEL(maybe), SEQ(CJUMP(3, TEMP(t2), MEM(BINOP(0, TEMP(t1), CONST(-size))), bad, good), LABEL(good)))))), MEM(BINOP(0, TEMP(t1), BINOP(2, TEMP(t2), CONST(size))))));
  }

  public Exp NilExp() {
    return Error();
  }

  public Exp IntExp(int value) {
    return Error();
  }

  private java.util.Hashtable strings = new java.util.Hashtable();
  public Exp StringExp(String lit) {
    String u = lit.intern();
    Label lab = (Label)strings.get(u);
    if (lab == null) {
      lab = new Label();
      strings.put(u, lab);
      DataFrag frag = new DataFrag(frame.string(lab, u));
      frag.next = frags;
      frags = frag;
    }
    return new Ex(NAME(lab));
  }

  private Tree.Exp CallExp(Symbol f, ExpList args, Level from) {
    return frame.externalCall(f.toString(), ExpList(args));
  }
  private Tree.Exp CallExp(Level f, ExpList args, Level from) { 
    Tree.Exp fp = TEMP(from.frame.FP());
    if (f.parent != from) {
      for (Level l = from; l != f.parent; l = l.parent) {fp = l.frame.formals.head.exp(fp);}  
    }
    return CALL(NAME(f.frame.name), ExpList(fp, ExpList(args)));
  }

  public Exp FunExp(Symbol f, ExpList args, Level from) {
    return new Ex(CallExp(f, args, from));
  }
  public Exp FunExp(Level f, ExpList args, Level from) {
    return new Ex(CallExp(f, args, from));
  }
  public Exp ProcExp(Symbol f, ExpList args, Level from) {
    return new Nx(EXP(CallExp(f, args, from)));
  }
  public Exp ProcExp(Level f, ExpList args, Level from) {
    return new Nx(EXP(CallExp(f, args, from)));
  }

  public Exp OpExp(int op, Exp left, Exp right) { 
    switch (op){
      case 0: return new Ex(BINOP(0, left.unEx(), right.unEx()));
      case 1: return new Ex(BINOP(1, left.unEx(), right.unEx()));
      case 2: return new Ex(BINOP(2, left.unEx(), right.unEx()));
      case 3: return new Ex(BINOP(3, left.unEx(), right.unEx()));
      case 4: return new RelCx(0, left.unEx(), right.unEx());
      case 5: return new RelCx(1, left.unEx(), right.unEx());
      case 6: return new RelCx(2, left.unEx(), right.unEx());
      case 7: return new RelCx(4, left.unEx(), right.unEx());
      case 8: return new RelCx(3, left.unEx(), right.unEx());
      case 9: return new RelCx(5, left.unEx(), right.unEx());
      default: break;
    }
    throw new Error("Translate.opExp");
  }

  
  public Exp StrOpExp(int op, Exp left, Exp right) { 
    Tree.Exp cmp = this.frame.externalCall("strcmp", ExpList(left.unEx(), ExpList(right.unEx())));
    switch (op){
      case 4: return new RelCx(0, cmp, CONST(0));
      case 5: return new RelCx(1, cmp, CONST(0));
      case 6: return new RelCx(2, cmp, CONST(0));
      case 7: return new RelCx(4, cmp, CONST(0));
      case 8: return new RelCx(3, cmp, CONST(0));
      case 9: return new RelCx(5, cmp, CONST(0));
      default: break; 
    }
    throw new Error("Translate.StrOpExp");
  }

  public Exp RecordExp(ExpList init) { 
    int size = 0;
    for (ExpList exp = init; exp != null; exp = exp.tail) {size++;}
    Temp temp = new Temp();
    return new Ex(ESEQ(SEQ(MOVE(TEMP(temp), this.frame.externalCall("allocRecord", ExpList(CONST(size)))), startRecord(temp, 0, init, this.frame.wordSize())), TEMP(temp)));
  }
  
  private Tree.Stm startRecord(Temp r, int i, ExpList init, int wordSize){
    if (init == null) {return null;}
    return SEQ(MOVE(MEM(BINOP(0, TEMP(r), CONST(i))), init.head.unEx()), startRecord(r, i+wordSize, init.tail, wordSize));
  }

  public Exp SeqExp(ExpList e) { 
    if (e == null) { return new Nx(null);}
    Tree.Stm stm = null;
    while (e.tail != null){
      stm = SEQ(stm, e.head.unNx()));
      e = e.tail;
    }
    Tree.Exp result = e.head.unNx()));
    if (result == null) { return new Nx(SEQ(stm, e.head.unNx()));}
    return new Ex(ESEQ(stm, result));
  }
  

  public Exp AssignExp(Exp lhs, Exp rhs) {
    return new Nx(MOVE(lhs.unEx(), rhs.unEx()));
  }

  public Exp IfExp(Exp cc, Exp aa, Exp bb) {
    return new IfThenElseExp(cc, aa, bb);
  }

  public Exp WhileExp(Exp test, Exp body, Label done) {
    Label t1 = new Label();
    Label t2 - new Label();
    return new Nx(SEQ(SEQ(SEQ(LABEL(temp1), test.unCx(t2, done)), SEQ(SEQ(LABEL(t1), body.unNx()), JUMP(t1))), LABEL(done)));                         
  }                        
                             
 public Exp ForExp(Access i, Exp lo, Exp hi, Exp body, Label done){
   Label temp = new Label();
   Label incr = new Label();
   Temp cap = new Temp();
   Temp home = i.home.frame.FP();
   return new Nx(SEQ(SEQ(SEQ(SEQ(MOVE(i.acc.exp(TEMP(home)), lo.unEx()), MOVE(TEMP(cap), hi.unEx())), CJUMP(4, i.acc.exp(TEMP(home)), TEMP(cap), temp, done)), SEQ(SEQ(SEQ(LABEL(temp), body.unNx()), CJUMP(2, i.acc.exp(TEMP(home)), TEMP(cap), incr, done)), SEQ(SEQ(LABEL(incr), MOVE(i.acc.exp(TEMP(home)), BINOP(0, i.acc.exp(TEMP(home)), CONST(1)))), JUMP(temp)))), LABEL(done)));
 }
                             

  public Exp BreakExp(Label done) {
    return new Nx(JUMP(done));
  }
                               

  public Exp LetExp(ExpList lets, Exp body) {
    Tree.Stm stm = null;
    for (ExpList e = lets; e != null; e = e.tail) {stm = SEQ(stm, e.head.unNx());  }
    Tree.Exp result = body.unEx();
    if (result == null) { return new Nx(SEQ(stm, body.unNx())); }
    return new Ex(ESEQ(stm, result));
  }

  public Exp ArrayExp(Exp size, Exp init) {
    return new Ex(this.frame.externalCall("initArray", ExpList(size.unEx(), ExpList(init.unEx()))));
  }

  public Exp VarDec(Access a, Exp init) {
    return new Nx(MOVE(a.acc.exp(TEMP(a.home.frame.FP())), init.unEx()));
  }

  public Exp TypeDec() {
    return new Nx(null);
  }

  public Exp FunctionDec() {
    return new Nx(null);
  }
}
