package jltools.ast;

import jltools.types.Type;
import jltools.util.CodeWriter;
import jltools.visit.PrettyPrinter;

/**
 * An <code>Expr</code> represents any Java expression.  All expressions
 * must be subtypes of Expr.
 */
public interface Expr extends Receiver {

    /**
     * Return an equivalent expression, but with the type <code>type</code>.
     */
    Expr type(Type type);

    /**
     * Return the expected type of the expression.
     */
    Type expectedType();

    /**
     * Return an equivalent expression, but with the expected type
     * <code>type</code>.
     */
    Expr expectedType(Type type);

    /** Get the precedence of the expression. */
    Precedence precedence();

    /**
     * Correctly parenthesize the subexpression <code>expr<code> given
     * based on its precedence and the precedence of this expression.
     *
     * If the sub-expression has the same precedence as this expression
     * we parenthesize if the sub-expression does not associate; e.g.,
     * we parenthesis the right sub-expression of a left-associative
     * operator.
     */
    void printSubExpr(Expr expr, boolean associative,
                      CodeWriter w, PrettyPrinter pp);

    /**
     * Correctly parenthesize the subexpression <code>expr<code> given
     * based on its precedence and the precedence of this expression.
     *
     * This is equivalent to <code>printSubexpr(expr, true, w, pp)</code>
     */
    void printSubExpr(Expr expr, CodeWriter w, PrettyPrinter pp);
}
