/*
 * MethodNode.java
 */

package jltools.ast;

import java.util.*;

import jltools.types.*;
import jltools.util.*;
import jltools.visit.SymbolReader;


/**
 * Overview: A MethodNode is a mutable representation of a methods
 * definition as part of a class body.  It consists of a method name,
 * a list of formal parameters, a list of exceptions which may be
 * thrown, a return type, access flags, and a method body.  A Method
 * may be a isConstructor in which case it does not have a name (as it
 * must be the same as the class, nor does it have a return type.
 */
public class MethodNode extends ClassMember {
  /**
   * Requires: all elements of <formals> are of type FormalParameter,
   * all elements of <exceptions> are of type Type.
   *
   * Overview: Creates a new MethodNode to represent a isConstructor
   * which takes <formals> as parameters, may throw exceptions in
   * <exceptions>, is modified by <accessFlags> and contains <body>
   * for a body.
   */
  public MethodNode(AccessFlags accessFlags,
                    String name,
		    List formals,
		    List exceptions,
		    BlockStatement body) {
    this.accessFlags = accessFlags;
    TypedList.check(formals, FormalParameter.class);
    if ( formals == null) throw new Error();
    this.formals = new ArrayList(formals);
    TypedList.check(exceptions, TypeNode.class);
    this.exceptions = new ArrayList(exceptions);
    this.body = body;
    this.name = name;
    this.returnType = null;
    this.isConstructor = true;
    this.additionalDimensions = 0;
    mtiThis = null;
  }

  /**
   * Requires: all elements of <formals> are of type FormalParameter,
   * all elements of <exceptions> are of type Type.
   *
   * Overview: Creates a new MethodNode to represent a method by the
   * name <name> which takes <formals> as parameters, returns type
   * <returnType>, may throw exceptions in <exceptions>, is modified
   * by <accessFlags> and contains <body> for a body.
   */
  public MethodNode(AccessFlags accessFlags,
		    TypeNode returnType,
		    String name,
		    List formals,
		    List exceptions,
		    BlockStatement body) {
    this.accessFlags = accessFlags;
    TypedList.check(formals, FormalParameter.class);
    this.formals = new ArrayList(formals);
    TypedList.check(exceptions, TypeNode.class);
    this.exceptions = new ArrayList(exceptions);
    this.body = body;
    this.name = name;
    this.returnType = returnType;
    this.isConstructor = false;
    this.additionalDimensions = 0;
    mtiThis = null;
  }

  /**
   * Effects: Returns true iff this MethodNode represents a isConstructor.
   */
  public boolean isConstructor() {
    return isConstructor;
  }

  /**
   * Effects: Returns the AccessFlags modifing this MethodNode.
   */
  public AccessFlags getAccessFlags() {
    return accessFlags;
  }

  /**
   * Effects: Sets the AccessFlags for this Method to be <newFlags>.
   */
  public void setAccessFlags(AccessFlags newFlags) {
    accessFlags = newFlags;
  }

  /**
   * Effects: Returns the return type of this MethodNode.
   */
  public TypeNode getReturnType() {
    return returnType;
  }

  /**
   * Effects: Sets the return type for this MethodNode to be
   * <newReturnType>.
   */
  public void setReturnType(TypeNode newReturnType) {
    if(additionalDimensions > 0)
    {
      Type type = newReturnType.getType();
      newReturnType.setType(new ArrayType(type.getTypeSystem(),
          type, additionalDimensions));
    }
    returnType = newReturnType;
  }

  /**
   * Effects: Sets the return type for this MethodNode to be
   * <newReturnType>.
   */
  public void setReturnType(Type newReturnType) {
    if(additionalDimensions > 0)
    {
      newReturnType = new ArrayType(null, newReturnType, additionalDimensions);
    }
    returnType = new TypeNode(newReturnType);
  }

  public void addAdditionalDimension() {
    additionalDimensions++;
  }

  /**
   * Effects: Returns the name of this this method.
   */
  public String getName() {
    return name;
  }

  /**
   * Effects: Sets the name of the method reprented by this to <newName>.
   */
  public void setName(String newName) {
    name = newName;
  }

  /**
   * Effects: Adds a formal parameter <fp> to the list of arguments taken
   * by this method.
   */
  public void addFormalParameter(FormalParameter fp) {
    formals.add(fp);
  }

  /**
   * Effects: Returns the parameter of this method as position <pos>.
   * Throws IndexOutOfBoundsException if <pos> is not valid.
   */
  public FormalParameter getFormalParameter(int pos) {
    return (FormalParameter) formals.get(pos);
  }

