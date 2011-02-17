/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import java.util.HashMap;
import java.util.Map;

import polyglot.dispatch.ConstantValueVisitor;
import polyglot.dispatch.TypeChecker;
import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.types.*;
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * An <code>Expr</code> represents any Java expression.  All expressions
 * must be subtypes of Expr.
 */
public abstract class Expr_c extends Term_c implements Expr
{
    protected Ref<Type> typeRef;

    public Expr_c(Position pos) {
	super(pos);
	TypeSystem ts = Globals.TS();
	this.typeRef = Types.<Type>lazyRef(ts.unknownType(position()));
    }

    /**
     * Get the type of the expression.  This may return an
     * <code>UnknownType</code> before type-checking, but should return the
     * correct type after type-checking.
     */
    public Ref<Type> typeRef() {
    	return this.typeRef;
    }
    
    public Type type() {
	return Types.get(this.typeRef);
    }
    
    /** Set the type of the expression. */
    public Expr type(Type type) {
    	Expr_c n = (Expr_c) copy();
    	n.typeRef.update(type);
    	return n;
    }

    public void dump(CodeWriter w) {
        super.dump(w);

	if (typeRef != null) {
	    w.allowBreak(4, " ");
	    w.begin(0);
	    w.write("(type " + typeRef + ")");
	    w.end();
	}
    }

    /** Get the precedence of the expression. */
    public Precedence precedence() {
	return Precedence.UNKNOWN;
    }

    public boolean isConstant() {
	Job job = Globals.currentJob();
	TypeSystem ts = Globals.TS();
	NodeFactory nf = Globals.NF();
	Object v = this.accept(new ConstantValueVisitor(job, ts, nf));
	return v != ConstantValueVisitor.NOT_CONSTANT;
    }

    public Object constantValue() {
	Job job = Globals.currentJob();
	TypeSystem ts = Globals.TS();
	NodeFactory nf = Globals.NF();
	Object v = this.accept(new ConstantValueVisitor(job, ts, nf));
	if (v == ConstantValueVisitor.NOT_CONSTANT)
	    return null;
	return v;
    }

    public String stringValue() {
        return (String) constantValue();
    }

    public boolean booleanValue() {
        return ((Boolean) constantValue()).booleanValue();
    }

    public byte byteValue() {
        return ((Byte) constantValue()).byteValue();
    }

    public short shortValue() {
        return ((Short) constantValue()).shortValue();
    }

    public char charValue() {
        return ((Character) constantValue()).charValue();
    }

    public int intValue() {
        return ((Integer) constantValue()).intValue();
    }

    public long longValue() {
        return ((Long) constantValue()).longValue();
    }

    public float floatValue() {
        return ((Float) constantValue()).floatValue();
    }

    public double doubleValue() {
        return ((Double) constantValue()).doubleValue();
    }

    /**
     * Correctly parenthesize the subexpression <code>expr<code> given
     * the its precendence and the precedence of the current expression.
     *
     * If the sub-expression has the same precedence as this expression
     * we do not parenthesize.
     *
     * @param expr The subexpression.
     * @param w The output writer.
     * @param pp The pretty printer.
     */
    public void printSubExpr(Expr expr, CodeWriter w, PrettyPrinter pp) {
        printSubExpr(expr, true, w, pp);
    }

    /**
     * Correctly parenthesize the subexpression <code>expr<code> given
     * the its precendence and the precedence of the current expression.
     *
     * If the sub-expression has the same precedence as this expression
     * we parenthesize if the sub-expression does not associate; e.g.,
     * we parenthesis the right sub-expression of a left-associative
     * operator.
     *
     * @param expr The subexpression.
     * @param associative Whether expr is the left (right) child of a left-
     * (right-) associative operator.
     * @param w The output writer.
     * @param pp The pretty printer.
     */
    public void printSubExpr(Expr expr, boolean associative,
                             CodeWriter w, PrettyPrinter pp) {
        if (! associative && precedence().equals(expr.precedence()) ||
	    precedence().isTighter(expr.precedence())) {
    		w.write("(");
    		printBlock(expr, w, pp);
    		w.write(")");
	}
        else {
            print(expr, w, pp);
        }
    }
}