/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.util.*;

/**
 * An <code>ArrayType</code> represents an array of base java types.
 */
public class ArrayType_c extends ReferenceType_c implements ArrayType
{
    protected Ref<? extends Type> base;
    protected List<FieldDef> fields;
    protected List<MethodDef> methods;
    protected List<Ref<? extends Type>> interfaces;

    /** Used for deserializing types. */
    protected ArrayType_c() { }

    public ArrayType_c(TypeSystem ts, Position pos, Ref<? extends Type> base) {
	super(ts, pos);
	this.base = base;

        methods = null;
        fields = null;
        interfaces = null;
    }

    protected void init() {
        if (methods == null) {
            methods = new ArrayList<MethodDef>(1);

            // Add method public Object clone()
            MethodDef mi = ts.methodDef(position(),
                                          Ref_c.<ArrayType_c>ref(this),
                                          ts.Public(),
                                          Ref_c.<ClassType>ref(ts.Object()),
                                          "clone",
                                          Collections.EMPTY_LIST,
                                          Collections.EMPTY_LIST);
            methods.add(mi);
        }

        if (fields == null) {
            fields = new ArrayList<FieldDef>(1);

            // Add field public final int length
            FieldDef fi = ts.fieldDef(position(),
                                        Ref_c.<ArrayType_c>ref(this),
                                        ts.Public().Final(),
                                        Ref_c.<PrimitiveType>ref(ts.Int()),
                                        "length");
            fi.setNotConstant();
            fields.add(fi);
        }

        if (interfaces == null) {
            interfaces = new ArrayList<Ref<? extends Type>>(2);
            interfaces.add(Ref_c.<ClassType>ref(ts.Cloneable()));
            interfaces.add(Ref_c.<ClassType>ref(ts.Serializable()));
        }
    }

    public Ref<? extends Type> theBaseType() {
        return base;
    }
    
    /** Get the base type of the array. */
    public Type base() {
        return get(base);
    }

    /** Set the base type of the array. */
    public ArrayType base(Type base) {
        return base(Ref_c.ref(base));
    }
    
    public ArrayType base(Ref<? extends Type> base) {
        if (base == this.base)
            return this;
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
        return base.toString() + "[]";
    }

    public void print(CodeWriter w) {
	base().print(w);
	w.write("[]");
    }

    /** Translate the type. */
    public String translate(Resolver c) {
        return base().translate(c) + "[]"; 
    }

    public boolean isArray() { return true; }
    public ArrayType toArray() { return this; }

    /** Get the methods implemented by the array type. */
    public List<MethodInstance> methods() {
        init();
        return new TransformingList<MethodDef,MethodInstance>(methods, new MethodAsTypeTransform());
    }

    /** Get the fields of the array type. */
    public List<FieldInstance> fields() {
        init();
        return new TransformingList<FieldDef,FieldInstance>(fields, new FieldAsTypeTransform());
    }

    /** Get the clone() method. */
    public MethodInstance cloneMethod() {
	return methods().get(0);
    }

    /** Get a field of the type by name. */
    public FieldInstance fieldNamed(String name) {
        FieldInstance fi = lengthField();
        return name.equals(fi.name()) ? fi : null;
    }

    /** Get the length field. */
    public FieldInstance lengthField() {
	return fields().get(0);
    }

    /** Get the super type of the array type. */
    public Type superType() {
	return ts.Object();
    }

    /** Get the interfaces implemented by the array type. */
    public List interfaces() {
        init();
	return Collections.unmodifiableList(interfaces);
    }

    public int hashCode() {
	return base().hashCode() << 1;
    }

    public boolean equalsImpl(TypeObject t) {
        if (t instanceof ArrayType) {
            ArrayType a = (ArrayType) t;
            return ts.equals(base(), a.base());
        }
	return false;
    }

    public boolean typeEquals(Type t) {
        if (t instanceof ArrayType) {
            ArrayType a = (ArrayType) t;
            return ts.typeEquals(base(), a.base());
        }
	return false;
    }

    public boolean isImplicitCastValid(Type toType) {
        if (toType.isArray()) {
            Type fromBase = base();
            Type toBase = toType.toArray().base();
            if (fromBase.isPrimitive() || toBase.isPrimitive()) {
                return ts.typeEquals(fromBase, toBase);
            }
            else {
                return ts.isImplicitCastValid(fromBase, toBase);
            }
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
    public boolean isCastValid(Type toType) {
        if (! toType.isReference()) return false;

	if (toType.isArray()) {
	    Type fromBase = base();
	    Type toBase = toType.toArray().base();

	    if (fromBase.isPrimitive()) return ts.typeEquals(toBase, fromBase);
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
