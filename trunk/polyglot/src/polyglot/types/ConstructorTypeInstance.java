/*
 * ConstructorType.java
 */

package jltools.types;

import java.util.List;

/**
 * ConstructorTypeInstance
 *
 * Overview:
 *    An instance of a particular method type.  Contains additional info such as accessflags.
 *    A ConstructorType represents the immutable typing information
 *    associated with a Java constructor.
 *
 **/
public class ConstructorTypeInstance extends MethodTypeInstance {

  /**
   *    ExceptionTypes and AccessFlags may be null.
   **/
  public ConstructorTypeInstance(TypeSystem ts, 
                         List argumentTypes,
			 List exceptionTypes,
			 AccessFlags flags) {
    super(ts, "[Constructor]", null, argumentTypes, exceptionTypes, flags);
  }
}