  /**
   * Effects: Removes the formal parameter at position <pos>.  Throws
   * an IndexOutOfBounds if <pos> is not valid.
   */
  public void removeFormalParameter(int pos) {
    formals.remove(pos);
  }

  /**
   * Effects: Returns a TypedListIterator which produces the
   * FormalParameters in order of the method defined by this.
   */
  public TypedListIterator formalParameters() {
    return new TypedListIterator(formals.listIterator(),
				 FormalParameter.class,
				 false);
  }

  /**
   * Effects: Adds <excep> to the list of exceptions thrown by the
   * method defined by this.
   */
  public void addException(Type excep) {
    exceptions.add(excep);
  }

  /**
   * Effects: Returns the exception at position <pos> in the exception
   * list.  Throws IndexOutOfBoundsException if <pos> is not valid.
   */
  public Type getException(int pos) {
    return (Type) exceptions.get(pos);
  }

  /**
   * Effects: Removes the exception at position <pos> in the exception
   * list.  Throws an IndexOutOfBoundsException if <pos> is not valid.
   */
  public void removeException(int pos) {
    exceptions.remove(pos);
  }

  public void setExceptions(List exceptions) {
    this.exceptions = exceptions;
  }

  /**
   * Effects: Returns a typed list iterator which returns the
   * exceptions thrown by this in order.
   */
  public TypedListIterator exceptions() {
    return new TypedListIterator(exceptions.listIterator(),
				 TypeNode.class,
				 false);
  }

  /**
   * Effects: Returns the BlockStatement representing the body of this
   * method.
   */
  public BlockStatement getBody() {
    return body;
  }

  /**
   * Effects: Sets the body of this to be <newBody>.
   */
  public void setBody(BlockStatement newBody) {
    body = newBody;
  }

  public void translate(LocalContext c, CodeWriter w)
  {
    w.write ( accessFlags.getStringRepresentation() );
    if (! isConstructor())
    {
      returnType.translate(c, w);
      w.write (" " + name + "( ");
    }
    else
    {
      w.write(name + "( ");
    }
    for (Iterator i = formals.iterator(); i.hasNext(); )
    {
      ((FormalParameter)i.next()).translate(c, w);
      if (i.hasNext())
        w.write (", ");
    }
    w.write(")");
    if (! exceptions.isEmpty())
    {
      w.write (" throws " );
      for (Iterator i = exceptions.iterator(); i.hasNext(); )
      {
        w.write ( ((TypeNode)i.next()).getType().getTypeString() );

        w.write ( (i.hasNext() ? ", " : "" ));
      }
    }
    

    if( !mtiThis.getAccessFlags().isAbstract()) {
      // FIXME should be abstract for interfaces.
      if( body != null) {
        w.newline( 0);
        body.translate(c, w);
      }
      else {
        w.write( ";");
      }
    }
    else {
      w.write( ";");
    }
  }

  public Node dump( CodeWriter w)
  {
    w.write( "( METHOD");
    w.write( " < " + name + " >");
    w.write( " < " + accessFlags.getStringRepresentation() + "> ");
    if( isConstructor) {
      w.write( "< isConstructor > ");
    }
    if( additionalDimensions > 0) {
      w.write( "< " + additionalDimensions + " > ");
    }
    dumpNodeInfo( w);
    w.write( ")");
    return null;
  }
  
  public Node readSymbols( SymbolReader sr)
  {
    ParsedClassType clazz = sr.getCurrentClass();
    TypeSystem ts = sr.getTypeSystem();

    if ( isConstructor )
    {
      returnType = new TypeNode (ts.getVoid());
      Annotate.setLineNumber( returnType, Annotate.getLineNumber( this ) );
    }

    /* Build a list of argument types. */
    List argTypes = new LinkedList();
    Iterator iter = formals.iterator();
    while( iter.hasNext()) {
      argTypes.add( ((FormalParameter)iter.next()).getType());
    }
    
    if ( isConstructor)
    {
      mtiThis = new ConstructorTypeInstance( ts, argTypes, exceptions, accessFlags) ;
    }
    else if( additionalDimensions == 0 ) {
      mtiThis = new MethodTypeInstance( ts, name,
                          returnType.getType(), argTypes, 
                          exceptions, accessFlags);
    }
    else {
      mtiThis = new MethodTypeInstance( ts, name,
                          new ArrayType( ts, returnType.getType(), 
                                         additionalDimensions),
                          argTypes, exceptions, accessFlags);     
    }

    Annotate.setLineNumber( mtiThis, Annotate.getLineNumber( this));
    clazz.addMethod( mtiThis);

    return this;
  }

