/*
 * TypeSystem.java
 */

package jltools.types;

import java.util.Iterator;
import java.util.List;

/**
 * TypeSystem
 *
 * Overview:
 *    A TypeSystem represents a universe of types.  It is responsible for
 *    finding classes to correspond to types, determining relations between
 *    types, and so forth.
 *
 **/
public abstract class TypeSystem {

  /**
   * This class represents the context in which a type lookup is
   * proceeding.
   *
   * The 'ImportTable' field is required for type lookups (like checkType and
   * getCanonicalType).  
   *
   * The Type field is required for all method and field lookups.
   *
   * The MethodType field may be null.
   **/
  public static class Context {
    public final ImportTable table;
    public final Type inClass;
    public final MethodType inMethod;
    
    public Context(ImportTable t, Type type, MethodType m) 
      { table = t; inClass = type; inMethod = m; }
    public Context(Type type, MethodType m) 
      { table = null; inClass = type; inMethod = m; }
  }

  /**
   * This class represents the <Type, methodType> pair of a method lookup.
   **/
  public static class MethodMatch {
    public final Type onClass;
    public final MethodTypeInstance method;
    public String error;

    public MethodMatch(Type c, MethodTypeInstance m) { onClass = c; method = m; }    
    public MethodMatch(String error) { this(null, null); this.error = error; }
  }

  /**
   * This class represents the <Type, fieldType> pair of a field lookup.
   **/
  public static class FieldMatch {
    public final Type onClass;
    public final FieldInstance field;
    public String error;

    public FieldMatch(Type c, FieldInstance f) { onClass = c; field = f; }
  }

  ////
  // Functions for two-type comparison.
  ////
  /**
   * Requires: all type arguments are canonical.
   *
   * Returns true iff childClass is not ancestorClass, but childClass descends
   * from ancestorClass.
   **/
  public abstract boolean descendsFrom(JavaClass childClass, 
				       JavaClass ancestorClass);
  /**
   * Requires: all type arguments are canonical.
   *
   * Returns true iff childType and ancestorType are distinct
   * ClassTypes, and childType descends from * ancestorType.
   **/
  public abstract boolean descendsFrom(Type childType, 
				       Type ancestorType);
  /**
   * Requires: all type arguments are canonical.
   *
   * Returns true iff childType and ancestorType are non-primitive
   * types, and a variable of type childType may be legally assigned
   * to a variable of type ancestorType.
   **/
  public abstract boolean isAssignableSubtype(Type childType, 
					      Type ancestorType);
  /**
   * Requires: all type arguments are canonical.
   *
   * Returns true iff a cast from fromType to toType is valid; in other
   * words, some non-null members of fromType are also members of toType.
   **/
  public abstract boolean isCastValid(Type fromType, Type toType);


  /**
   * Requires: all type arguments are canonical.
   *
   * Returns true iff an implicit cast from fromType to toType is valid;
   * in other wors, every member of fromType is member of toType.
   **/
  public abstract boolean isImplicitCastValid(Type fromType, Type toType);

  /**
   * Requires: all type arguments are canonical.
   *
   * Returns true iff type1 and type2 are the same type.
   **/
  public abstract boolean isSameType(Type type1, Type type2);

  ////
  // Functions for one-type checking and resolution.
  ////
  
  /**
   * Returns true iff <type> is a canonical (fully qualified) type.
   **/
  public abstract boolean isCanonical(Type type);
  /**
   * Tries to return the canonical (fully qualified) form of <type> in
   * the provided context, which may be null.  Returns null if no such
   * type exists.
   **/
  public Type getCanonicalType(Type type, Context context) {
    try { return checkAndResolveType(type, context); }
    catch (TypeCheckError tce )
    { return null; }
  }

  /**
   * Checks whether a method or field within tEnclosingClass with access flags 'flags' can
   * be accessed from Context context. 
   */
  public abstract boolean isAccessible(ClassType tEnclosingClass, AccessFlags flags, Context context);

