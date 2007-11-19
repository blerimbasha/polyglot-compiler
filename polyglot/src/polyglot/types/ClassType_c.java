/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.frontend.Globals;
import polyglot.frontend.Job;
import polyglot.main.Options;
import polyglot.util.*;

/**
 * A <code>ClassType</code> represents a class -- either loaded from a
 * classpath, parsed from a source file, or obtained from other source.
 */
public abstract class ClassType_c extends ReferenceType_c implements ClassType
{
    Ref<ClassDef> def;
    
    /** Used for deserializing types. */
    protected ClassType_c() { }

    public ClassType_c(TypeSystem ts, Position pos, Ref<ClassDef> def) {
        super(ts, pos);
        this.def = def;
    }
    
    public ClassDef def() {
        return def.get();
    }
    
    public boolean equals(TypeObject t) {
        if (t instanceof ClassType_c) {
            Ref<ClassDef> thisDef = def;
            Ref<ClassDef> thatDef = ((ClassType_c) t).def;
            return thisDef == thatDef;
        }
        return false;
    }

    public boolean typeEquals(Type t) {
        if (t instanceof ClassType) {
            ClassDef thisDef = def();
            ClassDef thatDef = ((ClassType) t).def();
            return thisDef.equals(thatDef);
        }
        return false;
    }

    protected transient Resolver memberCache;
    
    public Resolver resolver() {
        if (memberCache == null) {
            memberCache = new CachingResolver(ts.createClassContextResolver(this));
        }
        return memberCache;
    }
    
    public Object copy() {
        ClassType_c n = (ClassType_c) super.copy();
        n.memberCache = null;
        return n;
    }
    
    public abstract Job job();
    
    /** Get the class's kind. */
    public abstract ClassDef.Kind kind();

    /** Get the class's outer class, or null if a top-level class. */
    public abstract ClassType outer();

    /** Get the short name of the class, if possible. */ 
    public abstract String name();

    /** Get the container class if a member class. */
    public ReferenceType container() {
        if (! isMember())
            throw new InternalCompilerError("Non-member class " + this + " cannot have container classes.");
        if (outer() == null)
            throw new InternalCompilerError("Nested class " + this + " must have an outer class.");
        return outer();
    }

    /** Get the full name of the class, if possible. */
    public String fullName() {
        if (isAnonymous()) {
            return toString();
        }
        String name = name();
        if (isTopLevel() && package_() != null) {
            return package_().fullName() + "." + name;
        }
        else if (isMember() && container() instanceof Named) {
            return ((Named) container()).fullName() + "." + name;
        }
        else {
            return name;
        }
    }

    public boolean isTopLevel() { return kind() == ClassDef.TOP_LEVEL; }
    public boolean isMember() { return kind() == ClassDef.MEMBER; }
    public boolean isLocal() { return kind() == ClassDef.LOCAL; }
    public boolean isAnonymous() { return kind() == ClassDef.ANONYMOUS; }

    public boolean isNested() {
        // Implement this way rather than with ! isTopLevel() so that
        // extensions can add more kinds.
        return kind() == ClassDef.MEMBER || kind() == ClassDef.LOCAL || kind() == ClassDef.ANONYMOUS;
    }
    
    public boolean isInnerClass() {
        // it's an inner class if it is not an interface, it is a nested
        // class, and it is not explicitly or implicitly static. 
        return !flags().isInterface() && isNested() && !flags().isStatic() && !inStaticContext();
    }
    
    public boolean isClass() { return true; }
    public ClassType toClass() { return this; }

    /** Get the class's package. */
    public abstract Package package_();

    /** Get the class's flags. */
    public abstract Flags flags();

    /** Get the class's constructors. */
    public abstract List<ConstructorInstance> constructors();

    /** Get the class's member classes. */
    public abstract List<Type> memberClasses();

    /** Get the class's methods. */
    public abstract List<MethodInstance> methods();

    /** Get the class's fields. */
    public abstract List<FieldInstance> fields();

    /** Get the class's interfaces. */
    public abstract List<Type> interfaces();

    /** Get the class's super type. */
    public abstract Type superType();
    
    /** Get a list of all the class's MemberInstances. */
    public List<MemberInstance> members() {
        List l = new ArrayList();
        l.addAll(methods());
        l.addAll(fields());
        l.addAll(constructors());
        l.addAll(memberClasses());
        return l;
    }

    /** Get a field of the class by name. */
    public FieldInstance fieldNamed(String name) {
        for (Iterator i = fields().iterator(); i.hasNext(); ) {
	    FieldInstance fi = (FieldInstance) i.next();
	    if (fi.name().equals(name)) {
	        return fi;
	    }
	}

	return null;
    }

    /** Get a member class of the class by name. */
    public ClassType memberClassNamed(String name) {
        for (Iterator i = memberClasses().iterator(); i.hasNext(); ) {
	    ClassType t = (ClassType) i.next();
	    if (t.name().equals(name)) {
	        return t;
	    }
	}

	return null;
    }

