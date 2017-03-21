package FindEscape;

public class FindEscape {
  Symbol.Table escEnv = new Symbol.Table(); // escEnv maps Symbol to Escape
  Absyn.FunctionDec parentFunction = null;

  public FindEscape(Absyn.Exp e) { traverseExp(0, e);  }

  void traverseVar(int depth, Absyn.Var v) {
    if (v instanceof Absyn.FieldVar)
      traverseVar(depth, (Absyn.FieldVar)v);
    else if (v instanceof Absyn.SimpleVar)
      traverseVar(depth, (Absyn.SimpleVar)v);
    else if (v instanceof Absyn.SubscriptVar)
      traverseVar(depth, (Absyn.SubscriptVar)v);
    else
      throw new Error("Error");
  }

  void traverseExp(int depth, Absyn.Exp e) {
    if (e instanceof Absyn.ArrayExp)
      traverseExp(depth, (Absyn.ArrayExp)e);
    else if (e instanceof Absyn.AssignExp)
      traverseExp(depth, Absyn.AssignExp)e);
    else if (e instanceof Absyn.BreakExp)
      traverseExp(depth, (Absyn.BreakExp)e);
    else if (e instanceof Absyn.CallExp)
      traverseExp(depth, (Absyn.CallExp)e);
    else if (e instanceof Absyn.ForExp)
      traverseExp(depth, (Absyn.ForExp)e);
    else if (e instanceof Absyn.IfExp)
      traverseExp(depth, (Absyn.IfExp)e);
    else if (e instanceof Absyn.IntExp)
      traverseExp(depth, (Absyn.IntExp)e);
    else if (e instanceof Absyn.LetExp)
      traverseExp(depth, Absyn.LetExp)e);
    else if (e instanceof Absyn.NilExp)
      traverseExp(depth, Absyn.NilExp)e);
    else if (e instanceof Absyn.OpExp)
      traverseExp(depth, Absyn.OpExp)e);
    else if (e instanceof Absyn.RecordExp)
      traverseExp(depth, Absyn.RecordExp)e);
    else if (e instanceof Absyn.SeqExp)
      traverseExp(depth, Absyn.SeqExp)e);
    else if (e instanceof Absyn.StringExp)
      traverseExp(depth, Absyn.StringExp)e);
    else if (e instanceof Absyn.VarExp)
      traverseExp(depth, (Absyn.VarExp)e);
    else if (e instanceof Absyn.WhileExp)
      traverseExp(depth, (Absyn.WhileExp)e);
    else 
      throw new Error("error");
  }

  void traverseDec(int depth, Absyn.Dec d) {
  }
}