  public Node adjustScope(LocalContext c)
  {
    c.enterMethod ( mtiThis ) ;
    return null;
  }

  public Node typeCheck( LocalContext c) throws TypeCheckException
  {
    boolean bThrowDeclared;

    if( body != null) {
      Annotate.addThrows ( this, Annotate.getThrows(body ) );

      // check our exceptions:
      
      SubtypeSet s = jltools.util.Annotate.getThrows( this );
      if ( s != null)
      {
        for (Iterator i = s.iterator(); i.hasNext() ; )
        {
          bThrowDeclared = false;
          Type t = (Type)i.next();
          
          if ( !t.isUncheckedException() )
          {
            for (Iterator i2 = exceptions.iterator(); i2.hasNext() ; )
            {
              Type t2 =  (ClassType)  ((TypeNode)i2.next()).getType();
              if ( t.equals (t2) || t.descendsFrom (t2 ))
              {
                bThrowDeclared = true; 
                break;
              }
            }
            if ( ! bThrowDeclared)
              throw new TypeCheckException ( 
                    "Method \"" + name + "\" throws the undeclared "
                    + "exception \"" + t.getTypeString() + "\".");
          }
        }
      }
       
      // make sure that all paths return, if our return type is not void
      if ( !mtiThis.getReturnType().equals (c.getTypeSystem().getVoid() ) &&
             !Annotate.terminatesOnAllPaths ( body ) &&
           ! mtiThis.getAccessFlags().isAbstract() )
        throw new TypeCheckException ( 
                          "Not all execution paths in the method \""
                          + name + "\" lead to a return or throw statement.");
    }
      
    c.leaveMethod(  ) ;
    return this;
  }

  Object visitChildren(NodeVisitor v) 
  {
    Object vinfo = Annotate.getVisitorInfo( this);
    
    if( returnType != null) {
      returnType = (TypeNode)returnType.visit( v);
      vinfo = v.mergeVisitorInfo( Annotate.getVisitorInfo( returnType), vinfo);
    }
    
    for (ListIterator i = formals.listIterator(); i.hasNext(); )
    {
      FormalParameter f = (FormalParameter)i.next();
      f = (FormalParameter)f.visit( v);
      vinfo = v.mergeVisitorInfo( Annotate.getVisitorInfo( f), vinfo);
      i.set( f);
    }
    for (ListIterator i = exceptions.listIterator(); i.hasNext(); )
    {
      TypeNode t = (TypeNode)i.next();
      t = (TypeNode)t.visit( v);
      vinfo = v.mergeVisitorInfo( Annotate.getVisitorInfo( t), vinfo);
      i.set( t);
    }

    if( body != null) {
      body = (BlockStatement) body.visit(v); 
      vinfo = v.mergeVisitorInfo( Annotate.getVisitorInfo( body), vinfo);
    }
    return vinfo;
  }

  public Node copy() {
    return copy(false);
  }

  public Node deepCopy() {
    return copy(true);
  }

  private Node copy(boolean deep) {
    MethodNode mn;
    List newFormals = new ArrayList(formals.size());
    for (Iterator i = formals.iterator(); i.hasNext(); ) {
      newFormals.add(deep? ((FormalParameter) i.next()).deepCopy() :i.next());
    }
    if (isConstructor()) {
      mn = new MethodNode(accessFlags.copy(),
        name, newFormals, 
			  deep ? Node.deepCopyList(exceptions) : exceptions,
			  deep ? (BlockStatement) body.deepCopy() : body);
      mn.additionalDimensions = additionalDimensions;
    } else {
      mn = new MethodNode(accessFlags.copy(),
			  deep ? (TypeNode)returnType.deepCopy() :returnType,
			  name,
			  newFormals,
			  deep ? Node.deepCopyList(exceptions) : exceptions,
			  deep ? (BlockStatement) body.deepCopy() : body);
      mn.additionalDimensions = additionalDimensions;
    }
    return mn;
  }

  private boolean isConstructor;
  private AccessFlags accessFlags;
  private TypeNode returnType;
  private String name;
  private List formals;
  private List exceptions;
  private BlockStatement body;
  private int additionalDimensions;
  private MethodTypeInstance mtiThis; 
}
