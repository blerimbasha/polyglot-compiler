/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import polyglot.types.SemanticException;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * An <code>AmbPrefix</code> is an ambiguous AST node composed of dot-separated
 * list of identifiers that must resolve to a prefix.
 */
public class AmbPrefix_c extends Node_c implements AmbPrefix
{
    protected Prefix prefix;
    protected Id name;

    public AmbPrefix_c(Position pos, Prefix prefix, Id name) {
        super(pos);
        assert(name != null); // prefix may be null
        this.prefix = prefix;
        this.name = name;
    }
    
    /** Get the name of the prefix. */
    public Id nameNode() {
        return this.name;
    }
    
    /** Set the name of the prefix. */
    public AmbPrefix name(Id name) {
        AmbPrefix_c n = (AmbPrefix_c) copy();
        n.name = name;
        return n;
    }

    /** Get the prefix of the prefix. */
    public Prefix prefix() {
	return this.prefix;
    }

    /** Set the prefix of the prefix. */
    public AmbPrefix prefix(Prefix prefix) {
	AmbPrefix_c n = (AmbPrefix_c) copy();
	n.prefix = prefix;
	return n;
    }

    /** Reconstruct the prefix. */
    protected AmbPrefix_c reconstruct(Prefix prefix, Id name) {
	if (prefix != this.prefix || name != this.name) {
	    AmbPrefix_c n = (AmbPrefix_c) copy();
	    n.prefix = prefix;
            n.name = name;
	    return n;
	}

	return this;
    }

    /** Visit the children of the prefix. */
    public Node visitChildren(NodeVisitor v) {
	Prefix prefix = (Prefix) visitChild(this.prefix, v);
        Id name = (Id) visitChild(this.name, v);
        return reconstruct(prefix, name);
    }

    /** Check exceptions thrown by the prefix. */
    public Node exceptionCheck(ExceptionChecker ec) throws SemanticException {
	throw new InternalCompilerError(position(),
	    "Cannot exception check ambiguous node " + this + ".");
    } 

    /** Write the prefix to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
	if (prefix != null) {
            print(prefix, w, tr);
            w.write(".");
        }
                
        tr.print(this, name, w);
    }

    public String toString() {
	return (prefix == null
		? name.toString()
		: prefix.toString() + "." + name.toString()) + "{amb}";
    }
}
