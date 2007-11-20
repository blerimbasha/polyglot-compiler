/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.ast;

import polyglot.types.MethodDef;
import polyglot.types.Flags;
import java.util.List;

/**
 * A method declaration.
 */
public interface MethodDecl extends ProcedureDecl 
{
    /** The method's flags. */
    Flags flags();

    /** Set the method's flags. */
    MethodDecl flags(Flags flags);

    /** The method's return type.  */
    TypeNode returnType();

    /** Set the method's return type.  */
    MethodDecl returnType(TypeNode returnType);

    /** The method's name. */
    Id id();
    
    /** Set the method's name. */
    MethodDecl id(Id name);

    /** The method's formal parameters.
     * @return A list of {@link polyglot.ast.Formal Formal}.
     */
    List<Formal> formals();

    /** Set the method's formal parameters.
     * @param formals A list of {@link polyglot.ast.Formal Formal}.
     */
    MethodDecl formals(List<Formal> formals);

    /** The method's exception throw types.
     * @return A list of {@link polyglot.ast.TypeNode TypeNode}.
     */
    List<TypeNode> throwTypes();

    /** Set the method's exception throw types.
     * @param throwTypes A list of {@link polyglot.ast.TypeNode TypeNode}.
     */
    MethodDecl throwTypes(List<TypeNode> throwTypes);

    /**
     * The method type object.  This field may not be valid until
     * after signature disambiguation.
     */
    MethodDef methodDef();

    /** Set the method's type object. */
    MethodDecl methodDef(MethodDef mi);
}
