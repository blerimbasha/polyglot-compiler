package jltools.ext.jl.ast;

import jltools.ast.*;

import jltools.util.*;
import jltools.types.*;
import jltools.visit.*;

/**
 * A <code>Catch</code> represents one half of a <code>try... catch</code>
 * statement.  Specifically, the second half.
 */
public class Catch_c extends Stmt_c implements Catch
{
    protected Formal formal;
    protected Block body;

    public Catch_c(Del ext, Position pos, Formal formal, Block body) {
	super(ext, pos);
	this.formal = formal;
	this.body = body;
    }

    /** Get the catchType of the catch block. */
    public Type catchType() {
        return formal.declType();
    }

    /** Get the formal of the catch block. */
    public Formal formal() {
	return this.formal;
    }

    /** Set the formal of the catch block. */
    public Catch formal(Formal formal) {
	Catch_c n = (Catch_c) copy();
	n.formal = formal;
	return n;
    }

    /** Get the body of the catch block. */
    public Block body() {
	return this.body;
    }

    /** Set the body of the catch block. */
    public Catch body(Block body) {
	Catch_c n = (Catch_c) copy();
	n.body = body;
	return n;
    }

    /** Reconstruct the catch block. */
    protected Catch_c reconstruct(Formal formal, Block body) {
	if (formal != this.formal || body != this.body) {
	    Catch_c n = (Catch_c) copy();
	    n.formal = formal;
	    n.body = body;
	    return n;
	}

	return this;
    }

    /** Visit the children of the catch block. */
    public Node visitChildren(NodeVisitor v) {
	Formal formal = (Formal) visitChild(this.formal, v);
	Block body = (Block) visitChild(this.body, v);
	return reconstruct(formal, body);
    }

    public void enterScope(Context c) {
        c.pushBlock();
    }

    public void leaveScope(Context c) {
        c.popBlock();
    }

    /** Type check the catch block. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();

	if (! catchType().isThrowable()) {
	    throw new SemanticException(
		"Can only throw subclasses of \"" +
		ts.Throwable() + "\".", formal.position());

	}

	return this;
    }

    /*
    public String toString() {
	return "catch (" + formal + ") " + body;
    }
    */

    /** Write the catch block to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("catch (");
	printBlock(formal, w, tr);
	w.write(")");
	printSubStmt(body, w, tr);
    }

    public void translate(CodeWriter w, Translator tr) {
        Context c = tr.context();

	w.write("catch (");
	printBlock(formal, w, tr);
	w.write(")");

	enterScope(c);
	printSubStmt(body, w, tr);
	leaveScope(c);
    }
}
