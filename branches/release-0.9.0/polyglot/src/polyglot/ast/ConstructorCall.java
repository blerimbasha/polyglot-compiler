package polyglot.ast;

import polyglot.types.ConstructorInstance;
import polyglot.util.Enum;
import java.util.List;

/**
 * A <code>ConstructorCall</code> is an immutable representation of
 * a direct call to a constructor of a class in the form of
 * <code>super(...)</code> or <code>this(...)</code>.
 */
public interface ConstructorCall extends Stmt
{
    /** Constructor call kind: either "super" or "this". */
    public static class Kind extends Enum {
        public Kind(String name) { super(name); }
    }

    public static final Kind SUPER = new Kind("super");
    public static final Kind THIS    = new Kind("this");

    /** The qualifier of the call, possibly null. */
    Expr qualifier();

    /** Set the qualifier of the call, possibly null. */
    ConstructorCall qualifier(Expr qualifier);

    /** The kind of the call: THIS or SUPER. */
    Kind kind();

    /** Set the kind of the call: THIS or SUPER. */
    ConstructorCall kind(Kind kind);

    /**
     * Actual arguments.
     * A list of <code>Expr</code>.
     * @see polyglot.ast.Expr
     */
    List arguments();

    /**
     * Set the actual arguments.
     * A list of <code>Expr</code>.
     * @see polyglot.ast.Expr
     */
    ConstructorCall arguments(List arguments);

    /**
     * The constructor that is called.  This field may not be valid until
     * after type checking.
     */
    ConstructorInstance constructorInstance();

    /** Set the constructor to call. */
    ConstructorCall constructorInstance(ConstructorInstance ci);
}