package ibex.types;

import java.util.Collections;
import java.util.List;

import polyglot.types.ClassType;
import polyglot.types.Def_c;
import polyglot.types.Flags;
import polyglot.types.Name;
import polyglot.types.Ref;
import polyglot.types.StructType;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.util.Position;
import polyglot.util.TypedList;

public class RuleDef_c extends Def_c implements RuleDef {
    protected Ref<? extends StructType> container;
    protected Flags flags;
    protected Name name;
    protected Ref<? extends Type> type;
    protected List<Rhs> choices;

    /** Used for deserializing types. */
    protected RuleDef_c() {
    }
    
    public RuleDef_c(TypeSystem ts, 
        Position pos, Ref<? extends ClassType> container, Flags flags, Ref<? extends Type> type, Name name, List<Rhs> choices) {
        super(ts, pos);
        this.container = container;
        this.flags = flags;
        this.type = type;
        this.name = name;
        this.choices = TypedList.copyAndCheck(choices, Rhs.class, true);
    }
    
    public Ref<? extends StructType> container() {
        return container;
    }

    public Flags flags() {
        return flags;
    }

    public List<Rhs> choices() {
        return Collections.unmodifiableList(choices);
    }

    /**
     * @param container The container to set.
     */
    public void setContainer(Ref<? extends StructType> container) {
        this.container = container;
    }
    
    /**
     * @param flags The flags to set.
     */
    public void setFlags(Flags flags) {
        this.flags = flags;
    }
    
    public void setChoices(List<Rhs> choices) {
        this.choices = TypedList.copyAndCheck(choices, Rhs.class, true);
    }
    
    protected transient RuleInstance asInstance;
    
    public RuleInstance asInstance() {
        if (asInstance == null) {
            asInstance = new RuleInstance_c(ts, position(), Types.ref(this));
        }
        return asInstance;
    }
    protected transient Nonterminal asNonterminal;
    public Nonterminal asNonterminal() {
        if (asNonterminal == null) {
            asNonterminal = new Nonterminal_c((IbexTypeSystem) ts, position(), asInstance());
        }
        return asNonterminal;
    }

    public Name name() {
        return name;
    }

    public Ref<? extends Type> type() {
        return type;
    }

    /**
     * @param name The name to set.
     */
    public void setName(Name name) {
        this.name = name;
        asInstance = null;
    }

    public void setType(Ref<? extends Type> type) {
        this.type = type;
        asInstance = null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String sep = "";
        for (Rhs r : choices) {
            sb.append(sep);
            sep = " | ";
            sb.append(r);
        }
        return flags.translate() + type + " " + name + " ::= " + sb;
    }
}
