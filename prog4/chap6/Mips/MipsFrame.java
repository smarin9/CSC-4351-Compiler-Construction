package Mips;
import java.util.Hashtable;
import Symbol.Symbol;
import Temp.Temp;
import Temp.Label;
import Frame.Frame;
import Frame.Access;
import Frame.AccessList;

public class MipsFrame extends Frame {

  private int count = 0;
  int offset;
  
  public Frame newFrame(Symbol name, Util.BoolList formals) {
    Label label;
    if (name == null)
      label = new Label();
    else if (this.name != null)
      label = new Label(this.name + "." + name + "." + count++);
    else
      label = new Label(name);
    return new MipsFrame(label, formals);
  }

  
  private AccessList traverseFormals(int offset, Util.Boollist fromals){
    Access access;
    if (fromals.head)
      access = new InFrame(offset);
    else if (formals == null)
      return null;
    else
      access = new InReg(new Temp());
    return new AccessList(access, traverseFormasls(wordsize + offset, fromals.tail));
  }
  
  
  public MipsFrame() {}
  private MipsFrame(Label n, Util.BoolList f) {
    name = n;
    formals = traverseFormals(0, f);
  }

  private static final int wordSize = 4;
  public int wordSize() { return wordSize; }

  public Access allocLocal(boolean escape) {
    if (!escape) {return new InReg(new Temp());}
    else{
      offset = offset - wordSize;
      return new InFrame(offset);
    }
  }
}
