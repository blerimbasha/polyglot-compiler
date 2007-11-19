/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import polyglot.frontend.*;
import polyglot.util.Position;

/**
 * A <code>FieldInstance</code> contains type information for a field.
 */
public class FieldDef_c extends VarDef_c implements FieldDef
{
    protected Ref<? extends ReferenceType> container;

    /** Used for deserializing types. */
    protected FieldDef_c() { }
    
    public FieldDef_c(TypeSystem ts, Position pos,
			   Ref<? extends ReferenceType> container,
	                   Flags flags, Ref<? extends Type> type, String name) {
        super(ts, pos, flags, type, name);
        this.container = container;
    }
    
    public FieldInstance asReference() {
        return new FieldInstance_c(ts, position(), Ref_c.<FieldDef>ref(this));
    }

    public Ref<? extends ReferenceType> container() {
        return container;
    }
    
    public boolean isConstant() {
        setConstant();
        assert constantValueSet;
        return isConstant;
    }
    
    public void complete(GoalSet phase) {
        Scheduler scheduler = typeSystem().extensionInfo().scheduler();
        
        if (phase.contains(scheduler.FieldConstantsChecked(this.<FieldDef>symbol()))) {
            setConstant();
        }
    }
    
    boolean recursive = false;
    
    void setConstant() {
        if (! constantValueSet) {
            if (! flags.isFinal()) {
                setNotConstant();
                return;
            }
            
            if (recursive) {
                // The field is not constant if the initializer is recursive.
                //
                // But, we could be checking if the field is constant for another
                // reference in the same file:
                //
                // m() { use x; }
                // final int x = 1;
                //
                // So this is incorrect.  The goal below needs to be refined to only visit the initializer.
                setNotConstant();
            }
            else {
                recursive = true;
                
                Scheduler scheduler = typeSystem().extensionInfo().scheduler();
                Goal g = scheduler.FieldConstantsChecked(this.<FieldDef>symbol());
                scheduler.attempt(g);
                assert g.hasBeenReached();
                assert constantValueSet;
            
                recursive = false;
            }
        }
    }

    /**
     * @param container The container to set.
     */
    public void setContainer(Ref<? extends ReferenceType> container) {
        this.container = container;
    }
     
    public boolean equalsImpl(TypeObject o) {
        return this == o;
    }

    public String toString() {
        Object v = constantValue;
        if (v instanceof String) {
          String s = (String) v;

          if (s.length() > 8) {
            s = s.substring(0, 8) + "...";
          }

          v = "\"" + s + "\"";
        }

        return "field " + flags.translate() + type + " " +
            container + "." + name +
	    (isConstant ? (" = " + v) : "");
    }
}