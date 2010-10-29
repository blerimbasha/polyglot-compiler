/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.util.Position;
import polyglot.visit.*;

/**
 * An <code>AmbReceiver</code> is an ambiguous AST node composed of
 * dot-separated list of identifiers that must resolve to a receiver.
 */
public class AmbReceiver_c extends AmbPrefix_c implements AmbReceiver
{
    protected Type type;

    public AmbReceiver_c(Position pos, Prefix prefix, Id name) {
	super(pos, prefix, name);
	assert(name != null); // prefix may be null
    }

    public Type type() {
            return this.type;
    }

    public AmbReceiver type(Type type) {
            AmbReceiver_c n = (AmbReceiver_c) copy();
            n.type = type;
            return n;
    }
}
