package Semant;

public class VarEntry extends Entry {
  Translate.Access access;
  public Types.Type ty;
  VarEntry(Types.Type t) {
    ty = t;
  }
  
  VarEntry(Translate.acess a, Types.Type t){
    acess = a;
    ty = t;
  }
}
