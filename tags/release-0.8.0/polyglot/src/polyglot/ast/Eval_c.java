package jltools.ext.jl.ast;

import jltools.ast.*;
import jltools.types.*;
import jltools.visit.*;
import jltools.util.*;

/**
 * An <code>Eval</code> is a wrapper for an expression in the context of
 * a statement.
 */
public class Eval_c extends Stmt_c implements Eval
{
    protected Expr expr;

    public Eval_c(Ext ext, Position pos, Expr expr) {
	super(ext, pos);
	this.expr = expr;
    }

    /** Get the expression of the statement. */
    public Expr expr() {
	return this.expr;
    }

    /** Set the expression of the statement. */
    public Eval expr(Expr expr) {
	Eval_c n = (Eval_c) copy();
	n.expr = expr;
	return n;
    }

    /** Reconstruct the statement. */
    protected Eval_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    Eval_c n = (Eval_c) copy();
	    n.expr = expr;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
	Expr expr = (Expr) this.expr.visit(v);
	return reconstruct(expr);
    }

    public String toString() {
	return expr.toString();
    }

    /** Write the statement to an output file. */
    public void translate_(CodeWriter w, Translator tr) {
	boolean semi = tr.appendSemicolon(true);

	expr.ext().translate(w, tr);

	if (semi) {
	    w.write(";");
	}

	tr.appendSemicolon(semi);
    }
}