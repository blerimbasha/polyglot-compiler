package jltools.ast;

import jltools.util.*;
import jltools.types.*;


/**
 * A ReturnStatment is an immutable representation of a <code>return</code>
 * statement in Java.
 */
public class ReturnStatement extends Statement 
{
  protected final Expression expr;

  /**
   * Creates a new <code>ReturnStatement</code> which returns 
   * <code>expr</code>.
   *
   * @param expr The expression to be returned. May optionally be 
   *  <code>null</code> if no expression is returned.
   */
  public ReturnStatement( Expression expr) 
  {
    this.expr = expr;
  }
  
  /**
   * Lazily reconstruct this node.
   */
  public ReturnStatement reconstruct( Expression expr)
  {
    if( this.expr == expr) {
      return this;
    }
    else {
      ReturnStatement n = new ReturnStatement( expr);
      n.copyAnnotationsFrom( this);
      return n;
    }
  }

  /**
   * Returns the expression which would be returned by this statement.
   */ 
  public Expression getExpression() 
  {
    return expr;
  }

  /** 
   * Visit the children of this node.
   *
   * @pre Requires that <code>expr.visit</code> returns an object of type
   *  <code>Expression</code>.
   */
  Node visitChildren( NodeVisitor v) 
  {
    return reconstruct( (expr == null ? null : (Expression)expr.visit( v)));
  }
  
  public Node typeCheck( LocalContext c) throws SemanticException
  {
    MethodTypeInstance mti = c.getCurrentMethod();
    if (mti instanceof MethodTypeInstanceInitializer)
      throw new SemanticException( "Return statements are not valid inside an " +
                                   "initializer block.");
                                  
    if( expr == null) {
      if( !mti.getReturnType().equals( c.getTypeSystem().getVoid())) {
        throw new SemanticException( 
                          "Method \"" + mti.getName() + "\" must return "
                          + "an expression of type \"" 
                          + mti.getReturnType().getTypeString() + "\".");

      }
    }
    else {
      if( mti.getReturnType().equals( c.getTypeSystem().getNull())) {
        throw new SemanticException(
                       "A return statement which returns a value can only"
                       + " occur in a method which does not have type void.");
      }
      else if( !expr.getCheckedType().descendsFrom( mti.getReturnType()) &&
               !expr.getCheckedType().equals( mti.getReturnType())) {
        throw new SemanticException( 
                          "Method \"" + mti.getName() + "\" must return "
                          + "an expression of type \"" 
                          + mti.getReturnType().getTypeString() + "\".");
      } 
    }
    
    return this;
  }
  
  public void translate( LocalContext c, CodeWriter w)
  {
    w.write( "return") ;
    if( expr != null) {
      w.write( " ");
      expr.translate( c, w);
    }
    w.write( ";");
  }

  public void dump( CodeWriter w)
  {
    w.write( "( RETURN ");
    dumpNodeInfo( w);
    w.write( ")");
  }
}
