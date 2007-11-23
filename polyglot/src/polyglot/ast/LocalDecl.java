/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import polyglot.types.Flags;
import polyglot.types.LocalDef;

/** 
 * A local variable declaration statement: a type, a name and an optional
 * initializer.
 */
public interface LocalDecl extends ForInit, VarDecl, VarInit
{
    /** Set the declaration's flags. */
    LocalDecl flags(Flags flags);

    /** Set the declaration's type. */
    LocalDecl type(TypeNode type);

    /** Set the declaration's name. */
    LocalDecl id(Id name);

    /** Get the declaration's initializer expression, or null. */
    Expr init();
    /** Set the declaration's initializer expression. */
    LocalDecl init(Expr init);

    /**
     * Set the type object for the local we are declaring.
     */
    LocalDecl localDef(LocalDef li);
    
    boolean constantValueSet();
}
