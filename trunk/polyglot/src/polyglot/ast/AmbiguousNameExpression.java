/*
 * AmbiguousNameExpression.java
 */

package jltools.ast;

import jltools.util.TypedList;
import java.util.List;
import java.util.Enumeration;
import java.util.ArrayList;
import java.util.Iterator;
import jltools.util.CodeWriter;
import jltools.types.Context;

/**
 * AmbiguousNameExpression
 *
 * Overview: An AmbiguousNameExpression represents an ambiguous
 *    expression composed of a series of period-separated identifiers.
 *
 * Notes: In standard Java, an AmbiguousNameExpression will be one of:
 *     -- field-name{.nonstatic-field-name}*
 *     -- class-name.static-field-name{.nonstatic-field-name}*....
 *
 * Since we can identify locals at parse-time, we make it invariant
 * that the first component of an AmbiguousNameExpression is _not_ a local.
 *
 * In order to resolve the ambiguity, the spec requires that we inspect the
 * first identifier to determine whether it's a field.  If not, we look for
 * the longest possible prefix that's a class name.
 **/
public class AmbiguousNameExpression extends AmbiguousExpression {
  /**
   * Checks: lst has at least one element, and every element of lst is a
   *   String which contains no periods.
   * Effects: creates a new AmbiguousNameExpression for the names in lst.
   **/
  public AmbiguousNameExpression(List lst) {
    names = TypedList.copyAndCheck(lst, String.class, false);
    if (lst.size() < 1) throw new Error();    
  }

  /**
   * Requires: strng is not empty, and does not begin or end with a '.'
   * Effects: creates a new AmbiguousNameExpression for the identifier in
   *   <strng>
   */
  public AmbiguousNameExpression(String strng) {
    names = new TypedList(new ArrayList(4), String.class, false);
    Enumeration enum = new java.util.StringTokenizer(strng, ".");
    while (enum.hasMoreElements())
      names.add(enum.nextElement());
  }

  /**
   * Returns a mutable TypedList of the identifiers in this
   * AmbiguousExpression.
   **/   
  public TypedList getIdentifiers() {
    return names;
  }

  public Node copy() {
    AmbiguousNameExpression ane = new AmbiguousNameExpression(names);
    ane.copyAnnotationsFrom(this);
    return ane;
  }

  public Node deepCopy() {
    return copy();
  }

  public void translate ( Context c, CodeWriter w)
  {
    // FIXME: Shouldn't get in here.
    w.write ("< AMBIGOUS NAME: ");
    dump(c, w);
    w.write ( "> ");
  }
  
  public void dump (Context c, CodeWriter w)
  {
    w.write ("( AMBIGOUS NAME ");
    dumpNodeInfo(c, w);
    for (Iterator i = names.listIterator(); i.hasNext(); )
    {
      w.write ("(" + (String)i.next() + ")");
    }
    w.write ( " ) ");
  }

  public Node typeCheck(Context c)
  {
    // FIXME: implement;
    return this;
  }

  public void visitChildren(NodeVisitor v) { }

  TypedList names;
}
