/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.*;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>ConstructorDecl</code> is an immutable representation of a
 * constructor declaration as part of a class body.
 */
public class ConstructorDecl_c extends Term_c implements ConstructorDecl
{
    protected Flags flags;
    protected Id name;
    protected List formals;
    protected List throwTypes;
    protected Block body;
    protected ConstructorInstance ci;

    public ConstructorDecl_c(Position pos, Flags flags, Id name, List formals, List throwTypes, Block body) {
	super(pos);
	assert(flags != null && name != null && formals != null && throwTypes != null); // body may be null
	this.flags = flags;
	this.name = name;
	this.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	this.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
	this.body = body;
    }
    
    public boolean isDisambiguated() {
        return ci != null && ci.isCanonical() && super.isDisambiguated();
    }
    
    public MemberInstance memberInstance() {
        return ci;
    }

    /** Get the flags of the constructor. */
    public Flags flags() {
	return this.flags;
    }

    /** Set the flags of the constructor. */
    public ConstructorDecl flags(Flags flags) {
        if (flags.equals(this.flags)) return this;
	ConstructorDecl_c n = (ConstructorDecl_c) copy();
	n.flags = flags;
	return n;
    }
    
    /** Get the name of the constructor. */
    public Id id() {
        return this.name;
    }
    
    /** Set the name of the constructor. */
    public ConstructorDecl id(Id name) {
        ConstructorDecl_c n = (ConstructorDecl_c) copy();
        n.name = name;
        return n;
    }

    /** Get the name of the constructor. */
    public String name() {
	return this.name.id();
    }

    /** Set the name of the constructor. */
    public ConstructorDecl name(String name) {
        return id(this.name.id(name));
    }

    /** Get the formals of the constructor. */
    public List formals() {
	return Collections.unmodifiableList(this.formals);
    }

    /** Set the formals of the constructor. */
    public ConstructorDecl formals(List formals) {
	ConstructorDecl_c n = (ConstructorDecl_c) copy();
	n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	return n;
    }

    /** Get the throwTypes of the constructor. */
    public List throwTypes() {
	return Collections.unmodifiableList(this.throwTypes);
    }

    /** Set the throwTypes of the constructor. */
    public ConstructorDecl throwTypes(List throwTypes) {
	ConstructorDecl_c n = (ConstructorDecl_c) copy();
	n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
	return n;
    }

    public Term codeBody() {
        return this.body;
    }
    
    /** Get the body of the constructor. */
    public Block body() {
	return this.body;
    }

    /** Set the body of the constructor. */
    public CodeBlock body(Block body) {
	ConstructorDecl_c n = (ConstructorDecl_c) copy();
	n.body = body;
	return n;
    }

    /** Get the constructorInstance of the constructor. */
    public ConstructorInstance constructorInstance() {
	return ci;
    }


    /** Get the procedureInstance of the constructor. */
    public ProcedureInstance procedureInstance() {
	return ci;
    }

    public CodeInstance codeInstance() {
	return procedureInstance();
    }
    
    /** Set the constructorInstance of the constructor. */
    public ConstructorDecl constructorInstance(ConstructorInstance ci) {
        if (ci == this.ci) return this;
	ConstructorDecl_c n = (ConstructorDecl_c) copy();
	n.ci = ci;
	return n;
    }

    /** Reconstruct the constructor. */
    protected ConstructorDecl_c reconstruct(Id name, List formals, List throwTypes, Block body) {
	if (name != this.name || ! CollectionUtil.equals(formals, this.formals) || ! CollectionUtil.equals(throwTypes, this.throwTypes) || body != this.body) {
	    ConstructorDecl_c n = (ConstructorDecl_c) copy();
	    n.name = name;
	    n.formals = TypedList.copyAndCheck(formals, Formal.class, true);
	    n.throwTypes = TypedList.copyAndCheck(throwTypes, TypeNode.class, true);
	    n.body = body;
	    return n;
	}

	return this;
    }

    /** Visit the children of the constructor. */
    public Node visitChildren(NodeVisitor v) {
        Id name = (Id) visitChild(this.name, v);
	List formals = visitList(this.formals, v);
	List throwTypes = visitList(this.throwTypes, v);
	Block body = (Block) visitChild(this.body, v);
	return reconstruct(name, formals, throwTypes, body);
    }

    public NodeVisitor buildTypesEnter(TypeBuilder tb) throws SemanticException {
        return tb.pushCode();
    }

