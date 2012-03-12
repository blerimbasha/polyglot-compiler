/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import polyglot.util.CodeWriter;
import polyglot.util.Position;

/**
 * A <code>BooleanLit</code> represents a boolean literal expression.
 */
public class BooleanLit_c extends Lit_c implements BooleanLit
{
  protected boolean value;

  public BooleanLit_c(Position pos, boolean value) {
    super(pos);
    this.value = value;
  }

  /** Get the value of the expression. */
  public boolean value() {
    return this.value;
  }

  /** Set the value of the expression. */
  public BooleanLit value(boolean value) {
    BooleanLit_c n = (BooleanLit_c) copy();
    n.value = value;
    return n;
  }

  public String toString() {
    return String.valueOf(value);
  }

  /** Dumps the AST. */
  public void dump(CodeWriter w) {
    super.dump(w);

    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(value " + value + ")");
    w.end();
  }
}