package jltools.lex;

import java_cup.runtime.Symbol;
import jltools.util.Position;

/** A token class for int literals. */
public class IntegerLiteral extends NumericLiteral {
  public IntegerLiteral(Position position, int i, int sym) {
      super(position, sym);
      this.val = new Integer(i);
  }
}