    public Node buildTypes(TypeBuilder tb) throws SemanticException {
        TypeSystem ts = tb.typeSystem();

        ParsedClassType ct = tb.currentClass();

        if (ct == null) {
            return this;
        }

        List formalTypes = new ArrayList(formals.size());
        for (int i = 0; i < formals.size(); i++) {
            formalTypes.add(ts.unknownType(position()));
        }

        List throwTypes = new ArrayList(throwTypes().size());
        for (int i = 0; i < throwTypes().size(); i++) {
            throwTypes.add(ts.unknownType(position()));
        }

        ConstructorInstance ci = ts.constructorInstance(position(), ct,
                                                        flags, formalTypes, throwTypes);
        ct.addConstructor(ci);

        return constructorInstance(ci);
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (this.ci.isCanonical()) {
            // already done
            return this;
        }

        List formalTypes = new LinkedList();
        List throwTypes = new LinkedList();
            
        for (Iterator i = formals.iterator(); i.hasNext(); ) {
            Formal f = (Formal) i.next();
            if (! f.isDisambiguated()) {
                return this;
            }
            formalTypes.add(f.declType());
        }

        ci.setFormalTypes(formalTypes);

        for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
            TypeNode tn = (TypeNode) i.next();
            if (! tn.isDisambiguated()) {
                return this;
            }
            throwTypes.add(tn.type());
        }

        ci.setThrowTypes(throwTypes);

        return this;
    }

    public Context enterScope(Context c) {
        return c.pushCode(ci);
    }

    /** Type check the constructor. */
    public Node typeCheck(TypeChecker tc) throws SemanticException {
        Context c = tc.context();
        TypeSystem ts = tc.typeSystem();

        ClassType ct = c.currentClass();

	if (ct.flags().isInterface()) {
	    throw new SemanticException(
		"Cannot declare a constructor inside an interface.",
		position());
	}

        if (ct.isAnonymous()) {
	    throw new SemanticException(
		"Cannot declare a constructor inside an anonymous class.",
		position());
        }

        String ctName = ct.name();

        if (! ctName.equals(name.id())) {
	    throw new SemanticException("Constructor name \"" + name +
                "\" does not match name of containing class \"" +
                ctName + "\".", position());
        }

	try {
	    ts.checkConstructorFlags(flags());
	}
	catch (SemanticException e) {
	    throw new SemanticException(e.getMessage(), position());
	}

	if (body == null && ! flags().isNative()) {
	    throw new SemanticException("Missing constructor body.",
		position());
	}

	if (body != null && flags().isNative()) {
	    throw new SemanticException(
		"A native constructor cannot have a body.", position());
	}

        for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
            TypeNode tn = (TypeNode) i.next();
            Type t = tn.type();
            if (! t.isThrowable()) {
                throw new SemanticException("Type \"" + t +
                    "\" is not a subclass of \"" + ts.Throwable() + "\".",
                    tn.position());
            }
        }

        return this;
    }

    public NodeVisitor exceptionCheckEnter(ExceptionChecker ec) throws SemanticException {
        return ec.push(constructorInstance().throwTypes());
    }

    public String toString() {
	return flags.translate() + name + "(...)";
    }

    /** Write the constructor to an output file. */
    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
	w.begin(0);
	w.write(flags().translate());

        tr.print(this, name, w);
	w.write("(");

	w.begin(0);

	for (Iterator i = formals.iterator(); i.hasNext(); ) {
	    Formal f = (Formal) i.next();
	    print(f, w, tr);

	    if (i.hasNext()) {
		w.write(",");
		w.allowBreak(0, " ");
	    }
	}

	w.end();
	w.write(")");

	if (! throwTypes().isEmpty()) {
	    w.allowBreak(6);
	    w.write("throws ");

	    for (Iterator i = throwTypes().iterator(); i.hasNext(); ) {
	        TypeNode tn = (TypeNode) i.next();
		print(tn, w, tr);

		if (i.hasNext()) {
		    w.write(",");
		    w.allowBreak(4, " ");
		}
	    }
	}

	w.end();
    }

    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        prettyPrintHeader(w, tr);

	if (body != null) {
	    printSubStmt(body, w, tr);
	}
	else {
	    w.write(";");
	}
    }

    public void dump(CodeWriter w) {
	super.dump(w);

	if (ci != null) {
	    w.allowBreak(4, " ");
	    w.begin(0);
	    w.write("(instance " + ci + ")");
	    w.end();
	}
    }

    public Term firstChild() {
        return listChild(formals(), body() != null ? body() : null);
    }

    public List acceptCFG(CFGBuilder v, List succs) {
        if (body() != null) {
            v.visitCFGList(formals(), body(), true);
            v.visitCFG(body(), this, false);
        }
        else {
            v.visitCFGList(formals(), this, false);
        }
        
        return succs;
    }
    public Node copy(NodeFactory nf) {
        return nf.ConstructorDecl(this.position, this.flags, this.name, this.formals, this.throwTypes, this.body);
    }

}