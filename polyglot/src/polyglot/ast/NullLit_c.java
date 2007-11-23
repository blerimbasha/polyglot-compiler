/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * The Java literal <code>null</code>.
 */
public class NullLit_c extends Lit_c implements NullLit
{
    public NullLit_c(Position pos) {
	super(pos);
    }

    /** Type check the expression. */
    public Node typeCheck(TypeChecker tc) {
	return type(tc.typeSystem().Null());
    }

    /** Get the value of the expression, as an object. */
    public Object objValue() {
	return null;
    }

    public String toString() {
	return "null";
    }

    /** Write the expression to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	w.write("null");
    }

    public Object constantValue() {
        return null;
    }
    
    public Node copy(NodeFactory nf) {
        return nf.NullLit(this.position);
    }

}
