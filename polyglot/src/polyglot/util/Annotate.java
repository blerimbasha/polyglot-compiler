/*
 * Annotate.java
 */

package jltools.util;

import jltools.types.Type;

import java.util.*;

/**
 * Annotate
 *
 * Overview:
 *     This class contains the constants and methods used to wrap accesses
 *     to AnnotatedObject's methods.
 **/
public class Annotate {

  static final int LINE_NUMBER            = 1;
  static final int CHECKED_TYPE           = 2;

  static final int THROWS_SET             = 4;
  // true if the node has all paths ending in function termination (either throws or return).
  static final int EXPECTED_TYPE          = 5;
  static final int COMPLETES_NORMALLY     = 6;
  static final int IS_REACHABLE           = 7;
  
  // True for (PolyJ) expressions that are children of an ExpressionStatement
  static final int IS_EXPR_STATEMENT      = 8; 

    

  /**
   * Notes that o appeared at line i of the source.
   **/
  public static void setLineNumber(AnnotatedObject o, int i) {
    o.setAnnotation(LINE_NUMBER, new Integer(i));
  }

  /**
   * Gets the line number for o. (-1 for none.
   **/
  public static int getLineNumber(AnnotatedObject o) {
    Integer i = (Integer) o.getAnnotation(LINE_NUMBER);
    if (i == null) 
      return -1;
    return i.intValue();
  }

  /**
   * Sets the checked type of an object.
   **/
  public static void setCheckedType(AnnotatedObject o, Type t) {
    o.setAnnotation(CHECKED_TYPE, t);
  }

  /**
   * Returns the checked type of an object -- null if not set.
   **/
  public static Type getCheckedType(AnnotatedObject o) {
    return (Type) o.getAnnotation(CHECKED_TYPE);
  }

  /**
   * Sets the checked type of an object.
   **/
  public static void setExpectedType(AnnotatedObject o, Type t) {
    o.setAnnotation(EXPECTED_TYPE, t);
  }

  /**
   * Returns the checked type of an object -- null if not set.
   **/
  public static Type getExpectedType(AnnotatedObject o) {
    return (Type) o.getAnnotation(EXPECTED_TYPE);
  }
  /*
  public static void addThrows( AnnotatedObject o, Type t)
  {
    SubtypeSet s = (SubtypeSet)o.getAnnotation(THROWS_SET);
    if ( s == null)
    {
      s = new SubtypeSet();
      o.setAnnotation( THROWS_SET, s);
    }
    s.add ( t );
  }

  public static void addThrows( AnnotatedObject o, Collection c)
  {
    SubtypeSet s = (SubtypeSet)o.getAnnotation(THROWS_SET);
    if ( s == null)
    {
      s = new SubtypeSet();
      o.setAnnotation( THROWS_SET, s);
    }
    s.addAll ( c );
  }

  public static SubtypeSet getThrows(AnnotatedObject o) 
  {
    return (SubtypeSet) o.getAnnotation(THROWS_SET);
  }
  
  public static boolean completesNormally(AnnotatedObject o )
  {
    Boolean b = ((Boolean)o.getAnnotation(COMPLETES_NORMALLY));
    if (b == null) 
      return false;
    return b.booleanValue();
  }

  public static void setCompletesNormally( AnnotatedObject o, boolean b )
  {
    o.setAnnotation( COMPLETES_NORMALLY, new Boolean (b ) );
  }

  public static boolean isReachable(AnnotatedObject o)
  {
    Boolean b = ((Boolean)o.getAnnotation(IS_REACHABLE));
    if (b == null) 
      return false;
    return b.booleanValue();
  }

  public static void setReachable(AnnotatedObject o, boolean b)
  {
    o.setAnnotation( IS_REACHABLE, new Boolean (b ) );
  }
  */


  /**
   * Label an Expression as being part of an ExpressionStatement,
   * meaning the result of the expression is not used
   * (Only used by PolyJ)
   **/
  public static void setStatementExpr(AnnotatedObject o, boolean b) {
    o.setAnnotation(IS_EXPR_STATEMENT, new Boolean(b));
  }

  public static boolean isStatementExpr(AnnotatedObject o) {
    Boolean b = (Boolean) o.getAnnotation(IS_EXPR_STATEMENT);
    if (b == null) 
      return false;    
    return b.booleanValue();
  }

  // Never instantiate this class.
  private Annotate() {}
}


