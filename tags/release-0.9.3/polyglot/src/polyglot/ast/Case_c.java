package polyglot.ext.jl.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.visit.*;
import polyglot.util.*;

/**
 * A <code>Case</code> is a representation of a Java <code>case</code>
 * statement.  It can only be contained in a <code>Switch</code>.
 */
public class Case_c extends Stmt_c implements Case
{
    protected Expr expr;
    protected long value;

    public Case_c(Del ext, Position pos, Expr expr) {
	super(ext, pos);
	this.expr = expr;
    }

    /** Returns true iff this is the default case. */
    public boolean isDefault() {
	return this.expr == null;
    }

    /**
     * Get the case label.  This must should a constant expression.
     * The case label is null for the <code>default</code> case.
     */
    public Expr expr() {
	return this.expr;
    }

    /** Set the case label.  This must should a constant expression, or null. */
    public Case expr(Expr expr) {
	Case_c n = (Case_c) copy();
	n.expr = expr;
	return n;
    }

    /**
     * Returns the value of the case label.  This value is only valid
     * after type-checking.
     */
    public long value() {
	return this.value;
    }

    /** Set the value of the case label. */
    public Case value(long value) {
	Case_c n = (Case_c) copy();
	n.value = value;
	return n;
    }

    /** Reconstruct the statement. */
    protected Case_c reconstruct(Expr expr) {
	if (expr != this.expr) {
	    Case_c n = (Case_c) copy();
	    n.expr = expr;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
        Expr expr = (Expr) visitChild(this.expr, v);
        return reconstruct(expr);
    }

    /** Type check the statement. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (expr == null) {
	    return this;
	}

	TypeSystem ts = tc.typeSystem();

	if (! ts.isImplicitCastValid(expr.type(), ts.Int())) {
	    throw new SemanticException(
		"Case label must be an byte, char, short, or int.",
		position());
	}

	long value;

	if (expr instanceof NumLit) {
	    value = ((NumLit) expr).longValue();
	}
	else if (expr instanceof Field) {
	    FieldInstance fi = ((Field) expr).fieldInstance();

	    if (fi == null) {
	        throw new InternalCompilerError(
		    "Undefined FieldInstance after type-checking.");
	    }

	    if (! fi.isConstant()) {
	        throw new SemanticException("Case label must be a constant.",
					    position());
	    }

	    value = ((Number) fi.constantValue()).longValue();
	}
	else if (expr instanceof Local) {
	    LocalInstance li = ((Local) expr).localInstance();

	    if (li == null) {
	        throw new InternalCompilerError(
		    "Undefined LocalInstance after type-checking.");
	    }

	    if (! li.isConstant()) {
	        throw new SemanticException("Case label must be a constant.",
					    position());
	    }

	    value = ((Number) li.constantValue()).longValue();
	}
	else {
	    throw new SemanticException("Case label must be a constant.",
					position());
	}

	return value(value);
    }

    public Type childExpectedType(Expr child, AscriptionVisitor av) {
        TypeSystem ts = av.typeSystem();

        if (child == expr) {
            return ts.Int();
        }

        return child.type();
    }

    public String toString() {
        if (expr == null) {
	    return "default:";
	}
	else {
	    return "case " + expr + ":";
	}
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        if (expr == null) {
	    w.write("default:");
	}
	else {
	    w.write("case ");
	    tr.print(expr, w);
	    w.write(":");
	}
    }
}
