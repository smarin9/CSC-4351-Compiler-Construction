package Translate;
import Temp.Temp;
import Temp.Label;

class IfThenElseExp extends Exp {
  Exp cond, a, b;
  Label t = new Label();
  Label f = new Label();
  Label join = new Label();

  IfThenElseExp(Exp cc, Exp aa, Exp bb) {
    cond = cc; 
    a = aa; 
    b = bb;
  }

  Tree.Stm unCx(Label tt, Label ff) {
    Tree.Stm aStm = a.unCx(tt, ff);
    if (aStm instanceof JUMP) {
	    JUMP aJump = (JUMP)aStm;
	    if (aJump.exp instanceof NAME){
		    NAME aName = (NAME)aJump.exp;
		    aStm = null;
		    t=aName.label;
	    }
    }
    Tree.Stm bStm = b.unCx(tt, ff);
    if (bStm instanceof JUMP) {
	    JUMP bJump = (JUMP)bStm;
	    if (bJump.exp instanceof NAME){
		    NAME bName = (NAME)bJump.exp;
		    bStm = null;
		    t=bName.label;
	    }
    }  

    Tree.Stm condStm = cond.unCx(t, f);

    if (aStm == null && bStm == null)
      return condStm;
    if (aStm == null)
      return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(f), bStm));
    if (bStm == null)
      return new Tree.SEQ(condStm, new Tree.SEQ(new Tree.LABEL(t), aStm));
    return new Tree.SEQ(condStm,
			new Tree.SEQ(new Tree.SEQ(new Tree.LABEL(t), aStm),
				     new Tree.SEQ(new Tree.LABEL(f), bStm)));
  }
	

  Tree.Exp unEx() {
    Temp temp = new Temp();
    Tree.Exp aExp = a.unEx();
    if (aExp == null) {return null;}
    Tree.Exp bExp = b.unEx();
    if (bExp == null) {return null;}	
    else { return new ESEQ(new SEQ(cond.unCx(t, f), new SEQ(new SEQ(new LABEL(t), new SEQ(new MOVE(new TEMP(temp), aExp), new JUMP(join))), new SEQ(new LABEL(f), new SEQ(new MOVE(new TEMP(temp), bExp), new JUMP(join))))), new LABEL(join)), new TEMP(temp));}	  
  }

  Tree.Stm unNx() {
	  Stm aStm = a.unNx();
	  if (aStm == null) {t = join;}
	  else {aStm = new SEQ(new SEQ(new LABEL(t), aStm), new JUMP(join));}
	  Stm bStm = b.unNx();
	  if (bStm == null) {t = join;}
	  else {bStm = new SEQ(new SEQ(new LABEL(t), bStm), new JUMP(join));}
	  if ((aStm == null) && (bStm == null)) {return cond.unNx();}
	  Stm condStm = cond.uncx(t, f);
	  if (aStm == null) {return new SEQ(new SEQ(condStm, bStm), new LABEL(join));}
	  if (bStm == null) {return new SEQ(new SEQ(condStm, aStm), new LABEL(join));}
	  else {return new SEQ(new SEQ(condStm, new SEQ(aStm, bStm)), new LABEL(join));}
  }
}
