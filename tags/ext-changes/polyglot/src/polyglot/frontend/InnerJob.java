package jltools.frontend;

import jltools.ast.*;
import jltools.types.*;
import jltools.visit.*;
import jltools.util.*;
import jltools.frontend.Compiler;

import java.util.*;
import java.io.IOException;

/**
 * A <code>State</code> encapsulates work done by the compiler on behalf of
 * one source file.  It includes all information carried between phases
 * of the compiler.
 */
public class InnerJob extends Job
{
    protected Job outer;
    protected Context context;
    protected Pass.ID begin;
    protected Pass.ID end;
    protected String name;

    /** Construct a new job for a given source and compiler. */
    public InnerJob(Compiler c, JobExt ext, Node ast, Context context,
                    Job outer, Pass.ID begin, Pass.ID end) {
        super(c, ext, null, ast);

        name = "inner-job[" + begin + ".." + end + "](" +
            context.currentCode() + " in " + context.currentClass() + ")";

	this.context = context;
	this.outer = outer;
        this.begin = begin;
        this.end = end;
        if (ast == null) {
            throw new InternalCompilerError("Null ast");
        }
        if (outer == null) {
            throw new InternalCompilerError("Null outer job");
        }
    }

    public String toString() {
        return name + " (" +
            (isRunning() ? "running " : "before ") + nextPass() + ")";
    }

    public List getPasses() {
      	List l = compiler.sourceExtension().passes(this, begin, end);

        for (int i = 0; i < l.size(); i++) {
            Pass pass = (Pass) l.get(i);
            if (pass.id() == begin) {
                nextPass = i;
            }
        }

        return l;
    }

    public Context context() {
	return context;
    }

    public Source source() {
	return outer.source();
    }
}
