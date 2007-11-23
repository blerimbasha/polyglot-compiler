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

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * An <code>Assign</code> represents a Java assignment expression.
 */
public abstract class Assign_c extends Expr_c implements Assign
{
  protected Expr left;
  protected Operator op;
  protected Expr right;

  public Assign_c(Position pos, Expr left, Operator op, Expr right) {
    super(pos);
    assert(left != null && op != null && right != null);
    this.left = left;
    this.op = op;
    this.right = right;
  }

  /** Get the precedence of the expression. */
  public Precedence precedence() {
    return Precedence.ASSIGN;
  }

  /** Get the left operand of the expression. */
  public Expr left() {
    return this.left;
  }

  /** Set the left operand of the expression. */
  public Assign left(Expr left) {
    Assign_c n = (Assign_c) copy();
    n.left = left;
    return n;
  }

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
    Assign_c n = (Assign_c) copy();
    n.right = right;
    return n;
  }

  /** Reconstruct the expression. */
  protected Assign_c reconstruct(Expr left, Expr right) {
    if (left != this.left || right != this.right) {
      Assign_c n = (Assign_c) copy();
      n.left = left;
      n.right = right;
      return n;
    }

    return this;
  }

  /** Visit the children of the expression. */
  public Node visitChildren(NodeVisitor v) {
    Expr left = (Expr) visitChild(this.left, v);
    Expr right = (Expr) visitChild(this.right, v);
    return reconstruct(left, right);
  }


  /** Type check the expression. */
  public Node typeCheck(TypeChecker tc) throws SemanticException {
    Type t = left.type();
    Type s = right.type();

    TypeSystem ts = tc.typeSystem();

    if (! (left instanceof Variable)) {
        throw new SemanticException("Target of assignment must be a variable.",
                                    position());
    }

    if (op == ASSIGN) {
      if (! ts.isImplicitCastValid(s, t) &&
          ! ts.typeEquals(s, t) &&
          ! ts.numericConversionValid(t, right.constantValue())) {

        throw new SemanticException("Cannot assign " + s + " to " + t + ".",
                                    position());
      }

      return type(t);
    }

    if (op == ADD_ASSIGN) {
      // t += s
      if (ts.typeEquals(t, ts.String()) && ts.canCoerceToString(s, tc.context())) {
        return type(ts.String());
      }

      if (t.isNumeric() && s.isNumeric()) {
        return type(ts.promote(t, s));
      }

      throw new SemanticException("The " + op + " operator must have "
                                  + "numeric or String operands.",
                                  position());
    }

    if (op == SUB_ASSIGN || op == MUL_ASSIGN ||
        op == DIV_ASSIGN || op == MOD_ASSIGN) {
      if (t.isNumeric() && s.isNumeric()) {
        return type(ts.promote(t, s));
      }

      throw new SemanticException("The " + op + " operator must have "
                                  + "numeric operands.",
                                  position());
    }

    if (op == BIT_AND_ASSIGN || op == BIT_OR_ASSIGN || op == BIT_XOR_ASSIGN) {
      if (t.isBoolean() && s.isBoolean()) {
        return type(ts.Boolean());
      }

      if (ts.isImplicitCastValid(t, ts.Long()) &&
          ts.isImplicitCastValid(s, ts.Long())) {
        return type(ts.promote(t, s));
      }

      throw new SemanticException("The " + op + " operator must have "
                                  + "integral or boolean operands.",
                                  position());
    }

    if (op == SHL_ASSIGN || op == SHR_ASSIGN || op == USHR_ASSIGN) {
      if (ts.isImplicitCastValid(t, ts.Long()) &&
          ts.isImplicitCastValid(s, ts.Long())) {
        // Only promote the left of a shift.
        return type(ts.promote(t));
      }

      throw new SemanticException("The " + op + " operator must have "
                                  + "integral operands.",
                                  position());
    }

    throw new InternalCompilerError("Unrecognized assignment operator " +
                                    op + ".");
  }
  
  public Type childExpectedType(Expr child, AscriptionVisitor av) {
      if (child == right) {
          TypeSystem ts = av.typeSystem();

          // If the RHS is an integral constant, we can relax the expected
          // type to the type of the constant.
          if (ts.numericConversionValid(left.type(), child.constantValue())) {
              return child.type();
          }
          else {
              return left.type();
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
    return left + " " + op + " " + right;
  }

  /** Write the expression to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    printSubExpr(left, true, w, tr);
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
  public Node copy(NodeFactory nf) {
      return nf.Assign(this.position, this.left, this.op, this.right);
  }
  
}
