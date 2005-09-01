package polyglot.ext.jl.types;

import polyglot.types.*;
import polyglot.util.*;

/**
 * A <code>InitializerInstance</code> contains the type information for a
 * static or anonymous initializer.
 */
public class InitializerInstance_c extends TypeObject_c
                                implements InitializerInstance
{
    protected ClassType container;
    protected Flags flags;

    /** Used for deserializing types. */
    protected InitializerInstance_c() { }

    public InitializerInstance_c(TypeSystem ts, Position pos,
				 ClassType container, Flags flags) {
        super(ts, pos);
	this.container = container;
	this.flags = flags;
    }

    public ReferenceType container() {
        return container;
    }

    public InitializerInstance container(ClassType container) {
        if (this.container != container) {
            InitializerInstance_c n = (InitializerInstance_c) copy();
            n.setContainer(container);
            return n;
        }
        return this;
    }
    
    public void setContainer(ReferenceType container) {
        this.container = (ClassType) container;
    }

    public Flags flags() {
        return flags;
    }

    public InitializerInstance flags(Flags flags) {
        if (!flags.equals(this.flags)) {
            InitializerInstance_c n = (InitializerInstance_c) copy();
            n.setFlags(flags);
            return n;
        }
        return this;
    }

    /**
     * @param container The container to set.
     */
    public void setContainer(ClassType container) {
        this.container = container;
    }
    
    /**
     * @param flags The flags to set.
     */
    public void setFlags(Flags flags) {
        this.flags = flags;
    }
    
    public int hashCode() {
        return container.hashCode() + flags.hashCode();
    }

    public boolean equalsImpl(TypeObject o) {
        if (o instanceof InitializerInstance) {
	    InitializerInstance i = (InitializerInstance) o;
	    return flags.equals(i.flags()) && ts.equals(container, i.container());
	}

	return false;
    }

    public String toString() {
        return flags.translate() + "initializer";
    }

    public boolean isCanonical() {
	return true;
    }
}