  /**
   * Checks whether <type> is a valid type in the given context,
   * which may be null.  Returns a description of the error, if any.
   **/
  public String checkTypeOk(Type type, Context context) {
    Object res = checkAndResolveType(type, context);
    return (res instanceof String) ? (String) res : null;
  }
  /**
   * If <type> is a valid type in the given context, returns a
   * canonical form of that type.  Otherwise, returns a String
   * describing the error.
   **/
  public abstract Type checkAndResolveType(Type type, Context context);

  ////
  // Various one-type predicates.
  ////
  /**
   * Requires: all type arguments are canonical.
   *
   * Returns true iff an object of type <type> may be thrown.
   **/
  public abstract boolean isThrowable(Type type);
  /**
   * Requires: all type arguments are canonical.
   *
   * Returns true iff an object of type <type> may be thrown by a method
   * without being declared in its 'throws' clause.
   **/
  public abstract boolean isUncheckedException(Type type);  

  ////
  // Functions for type membership.
  ////
  /**
   * Requires: all type arguments are canonical.
   *
   * Returns an immutable iterator of all the FieldMatches defined on
   * type (if any).  The iterator is guaranteed to yeild fields
   * defined on subclasses before those defined on superclasses.
   **/
  public abstract Iterator getFieldsForType(Type type);

  /**
   * Requires: all type arguments are canonical.
   *
   * Returns an immutable iterator of all the MethodMatches defined on
   * type (if any).  The iterator is guaranteed to yield methods
   * defined on subclasses before those defined on superclasses.
   **/  
  public abstract Iterator getMethodsForType(Type type);

  /**
   * Requires: all type arguments are canonical.
   *
   * Returns an immutable iterator of all the FieldMatches named 'name' defined
   * on type (if any).  If 'name' is null, matches all.  The iterator is guaranteed 
   * to yield fields defined on subclasses before those defined on superclasses.
   **/
  public abstract Iterator getFieldsNamed(Type type, String name);

  /**
   * Requries all type are canonical.
   * 
   * Returns an immutable iterator of all the MethodMatches named 'name' defined
   * on type (if any).  If 'name' is null, mathces all. The iterator is guaranteed
   * to yield methods defined on subclasses before those defined on superclasses.
   **/
  public abstract Iterator getMethodsNamed(Type type, String name);

  /**
   * Requires: all type arguments are canonical.
   *
   * Returns the fieldMatch named 'name' defined on 'type' visible in
   * context.  If no such field may be found, returns a fieldmatch
   * with an error explaining why. Considers accessflags
   **/
  public abstract Iterator getField(Type type, String name, Context context);

  /**
   * Requires: all type arguments are canonical.
   * 
   * Returns the MethodMatch named 'name' defined on 'type' visibile in
   * context.  If no such field may be found, returns a fieldmatch
   * with an error explaining why. Considers accessflags.
   **/
  public abstract Iterator getMethod(Type type, String name, Context context);
 

  /**
   * Requires: all type arguments are canonical.
   *
   * Returns the supertype of type, or null if type has no supertype.
   **/
  public abstract Type getSuperType(Type type);
  /**
   * Requires: all type arguments are canonical.
   *
   * Returns an immutable list of all the interface types which type
   * implements.
   **/
  public abstract List getInterfaces(Type type);

