package polyglot.ext.jl5.ast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import polyglot.ast.Block;
import polyglot.ast.ClassBody;
import polyglot.ast.ClassDecl;
import polyglot.ast.ClassDecl_c;
import polyglot.ast.ConstructorCall;
import polyglot.ast.ConstructorDecl;
import polyglot.ast.Id;
import polyglot.ast.Node;
import polyglot.ast.NodeFactory;
import polyglot.ast.TypeNode;
import polyglot.ext.jl5.types.AnnotationElemInstance;
import polyglot.ext.jl5.types.FlagAnnotations;
import polyglot.ext.jl5.types.JL5Context;
import polyglot.ext.jl5.types.JL5Flags;
import polyglot.ext.jl5.types.JL5MethodInstance;
import polyglot.ext.jl5.types.JL5ParsedClassType;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.ext.jl5.types.ParameterizedType;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.ext.jl5.visit.ApplicationCheck;
import polyglot.ext.jl5.visit.ApplicationChecker;
import polyglot.ext.jl5.visit.JL5AmbiguityRemover;
import polyglot.types.ClassType;
import polyglot.types.ConstructorInstance;
import polyglot.types.Context;
import polyglot.types.Flags;
import polyglot.types.ParsedClassType;
import polyglot.types.QName;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.util.CodeWriter;
import polyglot.util.CollectionUtil;
import polyglot.util.Position;
import polyglot.util.TypedList;
import polyglot.visit.NodeVisitor;
import polyglot.visit.PrettyPrinter;
import polyglot.visit.TypeChecker;

/**
 * A <code>ClassDecl</code> is the definition of a class, abstract class, or
 * interface. It may be a public or other top-level class, or an inner named
 * class, or an anonymous class.
 */
public class JL5ClassDecl_c extends ClassDecl_c implements JL5ClassDecl, ApplicationCheck {
    protected List annotations;

    protected List runtimeAnnotations;

    protected List classAnnotations;

    protected List sourceAnnotations;

    protected List<ParamTypeNode> paramTypes = new ArrayList<ParamTypeNode>();

