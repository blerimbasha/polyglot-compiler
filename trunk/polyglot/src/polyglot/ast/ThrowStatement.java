/*
 * ThrowStatement.java
 */

package jltools.ast;
import jltools.util.CodeWriter;
import jltools.types.*;
import jltools.util.Annotate;

/**
 * ThrowStatement
 * 
 * Overview: ThrowStatement is a mutable representation of a throw
 *    statement.  ThrowStatement contains a single Expression which
 *    evaluates to the object being thrown.
 */
public class ThrowStatement extends Statement {
  
  /**
   * Effects: Creates a new ThrowStatement which throws the value
   * of the Expression <expr>.
   */
  public ThrowStatement (Expression expr) {
    this.expr = expr;
  }

  /** 
   * Effects: Returns the expression whose value is thrown by
   *    this ThrowStatement.
   */
  public Expression getExpression() {
    return expr;
  }

  /**
   * Effects: Sets the expression being thrown by this ThrowStatement
   *    to <newExpr>.
   */
  public void setExpression(Expression newExpr) {
    expr = newExpr;
  }

   /**
    *
    */
   void visitChildren(NodeVisitor vis)
   {
      expr = (Expression)expr.visit(vis);
   }

   public Node typeCheck(LocalContext c) throws TypeCheckException
   {
     if (! expr.getCheckedType().isThrowable())
       throw new TypeCheckException("Can only throw objects that extend from \"java.lang.Throwable\"");
     Annotate.addThrows ( this, expr.getCheckedType()  );
     Annotate.addThrows ( this, Annotate.getThrows( expr ) );
     return this;
   }

   public void  translate(LocalContext c, CodeWriter w)
   {
      w.write("throw ");
      expr.translate(c, w);
      w.write(";");
   }

   public Node dump( CodeWriter w)
   {
      w.write( "( THROW");
      dumpNodeInfo( w);
      w.write( ")");
      return null;
   }
  
  public Node copy() {
    ThrowStatement ts = new ThrowStatement(expr);
    ts.copyAnnotationsFrom(this);
    return ts;
  }

  public Node deepCopy() {
    ThrowStatement ts = new ThrowStatement((Expression) expr.deepCopy());
    ts.copyAnnotationsFrom(this);
    return ts;
  }

  private Expression expr;

}
  

  
