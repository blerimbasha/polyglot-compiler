package jltools.ast;

/**
 * A <code>Throw</code> is an immutable representation of a <code>throw</code>
 * statement. Such a statement contains a single <code>Expr</code> which
 * evaluates to the object being thrown.
 */
public interface Throw extends Stmt {

    Expr expr();
    Throw expr(Expr expr);
}