    public JL5ClassDecl_c(Position pos, FlagAnnotations flags, Id name, TypeNode superClass,
            List interfaces, ClassBody body) {
        super(pos, flags.classicFlags(), name, superClass, interfaces, body);
        if (flags.annotations() != null) {
            this.annotations = TypedList.copyAndCheck(flags.annotations(), AnnotationElem.class, false);
        } else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }

    }

    public JL5ClassDecl_c(Position pos, FlagAnnotations fl, Id name, TypeNode superType,
            List interfaces, ClassBody body, List<ParamTypeNode> paramTypes) {

        super(pos, fl.classicFlags(), name, superType, interfaces, body);
        if (fl.annotations() != null) {
            this.annotations = TypedList.copyAndCheck(fl.annotations(), AnnotationElem.class, false);
        } else {
            this.annotations = new TypedList(new LinkedList(), AnnotationElem.class, false);
        }
        this.paramTypes = paramTypes;
    }

    public List annotations() {
        return this.annotations;
    }

    public JL5ClassDecl annotations(List annotations) {
        if (annotations != null) {
            JL5ClassDecl_c n = (JL5ClassDecl_c) copy();
            n.annotations = annotations;
            return n;
        }
        return this;
    }

    public List<ParamTypeNode> paramTypes() {
        return this.paramTypes;
    }

    public JL5ClassDecl paramTypes(List<ParamTypeNode> types) {
        JL5ClassDecl_c n = (JL5ClassDecl_c) copy();
        n.paramTypes = types;
        return n;
    }

    protected ClassDecl reconstruct(TypeNode superClass, List interfaces, ClassBody body,
            List annotations, List paramTypes) {
        if (superClass != this.superClass || !CollectionUtil.allEqual(interfaces, this.interfaces)
                || body != this.body || !CollectionUtil.allEqual(annotations, this.annotations)
                || !CollectionUtil.allEqual(paramTypes, this.paramTypes)) {
            JL5ClassDecl_c n = (JL5ClassDecl_c) copy();
            n.superClass = superClass;
            n.interfaces = TypedList.copyAndCheck(interfaces, TypeNode.class, false);
            n.body = body;
            n.annotations = TypedList.copyAndCheck(annotations, AnnotationElem.class, false);
            n.paramTypes = paramTypes;
            return n;
        }
        return this;
    }

    public Node visitChildren(NodeVisitor v) {
        List annots = visitList(this.annotations, v);
        List paramTypes = visitList(this.paramTypes, v);
        TypeNode superClass = (TypeNode) visitChild(this.superClass, v);
        List interfaces = visitList(this.interfaces, v);
        ClassBody body = (ClassBody) visitChild(this.body, v);
        return reconstruct(superClass, interfaces, body, annots, paramTypes);
    }

    /*
    public Context enterScope(Node child, Context c) {
        // System.out.println("enter scop with child : " + child );//for debug
        for (ParamTypeNode tn : paramTypes) {
            c = ((JL5Context) c).pushTypeVariable((TypeVariable) tn.type());
        }
        return super.enterScope(child, c);
    }
*/
    /*
     * (non-Javadoc)
     * 
     * @see polyglot.ast.NodeOps#enterScope(polyglot.types.Context)
     */
    public Context enterScope(Context c) {
        TypeSystem ts = c.typeSystem();
        // System.out.println("enter scop with context" );//for debug
        c = c.pushClass(type, ts.staticTarget(type).toClass());
        for (ParamTypeNode tn : paramTypes) {
            c = ((JL5Context) c).addTypeVariable((TypeVariable) tn.type());
        }
        return super.enterScope(c);
    }

    protected void disambiguateSuperType(AmbiguityRemover ar) throws SemanticException {
        JL5TypeSystem ts = (JL5TypeSystem) ar.typeSystem();
        if (JL5Flags.isAnnotationModifier(flags())) {
            this.type.superType(ts.Annotation());
        } else {
            super.disambiguateSuperType(ar);
        }
    }

    // still need this - will cause an extra disamb pass which will permit
    // the type variables to fully disambigute themselves
    // before they may be needed as args in superClass or interfaces
    public NodeVisitor disambiguateEnter(AmbiguityRemover ar) throws SemanticException {
        if (ar.kind() == JL5AmbiguityRemover.TYPE_VARS) {
            NodeVisitor nv = ar.bypass(superClass).bypass(interfaces);
            return nv;
        } else {
            return super.disambiguateEnter(ar);
        }
    }

    public Node disambiguate(AmbiguityRemover ar) throws SemanticException {
        Node n = super.disambiguate(ar);
        addTypeParameters();
        return n;
    }

    public Node typeCheck(TypeChecker tc) throws SemanticException {
        if (JL5Flags.isEnumModifier(flags()) && flags().isAbstract()) {
            throw new SemanticException("Enum types cannot have abstract modifier", this.position());
        }
        if (JL5Flags.isEnumModifier(flags()) && flags().isPrivate() && !type().isInnerClass()) {
            throw new SemanticException("Enum types cannot have explicit private modifier", this.position());
        }
        if (JL5Flags.isEnumModifier(flags()) && flags().isFinal()) {
            throw new SemanticException("Enum types cannot have explicit final modifier", this.position());
        }
        if (JL5Flags.isAnnotationModifier(flags()) && flags().isPrivate()) {
            throw new SemanticException("Annotation types cannot have explicit private modifier", this.position());
        }

        if (type().superType() != null
                && JL5Flags.isEnumModifier(type().superType().toClass().flags())) {
            throw new SemanticException("Cannot extend enum type", position());
        }

        JL5TypeSystem ts = (JL5TypeSystem) tc.typeSystem();
        ts.checkDuplicateAnnotations(annotations);

        if (ts.equals(ts.Object(), type()) && !paramTypes.isEmpty()) {
            throw new SemanticException("Type: " + type() + " cannot declare type variables.", position());
        }

        // check not extending java.lang.Throwable (or any of its subclasses)
        // with a generic class
        if (type().superType() != null && ts.isSubtype(type().superType(), ts.Throwable())
                && !paramTypes.isEmpty()) {
            throw new SemanticException("Cannot subclass java.lang.Throwable or any of its subtypes with a generic class", superClass().position());
        }

        // check duplicate type variable decls
        for (int i = 0; i < paramTypes.size(); i++) {
            TypeNode ti = (TypeNode) paramTypes.get(i);
            for (int j = i + 1; j < paramTypes.size(); j++) {
                TypeNode tj = (TypeNode) paramTypes.get(j);
                if (ts.equals(ti.type(), tj.type())) {
                    throw new SemanticException("Duplicate type variable declaration.", tj.position());
                }
            }
        }
        

        JL5ParsedClassType ct = (JL5ParsedClassType) type();
        if (ct.isGeneric()) {
            ts.checkTVForwardReference(ct.typeVariables());
        }
        // set up ct with annots
        ct.annotations(this.annotations);

        if (JL5Flags.isEnumModifier(flags())) {
            for (Iterator it = type().constructors().iterator(); it.hasNext();) {
                ConstructorInstance ci = (ConstructorInstance) it.next();
                if (!ci.flags().clear(Flags.PRIVATE).equals(Flags.NONE)) {
                    throw new SemanticException("Modifier " + ci.flags().clear(Flags.PRIVATE)
                            + " not allowed here", ci.position());
                }
            }
        }

        //disallow wildcards in supertypes and super interfaces
        if (superClass() != null) {
            Type superType = superClass().type();
            if (!ts.equals(superType, ts.capture(superType))) {
                throw new SemanticException("Wildcards not allowed here.", superClass().position());
            }
        }
        for (Iterator it = interfaces().iterator(); it.hasNext();){
            TypeNode itNode = (TypeNode) it.next();
            Type ittype = itNode.type();
            if (!ts.equals(ittype, ts.capture(ittype))) {
                throw new SemanticException("Wildcards not allowed here.", itNode.position());
            }
        }
        checkSuperTypeTypeArgs(tc);

        return super.typeCheck(tc);
    }

    private void checkSuperTypeTypeArgs(TypeChecker tc) throws SemanticException {
        
        List allInterfaces = new ArrayList();
        allInterfaces.addAll(type().interfaces());
        if (((ParsedClassType) type()).superType() != null) {
            allInterfaces.addAll(((ParsedClassType) ((ParsedClassType) type()).superType()).interfaces());
        }

        for (int i = 0; i < allInterfaces.size(); i++) {
            Type next = (Type) allInterfaces.get(i);
            for (int j = i + 1; j < allInterfaces.size(); j++) {
                Type other = (Type) allInterfaces.get(j);
                if (next instanceof ParameterizedType && other instanceof ParameterizedType) {
                    if (tc.typeSystem().equals(((ParameterizedType) next).baseType(), ((ParameterizedType) other).baseType())
                            && !tc.typeSystem().equals(next, other)) {
                        throw new SemanticException(((ParameterizedType) next).baseType()
                                + " cannot be inherited with different type arguments.", position());
                    }
                } else if (next instanceof ParameterizedType) {
                    if (tc.typeSystem().equals(((ParameterizedType) next).baseType(), other)) {
                        throw new SemanticException(((ParameterizedType) next).baseType()
                                + " cannot be inherited with different type arguments.", position());
                    }
                } else if (other instanceof ParameterizedType) {
                    if (tc.typeSystem().equals(((ParameterizedType) other).baseType(), next)) {
                        throw new SemanticException(((ParameterizedType) other).baseType()
                                + " cannot be inherited with different type arguments.", position());
                    }
                }
            }
        }
    }

    public Node applicationCheck(ApplicationChecker appCheck, Context ctx) throws SemanticException {

        // check proper used of predefined annotations
        JL5TypeSystem ts = (JL5TypeSystem) appCheck.typeSystem();
        for (Iterator it = annotations.iterator(); it.hasNext();) {
            AnnotationElem next = (AnnotationElem) it.next();
            ts.checkAnnotationApplicability(next, this);
        }

        // check annotation circularity
        if (JL5Flags.isAnnotationModifier(flags().flags())) {
            JL5ParsedClassType ct = (JL5ParsedClassType) type;
            for (Iterator it = ct.annotationElems().iterator(); it.hasNext();) {
                AnnotationElemInstance ai = (AnnotationElemInstance) it.next();
                if (ts.isTypeExtendsAnnotation(ai.type())) {
                    JL5ParsedClassType other = (JL5ParsedClassType) ai.type();
                    for (Iterator otherIt = other.annotationElems().iterator(); otherIt.hasNext();) {
                        AnnotationElemInstance aj = (AnnotationElemInstance) otherIt.next();
                        if (ts.typeEquals(aj.type(), ct, ctx)) {
                            throw new SemanticException("cyclic annotation element type", aj.position());
                        }
                    }
                }
            }
        }
        return this;
    }

    public Node addMembers(AddMemberVisitor tc) throws SemanticException {
        TypeSystem ts = tc.typeSystem();
        NodeFactory nf = tc.nodeFactory();
        JL5ClassDecl n = (JL5ClassDecl) addGenEnumMethods(ts, nf);
        return n.addDefaultConstructorIfNeeded(ts, nf);
    }

    public Node addDefaultConstructorIfNeeded(TypeSystem ts, NodeFactory nf) {
        return super.addDefaultConstructorIfNeeded(ts, nf);
    }

    protected void addTypeParameters() {
        for (Iterator<ParamTypeNode> it = paramTypes.iterator(); it.hasNext();) {
            TypeVariable tv = (TypeVariable)it.next().type();
            ((JL5ParsedClassType) this.type()).addTypeVariable(tv);
        }
    }

    protected Node addGenEnumMethods(TypeSystem ts, NodeFactory nf) {
        if (JL5Flags.isEnumModifier(type.flags())) {

            JL5ClassBody newBody = (JL5ClassBody) body();
            // add values method
            FlagAnnotations vmFlags = new FlagAnnotations();
            vmFlags.classicFlags(Flags.PUBLIC.set(Flags.STATIC.set(Flags.FINAL)));
            Block valuesB = nf.Block(position());
            valuesB = valuesB.append(nf.Return(position(), nf.NullLit(position())));
            JL5MethodDecl valuesMeth = ((JL5NodeFactory) nf).JL5MethodDecl(position(), vmFlags, nf.CanonicalTypeNode(position(), ts.arrayOf(this.type())), "values", Collections.EMPTY_LIST, Collections.EMPTY_LIST, valuesB, null);

            valuesMeth = valuesMeth.setCompilerGenerated(true);

            JL5MethodInstance mi = (JL5MethodInstance) ts.methodInstance(position(), this.type(), JL5Flags.PUBLIC.set(JL5Flags.STATIC).set(JL5Flags.FINAL), ts.arrayOf(this.type()), "values", Collections.EMPTY_LIST, Collections.EMPTY_LIST);

            mi = mi.setCompilerGenerated(true);
            this.type.addMethod(mi);
            valuesMeth = (JL5MethodDecl) valuesMeth.methodInstance(mi);
            newBody = (JL5ClassBody) newBody.addMember(valuesMeth);

            // add valueOf method
            ArrayList formals = new ArrayList();
            FlagAnnotations fl = new FlagAnnotations();
            fl.classicFlags(JL5Flags.NONE);
            fl.annotations(new ArrayList());
            JL5Formal f1 = ((JL5NodeFactory) nf).JL5Formal(position(), fl, nf.CanonicalTypeNode(position(), ts.String()), "arg1");
            f1 = (JL5Formal) f1.localInstance(ts.localInstance(position(), JL5Flags.NONE, ts.String(), "arg1"));
            formals.add(f1);

            FlagAnnotations voFlags = new FlagAnnotations();
            voFlags.classicFlags(Flags.PUBLIC.set(Flags.STATIC));

            Block valueOfB = nf.Block(position());
            valueOfB = valueOfB.append(nf.Return(position(), nf.NullLit(position())));

            JL5MethodDecl valueOfMeth = ((JL5NodeFactory) nf).JL5MethodDecl(position(), voFlags, nf.CanonicalTypeNode(position(), this.type()), "valueOf", formals, Collections.EMPTY_LIST, valueOfB, null);

            valueOfMeth = valueOfMeth.setCompilerGenerated(true);

            ArrayList formalTypes = new ArrayList();
            formalTypes.add(ts.String());

            JL5MethodInstance mi2 = (JL5MethodInstance) ts.methodInstance(position(), this.type(), JL5Flags.PUBLIC.set(JL5Flags.STATIC), this.type(), "valueOf", formalTypes, Collections.EMPTY_LIST);

            mi2 = mi2.setCompilerGenerated(true);
            this.type.addMethod(mi2);
            valueOfMeth = (JL5MethodDecl) valueOfMeth.methodInstance(mi2);
            newBody = (JL5ClassBody) newBody.addMember(valueOfMeth);

            return body(newBody);
        }
        return this;
    }

    protected Node addDefaultConstructor(TypeSystem ts, NodeFactory nf) {
        ConstructorInstance ci = ts.defaultConstructor(position(), this.type);
        this.type.addConstructor(ci);
        Block block = null;
        if (this.type.superType() instanceof ClassType && !JL5Flags.isEnumModifier(flags())) {
            ConstructorInstance sci = ts.defaultConstructor(position(), (ClassType) this.type.superType());
            ConstructorCall cc = nf.SuperCall(position(), Collections.EMPTY_LIST);
            cc = cc.constructorInstance(sci);
            block = nf.Block(position(), cc);
        } else {
            block = nf.Block(position());
        }

        ConstructorDecl cd;
        FlagAnnotations fl = new FlagAnnotations();
        fl.annotations(new ArrayList());
        if (!JL5Flags.isEnumModifier(flags())) {
            fl.classicFlags(Flags.PUBLIC);
            cd = ((JL5NodeFactory) nf).JL5ConstructorDecl(position(), fl, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST, block, new ArrayList());
        } else {
            fl.classicFlags(Flags.PRIVATE);
            /*
             * ArrayList formalTypes = new ArrayList(); FlagAnnotations fa = new
             * FlagAnnotations(); fa.classicFlags(Flags.NONE);
             * fa.annotations(new ArrayList());
             * formalTypes.add(((JL5NodeFactory)nf).JL5Formal(position(), fa,
             * nf.CanonicalTypeNode(position(), ts.String()), "arg0"));
             * formalTypes.add(((JL5NodeFactory)nf).JL5Formal(position(), fa,
             * nf.CanonicalTypeNode(position(), ts.Int()), "arg1"));
             */
            cd = ((JL5NodeFactory) nf).JL5ConstructorDecl(position(), fl, name, Collections.EMPTY_LIST, Collections.EMPTY_LIST, block, new ArrayList());
        }
        cd = (ConstructorDecl) cd.constructorInstance(ci);
        return body(body.addMember(cd));
    }

    /*
     * protected boolean defaultConstructorNeeded(){ if
     * (JL5Flags.isEnumModifier(flags())) return false; return
     * super.defaultConstructorNeeded(); }
     */

    public void prettyPrintModifiers(CodeWriter w, PrettyPrinter tr) {
        for (Iterator it = annotations.iterator(); it.hasNext();) {
            print((AnnotationElem) it.next(), w, tr);
        }
        if (flags.isInterface()) {
            if (JL5Flags.isAnnotationModifier(flags)) {
                w.write(JL5Flags.clearAnnotationModifier(flags).clearInterface().clearAbstract().translate());
                w.write("@");
            } else {
                w.write(flags.clearInterface().clearAbstract().translate());
            }
        } else {
            w.write(flags.translate());
        }

        if (flags.isInterface()) {
            w.write("interface ");
        } else if (JL5Flags.isEnumModifier(flags)) {
        } else {
            w.write("class ");
        }
    }

    public void prettyPrintName(CodeWriter w, PrettyPrinter tr) {
        w.write(name);
    }

    public void prettyPrintHeaderRest(CodeWriter w, PrettyPrinter tr) {
        if (superClass() != null && !JL5Flags.isEnumModifier(type.flags())) {
            w.write(" extends ");
            print(superClass(), w, tr);
        }

        if (!interfaces.isEmpty() && !JL5Flags.isAnnotationModifier(type.flags())) {
            if (flags.isInterface()) {
                w.write(" extends ");
            } else {
                w.write(" implements ");
            }

            for (Iterator i = interfaces().iterator(); i.hasNext();) {
                TypeNode tn = (TypeNode) i.next();
                print(tn, w, tr);

                if (i.hasNext()) {
                    w.write(", ");
                }
            }
        }

        w.write(" {");
    }

    public void prettyPrintHeader(CodeWriter w, PrettyPrinter tr) {
        prettyPrintModifiers(w, tr);
        prettyPrintName(w, tr);
        if (paramTypes != null && !paramTypes.isEmpty()) {
            w.write("<");
            for (Iterator<ParamTypeNode> it = paramTypes.iterator(); it.hasNext();) {
                ParamTypeNode next = it.next();
                print(next, w, tr);
                if (it.hasNext()) {
                    w.write(", ");
                }
            }
            w.write("> ");
        }
        prettyPrintHeaderRest(w, tr);

    }

    public List runtimeAnnotations() {
        return runtimeAnnotations;
    }

    public List classAnnotations() {
        return classAnnotations;
    }

    public List sourceAnnotations() {
        return sourceAnnotations;
    }
}
