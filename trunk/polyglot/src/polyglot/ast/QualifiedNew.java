package jltools.ast;

import jltools.types.ConstructorInstance;
import java.util.List;

/**
 * A <code>New</code> is an immutable representation of the use of the
 * <code>new</code> operator to create a new instance of a class.  In
 * addition to the type of the class being created, a <code>New</code> has a
 * list of arguments to be passed to the constructor of the object and an
 * optional <code>ClassBody</code> used to support anonymous classes. Such an
 * expression may also be proceeded by an qualifier expression which specifies
 * the context in which the object is being created.
 */
public interface QualifiedNew extends Expr 
{
    Expr qualifier();
    QualifiedNew qualifier(Expr qualifier);

    String typeName();
    QualifiedNew typeName(String typeName);

    List arguments();
    QualifiedNew arguments(List arguments);

    ClassBody body();
    QualifiedNew body(ClassBody body);
}
