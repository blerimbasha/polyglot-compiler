/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 * 
 */

package polyglot.ast;

import java.util.Collections;
import java.util.List;

import polyglot.types.*;
import polyglot.util.*;
import polyglot.visit.*;

/**
 * A <code>Field</code> is an immutable representation of a Java field
 * access.  It consists of field name and may also have either a 
 * <code>Type</code> or an <code>Expr</code> containing the field being 
 * accessed.
 */
public class Field_c extends Expr_c implements Field
{
  protected Receiver target;
  protected Id name;
  protected FieldInstance fi;
  protected boolean targetImplicit;
  
  public Field_c(Position pos, Receiver target, Id name) {
    super(pos);
    assert(target != null && name != null);
    this.target = target;
    this.name = name;
    this.targetImplicit = false;
  }

  /** Get the precedence of the field. */
  public Precedence precedence() { 
    return Precedence.LITERAL;
  }

  /** Get the target of the field. */
  public Receiver target() {
    return this.target;
  }

  /** Set the target of the field. */
  public Field target(Receiver target) {
    Field_c n = (Field_c) copy();
    n.target = target;
    return n;
  }
  
  /** Get the name of the field. */
  public Id id() {
      return this.name;
  }
  
  /** Set the name of the field. */
  public Field id(Id name) {
      Field_c n = (Field_c) copy();
      n.name = name;
      return n;
  }

  /** Get the name of the field. */
  public String name() {
    return this.name.id();
  }

  /** Set the name of the field. */
  public Field name(String name) {
      return id(this.name.id(name));
  }

  /** Return the access flags of the variable. */
  public Flags flags() {
    return fi.flags();
  }

  /** Get the field instance of the field. */
  public VarType varInstance() {
    return fi;
  }

  /** Get the field instance of the field. */
  public FieldInstance fieldInstance() {
    return fi;
  }

  /** Set the field instance of the field. */
  public Field fieldInstance(FieldInstance fi) {
    if (fi == this.fi) return this;
    Field_c n = (Field_c) copy();
    n.fi = fi;
    return n;
  }

  public boolean isTargetImplicit() {
      return this.targetImplicit;
  }

  public Field targetImplicit(boolean implicit) {
      Field_c n = (Field_c) copy();
      n.targetImplicit = implicit;
      return n;
  }

  /** Reconstruct the field. */
  protected Field_c reconstruct(Receiver target, Id name) {
    if (target != this.target || name != this.name) {
      Field_c n = (Field_c) copy();
      n.target = target;
      n.name = name;
      return n;
    }

    return this;
  }

  /** Visit the children of the field. */
  public Node visitChildren(NodeVisitor v) {
    Receiver target = (Receiver) visitChild(this.target, v);
    Id name = (Id) visitChild(this.name, v);
    return reconstruct(target, name);
  }

  public Node buildTypes(TypeBuilder tb) throws SemanticException {
      Field_c n = (Field_c) super.buildTypes(tb);

      TypeSystem ts = tb.typeSystem();

      FieldInstance fi = new FieldInstance_c(ts, position(), new ErrorRef_c<FieldDef>(ts, position()));
      return n.fieldInstance(fi);
  }

  /** Type check the field. */
  public Node typeCheck(TypeChecker tc) throws SemanticException {
      Context c = tc.context();
      TypeSystem ts = tc.typeSystem();
      
      if (target.type().isReference()) {
	  FieldInstance fi = ts.findField(target.type().toReference(), name.id(), c.currentClassScope());
	  
	  if (fi == null) {
	      throw new InternalCompilerError("Cannot access field on node of type " +
	                                      target.getClass().getName() + ".");
	  }
	  
	  Field_c f = (Field_c) fieldInstance(fi).type(fi.type());  
	  f.checkConsistency(c);
	  
	  return f; 
      }

      throw new SemanticException("Cannot access field \"" + name.id() +
                                  "\" " + (target instanceof Expr
                                          ? "on an expression "
                                                  : "") +
                                                  "of non-reference type \"" +
                                                  target.type() + "\".", target.position());
  }
  
  public Node checkConstants(TypeChecker tc) throws SemanticException {
      // Just check if the field is constant to force a dependency to be
      // created.
      isConstant();
      return this;
  }
  
  public Type childExpectedType(Expr child, AscriptionVisitor av)
  {
      if (child == target) {
          return fi.container();
      }

      return child.type();
  }

  /** Write the field to an output file. */
  public void prettyPrint(CodeWriter w, PrettyPrinter tr) {
    w.begin(0);
    if (!targetImplicit) {
        // explicit target.
        if (target instanceof Expr) {
          printSubExpr((Expr) target, w, tr);
        }
        else if (target instanceof TypeNode || target instanceof AmbReceiver) {
          print(target, w, tr);
        }
    
        w.write(".");
	w.allowBreak(2, 3, "", 0);
    }
    tr.print(this, name, w);
    w.end();
  }

  public void dump(CodeWriter w) {
    super.dump(w);

    if (fi != null) {                       
        w.allowBreak(4, " ");
        w.begin(0);
        w.write("(instance " + fi + ")");   
        w.end();                            
    }                                       

    w.allowBreak(4, " ");
    w.begin(0);
    w.write("(name \"" + name + "\")");
    w.end();
  }

  public Term firstChild() {
      if (target instanceof Term) {
          return (Term) target;
      }
      
      return null;
  }

  public List<Term> acceptCFG(CFGBuilder v, List<Term> succs) {
      if (target instanceof Term) {
          v.visitCFG((Term) target, this, EXIT);
      }
      
      return succs;
  }


  public String toString() {
    return (target != null ? target + "." : "") + name;
  }


  public List<Type> throwTypes(TypeSystem ts) {
      if (target instanceof Expr && ! (target instanceof Special)) {
          return Collections.<Type>singletonList(ts.NullPointerException());
      }

      return Collections.EMPTY_LIST;
  }

  public boolean constantValueSet() {
      if (fi != null &&
              (target instanceof TypeNode ||
                      (target instanceof Special && targetImplicit))) {
          return fi.constantValueSet();
      }
      return fi != null;
  }
  
  public boolean isConstant() {
      if (fi != null &&
              (target instanceof TypeNode ||
                      (target instanceof Special && targetImplicit))) {
          return fi.isConstant();
      }

      return false;
  }

  public Object constantValue() {
    if (isConstant()) {
      return fi.constantValue();
    }

    return null;
  }
  
  /**
   * Check the consistency of the implicit target inserted by the compiler by
   * asserting that the FieldInstance in the Context for this field's name is
   * the same as the FieldInstance we assigned to this field.
   */
  protected void checkConsistency(Context c) {
      if (targetImplicit) {
          VarType vi = c.findVariableSilent(name.id());
          if (vi instanceof FieldInstance) {
              FieldInstance rfi = (FieldInstance) vi;
              // Compare the original (declaration) fis, not the actuals.
              // We do this because some extensions that do type substitutions
              // perform the substitution
              // on the fi after lookup and some extensions modify lookup
              // itself to do the substitution.
              if (c.typeSystem().equals(rfi.def(), fi.def())) {
                  // all is OK.
                  return;
              }
              System.out.println("(found) rfi is " + rfi.def());
              System.out.println("(actual) fi is " + fi.def());
          }
          throw new InternalCompilerError("Field " + this + " has an " +
               "implicit target, but the name " + name.id() + " resolves to " +
               vi + " instead of " + target, position());
      }      
  }

  public Node copy(NodeFactory nf) {
      return nf.Field(this.position, this.target, this.name);
  }

}
