package polyglot.ext.jl.ast;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;
import polyglot.frontend.*;
import java.util.*;

/**
 * A local class declaration statement.  The node is just a wrapper around
 * a class declaration.
 */
public class LocalClassDecl_c extends Stmt_c implements LocalClassDecl
{
    protected ClassDecl decl;

    public LocalClassDecl_c(Del ext, Position pos, ClassDecl decl) {
	super(ext, pos);
	this.decl = decl;
    }

    /** Get the class declaration. */
    public ClassDecl decl() {
	return this.decl;
    }

    /** Set the class declaration. */
    public LocalClassDecl decl(ClassDecl decl) {
	LocalClassDecl_c n = (LocalClassDecl_c) copy();
	n.decl = decl;
	return n;
    }

    /** Reconstruct the statement. */
    protected LocalClassDecl_c reconstruct(ClassDecl decl) {
        if (decl != this.decl) {
	    LocalClassDecl_c n = (LocalClassDecl_c) copy();
	    n.decl = decl;
	    return n;
	}

	return this;
    }

    /** Visit the children of the statement. */
    public Node visitChildren(NodeVisitor v) {
        ClassDecl decl = (ClassDecl) visitChild(this.decl, v);
        return reconstruct(decl);
    }

    public Context updateScope(Context c) {
        // We should now be back in the scope of the enclosing block.
        // Add the type.
        c.addType(decl.type().toClass().toLocal());
        return c;
    }

    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {
        return ar.bypassChildren(this);
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == AmbiguityRemover.ALL) {
            ClassDecl d = (ClassDecl) ar.job().spawn(ar.context(), decl,
                                                     Pass.CLEAN_SUPER,
                                                     Pass.ADD_MEMBERS_ALL);

            if (d == null) {
                throw new SemanticException(
                    "Could not disambiguate local class \"" + decl.name() + "\".",
                    position());
            }

            LocalClassDecl n = decl(d);
            return n.visitChild(d, ar);
        }

        return this;
    }

    public String toString() {
	return decl.toString();
    }

    /** Write the statement to an output file. */
    public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
        printBlock(decl, w, tr);
	w.write(";");
    }
}
