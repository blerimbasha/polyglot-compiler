package jltools.ast;

import jltools.util.CodeWriter;
import jltools.util.Copy;
import jltools.visit.TypeBuilder;
import jltools.visit.AmbiguityRemover;
import jltools.visit.ConstantFolder;
import jltools.visit.AddMemberVisitor;
import jltools.visit.TypeChecker;
import jltools.visit.ExceptionChecker;
import jltools.visit.Translator;
import jltools.types.SemanticException;
import jltools.types.TypeSystem;
import jltools.types.Context;

/**
 * <code>Ext</code> is the super type of all node extension objects.
 * It declares the methods which implement compiler passes.
 */
public interface Ext extends Copy
{
    Object copy();
    void init(Node n);

    Node buildTypesOverride(TypeBuilder tb) throws SemanticException;
    Node buildTypes(TypeBuilder tb) throws SemanticException;

    Node addMembersOverride(AddMemberVisitor tc) throws SemanticException;
    Node addMembers(AddMemberVisitor tc) throws SemanticException;

    Node disambiguateOverride(AmbiguityRemover ar) throws SemanticException;
    Node disambiguate(AmbiguityRemover ar) throws SemanticException;

    Node foldConstantsOverride(ConstantFolder cf);
    Node foldConstants(ConstantFolder cf);

    Node typeCheckOverride(TypeChecker tc) throws SemanticException;
    Node typeCheck(TypeChecker tc) throws SemanticException;

    Node exceptionCheckOverride(ExceptionChecker ec) throws SemanticException;
    Node exceptionCheck(ExceptionChecker ec) throws SemanticException;

    void translate(CodeWriter w, Translator tr);
}