    public boolean descendsFrom(Type ancestor) {
        if (ancestor.isNull()) {
            return false;
        }

        if (ts.typeEquals(this, ancestor)) {
            return false;
        }

        if (! ancestor.isReference()) {
            return false;
        }

        if (ts.typeEquals(ancestor, ts.Object())) {
            return true;
        }

        // Check subtype relation for classes.
        if (! flags().isInterface()) {
            if (ts.typeEquals(this, ts.Object())) {
                return false;
            }

            if (superType() == null) {
                return false;
            }

            if (ts.isSubtype(superType(), ancestor)) {
                return true;
            }
        }

        // Next check interfaces.
        for (Iterator<Type> i = interfaces().iterator(); i.hasNext(); ) {
            Type parentType = i.next();

            if (ts.isSubtype(parentType, ancestor)) {
                return true;
            }
        }

        return false;
    }

    public boolean isThrowable() {
        return ts.isSubtype(this, ts.Throwable());
    }

    public boolean isUncheckedException() {
        if (isThrowable()) {
            Collection c = ts.uncheckedExceptions();
                                  
            for (Iterator i = c.iterator(); i.hasNext(); ) {
                Type t = (Type) i.next();

                if (ts.isSubtype(this, t)) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean isImplicitCastValid(Type toType) {
        if (! toType.isClass()) return false;
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
	    // From type is not an array, but to type is.  Check if the array
	    // is a subtype of the from type.  This happens when from type
	    // is java.lang.Object.
	    return ts.isSubtype(toType, this);
	}

	// Both types should be classes now.
	if (! toType.isClass()) return false;

	// From and to are neither primitive nor an array. They are distinct.
	boolean fromInterface = flags().isInterface();
	boolean toInterface   = toType.toClass().flags().isInterface();
	boolean fromFinal     = flags().isFinal();
	boolean toFinal       = toType.toClass().flags().isFinal();

	// This is taken from Section 5.5 of the JLS.
	if (! fromInterface) {
	    // From is not an interface.
	    if (! toInterface) {
		// Nether from nor to is an interface.
		return ts.isSubtype(this, toType) || ts.isSubtype(toType, this);
	    }

	    if (fromFinal) {
		// From is a final class, and to is an interface
		return ts.isSubtype(this, toType);
	    }

	    // From is a non-final class, and to is an interface.
	    return true;
	}
	else {
	    // From is an interface
	    if (! toInterface && ! toFinal) {
		// To is a non-final class.
		return true;
	    }

	    if (toFinal) {
		// To is a final class.
		return ts.isSubtype(toType, this);
	    }

	    // To and From are both interfaces.
	    return true;
	}
    }

    public String translate(Resolver c) {
        if (isTopLevel()) {
            if (package_() == null) {
                return name();
            }

            // Use the short name if it is unique.
            if (c != null && !Globals.Options().fully_qualified_names) {
                try {
                    Named x = c.find(name());

                    if (ts.equals(this, x)) {
                        return name();
                    }
                }
                catch (SemanticException e) {
                }
            }

            return package_().translate(c) + "." + name();
        }
        else if (isMember()) {
            // Use only the short name if the outer class is anonymous.
            if (container().toClass().isAnonymous()) {
                return name();
            }

            // Use the short name if it is unique.
            if (c != null && !Globals.Options().fully_qualified_names) {
                try {
                    Named x = c.find(name());

                    if (ts.equals(this, x)) {
                        return name();
                    }
                }
                catch (SemanticException e) {
                }
            }

            return container().translate(c) + "." + name();
        }
        else if (isLocal()) {
            return name();
        }
        else {
            throw new InternalCompilerError("Cannot translate an anonymous class.");
        }
    }

    public String toString() {
        if (isTopLevel()) {
            if (package_() != null) {
                return package_() + "." + name();
            }
            return name();
        }
        else if (isMember()) {
            return container().toString() + "." + name();
        }
        else if (isLocal()) {
            return name();
        }
        else if (isAnonymous()) {
            return "<anonymous class>";
        }
        else {
            return "<unknown class>";
        }
    }
    
    /** Pretty-print the name of this class to w. */
    public void print(CodeWriter w) {
	// XXX This code duplicates the logic of toString.
        if (isTopLevel()) {
            if (package_() != null) {
		package_().print(w);
		w.write(".");
		w.allowBreak(2, 3, "", 0);
            }
            w.write(name());
        } else if (isMember()) {
            container().print(w);
	    w.write(".");
	    w.allowBreak(2, 3, "", 0);
	    w.write(name());
        } else if (isLocal()) {
	    w.write(name());
        } else if (isAnonymous()) {
	    w.write("<anonymous class>");
        } else {
	    w.write("<unknown class>");
        }
    }

    public boolean isEnclosed(ClassType maybe_outer) {
        if (isTopLevel())
            return false;
        else if (outer() != null)
            return outer().equals(maybe_outer) ||
                  outer().isEnclosed(maybe_outer);
        else
            throw new InternalCompilerError("Non top-level classes " + 
                    "must have outer classes.");
    }

    /** 
     * Return true if an object of the class has
     * an enclosing instance of <code>encl</code>. 
     */
    public boolean hasEnclosingInstance(ClassType encl) {
        if (this.equals(encl)) {
            // object o is the zeroth lexically enclosing instance of itself. 
            return true;
        }
        
        if (!isInnerClass() || inStaticContext()) {
            // this class is not an inner class, or was declared in a static
            // context; it cannot have an enclosing
            // instance of anything. 
            return false;
        }
        
        // see if the immediately lexically enclosing class has an 
        // appropriate enclosing instance
        return this.outer().hasEnclosingInstance(encl);
    }
}
