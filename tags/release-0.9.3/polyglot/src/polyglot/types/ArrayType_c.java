package polyglot.ext.jl.types;

import polyglot.types.*;
import polyglot.util.*;
import java.util.*;

/**
 * An <code>ArrayType</code> represents an array of base java types.
 */
public class ArrayType_c extends ReferenceType_c implements ArrayType
{
    protected Type base;
    protected List fields;
    protected List methods;
    protected List interfaces;

    /** Used for deserializing types. */
    protected ArrayType_c() { }

    public ArrayType_c(TypeSystem ts, Position pos, Type base) {
	super(ts, pos);
	this.base = base;

	methods = new ArrayList(1);
	fields = new ArrayList(2);
	interfaces = new ArrayList(2);

	// Add method public Object clone()
	methods.add(ts.methodInstance(position(),
				      this,
	                              Flags.PUBLIC,
				      ts.Object(),
	                              "clone",
				      Collections.EMPTY_LIST,
				      Collections.EMPTY_LIST));

	// Add field public final int length
	fields.add(ts.fieldInstance(position(),
	                            this,
				    Flags.PUBLIC.set(Flags.FINAL),
				    ts.Int(),
				    "length"));

	// Add field public static final Class class
	fields.add(ts.fieldInstance(position(),
	                            this,
				    Flags.PUBLIC.set(Flags.STATIC).set(Flags.FINAL),
				    ts.Class(),
				    "class"));

	interfaces.add(ts.Cloneable());
	interfaces.add(ts.Serializable());
    }

    /** Get the base type of the array. */
    public Type base() {
        return base;
    }

    /** Set the base type of the array. */
    public ArrayType base(Type base) {
	ArrayType_c n = (ArrayType_c) copy();
	n.base = base;
	return n;
    }

    /** Get the ulitimate base type of the array. */
    public Type ultimateBase() {
        if (base().isArray()) {
            return base().toArray().ultimateBase();
        }

        return base();
    }

    public int dims() {
        return 1 + (base().isArray() ? base().toArray().dims() : 0);
    }

    public String toString() {
        return base().toString() + "[]";
    }

    /** Translate the type. */
    public String translate(Resolver c) {
        return base().translate(c) + "[]"; 
    }

    /** Returns true iff the type is canonical. */
    public boolean isCanonical() {
	return base().isCanonical();
    }

    public boolean isArray() { return true; }
    public ArrayType toArray() { return this; }

    /** Get the methods implemented by the array type. */
    public List methods() {
	return Collections.unmodifiableList(methods);
    }

    /** Get the fields of the array type. */
    public List fields() {
	return Collections.unmodifiableList(fields);
    }

    /** Get the clone() method. */
    public MethodInstance cloneMethod() {
	return (MethodInstance) methods.get(0);
    }

    /** Get a field of the type by name. */
    public FieldInstance fieldNamed(String name) {
        FieldInstance fi = lengthField();
        return name.equals(fi.name()) ? fi : null;
    }

    /** Get the length field. */
    public FieldInstance lengthField() {
	return (FieldInstance) fields.get(0);
    }

    /** Get the super type of the array type. */
    public Type superType() {
	return ts.Object();
    }

    /** Get the interfaces implemented by the array type. */
    public List interfaces() {
	return Collections.unmodifiableList(interfaces);
    }

    public int hashCode() {
	return base().hashCode() << 1;
    }

    public boolean isSameImpl(Type t) {
        if (t.isArray()) {
	    return ts.isSame(base(), t.toArray().base());
	}

	return false;
    }

    /** Restore the type after deserialization. */
    public TypeObject restore_() throws SemanticException {
	Type base = (Type) this.base.restore();

	if (base != this.base) {
	    return base(base);
	}

	return this;
    }

    public boolean isImplicitCastValidImpl(Type toType) {
        if (toType.isArray()) {
            return ts.isImplicitCastValid(base(), toType.toArray().base());
        }

        // toType is not an array, but this is.  Check if the array
        // is a subtype of the toType.  This happens when toType
        // is java.lang.Object.
        return ts.isSubtype(this, toType);
    }

    /**
     * Requires: all type arguments are canonical.  ToType is not a NullType.
     *
     * Returns true iff a cast from this to toType is valid; in other
     * words, some non-null members of this are also members of toType.
     **/
    public boolean isCastValidImpl(Type toType) {
        if (! toType.isReference()) return false;

	if (toType.isArray()) {
	    Type fromBase = base();
	    Type toBase = toType.toArray().base();

	    if (fromBase.isPrimitive()) return ts.isSame(toBase, fromBase);
	    if (toBase.isPrimitive()) return false;

	    if (fromBase.isNull()) return false;
	    if (toBase.isNull()) return false;

	    // Both are reference types.
	    return ts.isCastValid(fromBase, toBase);
	}

        // Ancestor is not an array, but child is.  Check if the array
        // is a subtype of the ancestor.  This happens when ancestor
        // is java.lang.Object.
        return ts.isSubtype(this, toType);
    }
}