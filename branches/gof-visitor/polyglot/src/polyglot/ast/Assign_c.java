/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.Collections;
import java.util.List;

import polyglot.frontend.Globals;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * An <code>Assign</code> represents a Java assignment expression.
 */
public abstract class Assign_c extends Expr_c implements Assign, Ambiguous
{
  protected Operator op;
  protected Expr right;

  public Assign_c(Position pos, Operator op, Expr right) {
    super(pos);
    assert(op != null && right != null);
    this.op = op;
    this.right = right;
  }

  /** Get the precedence of the expression. */
  public Precedence precedence() {
    return Precedence.ASSIGN;
  }

  /** Get the left operand of the expression. */
  public abstract Expr left(NodeFactory nf);

  /** Get the operator of the expression. */
  public Operator operator() {
    return this.op;
  }

  /** Set the operator of the expression. */
  public Assign operator(Operator op) {
    Assign_c n = (Assign_c) copy();
    n.op = op;
    return n;
  }

  /** Get the right operand of the expression. */
  public Expr right() {
    return this.right;
  }

  /** Set the right operand of the expression. */
  public Assign right(Expr right) {
      assert right != null;
      if (right == this.right) return this;
    Assign_c n = (Assign_c) copy();
    n.right = right;
    return n;
  }
  
  public abstract Assign visitLeft(NodeVisitor v);

  /** Visit the children of the expression. */
  public Node visitChildren(NodeVisitor v) {
      Assign a = visitLeft(v);
      Expr right = (Expr) visitChild(a.right(), v);
      return a.right(right);
  }
  
  public abstract Type leftType();
  
  public Type childExpectedType(Expr child, AscriptionVisitor av) {
      if (child == right) {
          TypeSystem ts = av.typeSystem();	

          // If the RHS is an integral constant, we can relax the expected
          // type to the type of the constant.
          if (ts.numericConversionValid(leftType(), child.constantValue(), av.context())) {
              return child.type();
          }
          else {
              return leftType();
          }
      }

      return child.type();
  }

  /** Get the throwsArithmeticException of the expression. */
  public boolean throwsArithmeticException() {
    // conservatively assume that any division or mod may throw
    // ArithmeticException this is NOT true-- floats and doubles don't
    // throw any exceptions ever...
    return op == DIV_ASSIGN || op == MOD_ASSIGN;
  }

  public String toString() {
    return left(Globals.NF()) + " " + op + " " + right;
  }

  /** Write the expression to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    printSubExpr(left(Globals.NF()), true, w, tr);
    w.write(" ");
    w.write(op.toString());
    w.allowBreak(2, 2, " ", 1); // miser mode
    w.begin(0);
    printSubExpr(right, false, w, tr);
    w.end();
  }

  /** Dumps the AST. */
  public void dump(CodeWriter w) {
    super.dump(w);
    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(operator " + op + ")");
    w.end();
  }

  abstract public Term firstChild();

  public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
      if (operator() == ASSIGN) {
          acceptCFGAssign(v);          
      }
      else {
          acceptCFGOpAssign(v);                    
      }
    return succs;
  }

  /**
   * ###@@@DOCO TODO
   */
  protected abstract void acceptCFGAssign(CFGBuilder v);

  /**
   * ###@@@DOCO TODO
   */
  protected abstract void acceptCFGOpAssign(CFGBuilder v);
  
  public List<Type> throwTypes(TypeSystem ts) {
    if (throwsArithmeticException()) {
        return Collections.<Type>singletonList(ts.ArithmeticException());
    }

    return Collections.<Type>emptyList();
  }
  
}