package polyglot.types;

public interface VarInstance<T extends VarDef> extends Use<T> {
    /**
     * The flags of the variable.
     */
    Flags flags();
    VarInstance<T> flags(Flags flags);

    /**
     * The name of the variable.
     */
    String name();
    VarInstance<T> name(String name);

    /**
     * The type of the variable.
     */
    Type type();
    VarInstance<T> type(Type type);

    /**
     * The variable's constant value, or null.
     */
    Object constantValue();
    VarInstance<T> constantValue(Object o);
    VarInstance<T> notConstant();

    /**
     * Whether the variable has a constant value.
     */
    boolean isConstant();
}