  ////
  // Functions for method testing.
  ////
  /**
   * Returns true iff <type1> is the same as <type2>.
   **/
  public abstract boolean isSameType(MethodType type1, MethodType type2);
  /**
   * Returns true iff <type1> has the same arguments as <type2>
   **/
  public abstract boolean hasSameArguments(MethodType type1, MethodType type2);
  /**
   * If an attempt to call a method of type <method> on <type> would
   * be successful, returns the actual MethodMatch for the method that
   * would be called.  Otherwise returns a MethodMatch with an error string
   * explaining why no method could be found.
   *
   * If <context> is non-null, only those methods visible in context are
   * considered.
   *
   * Iff <isThis> is true, methods are considered which would only be valid
   * if the target object were equal to the "this" object.
   *
   * This method uses the name, argument types, and access flags of <method>.
   * The access flags are used to select which protections may be accepted.
   *
   * (Guavac gets this wrong.)
   **/
  public abstract MethodMatch getMethod(Type type, MethodType method, 
					Context context, boolean isThis);
  /**
   * If an attempt to call a method of type <method> on <type> would
   * be successful, and the method would match on the given <type>,
   * returns the actual MethodMatch for the method that would be
   * called.  Otherwise returns a MethodMatch with an error string
   * explaining why no method could be found.
   *
   * If <context> is non-null, only those methods visible in context are
   * considered.
   *
   * Iff <isThis> is true, methods are considered which would only be valid
   * if the target object were equal to the "this" object.
   *
   * This method uses the name, argument types, and access flags of <method>.
   * The access flags are used to select which protections may be accepted.
   *
   * (Guavac gets this wrong.)
   **/
  public abstract MethodMatch getMethodInClass(Type type, MethodType method, 
					      Context context, boolean isThis);
  /**
   * As above, except only returns a match if the argument types are identical,
   * and disregards context.
   **/
  public abstract MethodMatch getExactMethod(Type type, MethodType method);
  public abstract MethodMatch getExactMethodInClass(Type type, MethodType method); 

  ////
  // Functions for type->class mapping.
  ////
  /**
   * Returns the JavaClass object corresponding to a given type, or null
   * if there is none.
   **/
  public abstract JavaClass getClassForType(Type type);

  ////
  // Functions which yield particular types.
  ////
  public abstract Type getNull();
  public abstract Type getVoid();
  public abstract Type getBoolean();
  public abstract Type getChar();
  public abstract Type getByte();
  public abstract Type getShort();
  public abstract Type getInt();
  public abstract Type getLong();
  public abstract Type getFloat();
  public abstract Type getDouble();
  public abstract Type getObject();
  public abstract Type getThrowable();
  /**
   * Returns a non-canonical type object for a class type whose name
   * is the provided string.  This type may not correspond to a valid
   * class.
   **/
  public abstract ClassType getTypeWithName(String name);
  /**
   * Returns a type identical to <type>, but with <dims> more array
   * dimensions.  If dims is < 0, array dimensions are stripped.
   **/
  public abstract Type extendArrayDims(Type type, int dims);
  /**
   * Returns a canonical type corresponding to the Java Class object
   * theClass.  Does not require that <theClass> have a JavaClass
   * registered in this typeSystem.  Does not register the type in
   * this TypeSystem.  For use only by JavaClass implementations.
   **/
  public abstract ClassType typeForClass(Class theClass);

  /**
   * Given the name for a class, returns the portion which appears to
   * constitute the package -- i.e., all characters up to but not including
   * the last dot, or no characters if the name has no dot.
   **/
  public static String getPackageComponent(String fullName) {
    int lastDot = fullName.lastIndexOf('.');
    return lastDot >= 0 ? fullName.substring(0,lastDot) : "";
  }
 
  /**
   * Given the name for a class, returns the portion which appears to
   * constitute the package -- i.e., all characters after the last
   * dot, or all the characters if the name has no dot.
   **/
  public static String getShortNameComponent(String fullName) {
    int lastDot = fullName.lastIndexOf('.');
    return lastDot >= 0 ? fullName.substring(lastDot+1) : fullName;
  }

  /**
   * Returns true iff the provided class name does not appear to be
   * qualified (i.e., it has no dot.)
   **/
  public static boolean isNameShort(String name) {
    return name.indexOf('.') < 0;
  }

  public static String getFirstComponent(String fullName) {
    int firstDot = fullName.indexOf('.');
    return firstDot >= 0 ? fullName.substring(0,firstDot-1) : fullName;
  }

  public static String removeFirstComponent(String fullName) {
    int firstDot = fullName.indexOf('.');
    return firstDot >= 0 ? fullName.substring(firstDot+1) : "";
  }
}

