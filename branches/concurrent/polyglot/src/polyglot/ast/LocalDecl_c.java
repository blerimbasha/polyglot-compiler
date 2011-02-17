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
import polyglot.util.CodeWriter;
import polyglot.util.Position;
import polyglot.visit.NodeVisitor;

/**
 * A <code>LocalDecl</code> is an immutable representation of the declaration
 * of a local variable.
 */
public class LocalDecl_c extends Stmt_c implements LocalDecl {
    protected FlagsNode flags;
    protected TypeNode type;
    protected Id name;
    protected Expr init;
    protected LocalDef li;

    public LocalDecl_c(Position pos, FlagsNode flags, TypeNode type,
                       Id name, Expr init)
    {
        super(pos);
        assert(flags != null && type != null && name != null); // init may be null
        this.flags = flags;
        this.type = type;
        this.name = name;
        this.init = init;
    }
    
    public List<Def> defs() {
        return Collections.<Def>singletonList(li);
    }
    
    /** Get the type of the declaration. */
    public Type declType() {
        return type.type();
    }

    /** Get the flags of the declaration. */
    public FlagsNode flags() {
        return flags;
    }

    /** Set the flags of the declaration. */
    public LocalDecl flags(FlagsNode flags) {
        LocalDecl_c n = (LocalDecl_c) copy();
        n.flags = flags;
        return n;
    }

    /** Get the type node of the declaration. */
    public TypeNode typeNode() {
        return type;
    }

    /** Set the type of the declaration. */
    public LocalDecl typeNode(TypeNode type) {
        if (type == this.type) return this;
        LocalDecl_c n = (LocalDecl_c) copy();
        n.type = type;
        return n;
    }
    
    /** Get the name of the declaration. */
    public Id name() {
        return name;
    }
    
    /** Set the name of the declaration. */
    public LocalDecl name(Id name) {
        LocalDecl_c n = (LocalDecl_c) copy();
        n.name = name;
        return n;
    }

    /** Get the initializer of the declaration. */
    public Expr init() {
        return init;
    }

    /** Set the initializer of the declaration. */
    public LocalDecl init(Expr init) {
        if (init == this.init) return this;
        LocalDecl_c n = (LocalDecl_c) copy();
        n.init = init;
        return n;
    }

    /** Set the local instance of the declaration. */
    public LocalDecl localDef(LocalDef li) {
        if (li == this.li) return this;
        LocalDecl_c n = (LocalDecl_c) copy();
        assert li != null;
        n.li = li;
        return n;
    }

    /** Get the local instance of the declaration. */
    public LocalDef localDef() {
        return li;
    }
    
    public VarDef varDef() {
        return li;
    }

    /** Reconstruct the declaration. */
    protected LocalDecl_c reconstruct(FlagsNode flags, TypeNode type, Id name, Expr init) {
        if (this.flags != flags || this.type != type || this.name != name || this.init != init) {
            LocalDecl_c n = (LocalDecl_c) copy();
            n.flags = flags;
            n.type = type;
            n.name = name;
            n.init = init;
            return n;
        }

        return this;
    }

    /** Visit the children of the declaration. */
    public Node visitChildren(NodeVisitor v) {
        TypeNode type = (TypeNode) visitChild(this.type, v);
        FlagsNode flags = (FlagsNode) visitChild(this.flags, v);
        Id name = (Id) visitChild(this.name, v);
        Expr init = (Expr) visitChild(this.init, v);
        return reconstruct(flags, type, name, init);
    }

    /**
     * Add the declaration of the variable as we enter the scope of the
     * intializer
     */
    public Context enterChildScope(Node child, Context c) {
        if (child == init) {
            c = c.pushBlock();
            addDecls(c);
        }
        return super.enterChildScope(child, c);
    }

    public void addDecls(Context c) {
        // Add the declaration of the variable in case we haven't already done
        // so in enterScope, when visiting the initializer.
        c.addVariable(li.asInstance());
    }

    public String toString() {
        return flags.flags().translate() + type + " " + name +
                (init != null ? " = " + init : "") + ";";
    }

    public void dump(CodeWriter w) {
        super.dump(w);

        if (li != null) {
            w.allowBreak(4, " ");
            w.begin(0);
            w.write("(instance " + li + ")");
            w.end();
        }
    }
}