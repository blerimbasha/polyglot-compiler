package jltools.ast;

import jltools.types.*;
import jltools.util.*;

import java.util.*;


/**
 * A <code>Field</code> is an immutable representation of a Java field
 * access.  It consists of field name and may also have either a 
 * <code>Type</code> or an <code>Expression</code> containing the field being 
 * accessed.
 */
public class FieldExpression extends Expression 
{
  // FIXME
  private FieldInstance fi;

  protected final Node target;
  protected final String name;

  /**
   * Creates a new <code>EFieldExpression</code>.
   *
   * @pre <code>target</code> is either a <code>TypeNode</code> or an 
   * <code>Expression</code>.
   */
  public FieldExpression( Node target, String name) 
  {
    if (target != null && ! (target instanceof TypeNode ||
			     target instanceof Expression))
     throw new InternalCompilerError( "Target of a field access must be a "
                                      + "type or expression.");

    this.target = target;
    this.name = name;
  }

  /**
   * Lazily reconstruct this node. 
   */
  public FieldExpression reconstruct( Node target, String name) 
  {
    if( this.target == target && this.name.equals( name)) {
      return this;
    }
    else {
      FieldExpression n = new FieldExpression( target, name);
      n.copyAnnotationsFrom( this);
      return n;
    }
  }

  /**
   * Returns the target that the field is being accessed from.
   */
  public Node getTarget() 
  {
    return target;
  }

  /**
   * Returns the name of the field being accessed in the target of this node.
   */
  public String getName() 
  {
    return name;
  }

  // FIXME
  public FieldInstance getFieldInstance()
  {
    return fi;
  }

  /**
   * Visit the children of this node.
   */
  Node visitChildren( NodeVisitor v) 
  {
    if ( target == null) 
      return this;
    return reconstruct( target.visit( v), name);
  }

  public Node typeCheck( LocalContext c) throws SemanticException
  {
    Type ltype;

    if (target == null)
      ltype = null;
    else if( target instanceof Expression) {
      ltype = ((Expression)target).getCheckedType();
    }
    else if( target instanceof TypeNode) {
      ltype = ((TypeNode)target).getCheckedType();
    }
    else {
      throw new InternalCompilerError(
                              "Attempting field access on node of type " 
                              + target.getClass().getName());
    }

    if( ltype == null ||
        ltype instanceof ClassType ||
        ltype instanceof ArrayType) {
      if (name.equals("class"))
      {
        Annotate.setExpectedType( target, ltype);
        setCheckedType( c.getTypeSystem().getClass_());
      }      
      else
      {
        fi = c.getField( ltype, name);
        
        if (target != null)
          Annotate.setExpectedType( target, fi.getEnclosingType());
        setCheckedType( fi.getType());
      }
    }
    else {
      throw new SemanticException( 
                    "Cannot access a field of an expression of type "
                    + ltype.getTypeString());
    }

    return this;
  }

  public void translate( LocalContext c, CodeWriter w)
  {
    if (target != null) 
    {
      if( target instanceof Expression) {
        translateExpression( (Expression)target, c, w);
        w.write( ".");
      }
      else if( target instanceof TypeNode) {
        if( ((TypeNode)target).getCheckedType() != c.getCurrentClass() ||
            name.equals( "class")) {
          target.translate(c, w);
          w.write( ".");
        }
      }
    }   
    w.write( name);
  }

  public void dump( CodeWriter w)
  {
    w.write( "( FIELD ACCESS");
    w.write( " < " + name + " > ");
    dumpNodeInfo( w);
    w.write( ")");
  }

  public int getPrecedence()
  {
    return PRECEDENCE_OTHER;
  }
}
    
  
