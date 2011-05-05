package polyglot.ext.jl5.types.reflect;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import polyglot.ext.jl5.types.JL5ParsedClassType;
import polyglot.ext.jl5.types.JL5TypeSystem;
import polyglot.ext.jl5.types.ParameterizedType;
import polyglot.ext.jl5.types.TypeVariable;
import polyglot.types.ClassDef;
import polyglot.types.ClassType;
import polyglot.types.Ref;
import polyglot.types.ReferenceType;
import polyglot.types.SemanticException;
import polyglot.types.Type;
import polyglot.types.TypeSystem;
import polyglot.types.Types;
import polyglot.types.reflect.Attribute;
import polyglot.types.reflect.ClassFile;
import polyglot.types.reflect.ClassFileLazyClassInitializer;
import polyglot.util.Position;

public class Signature extends Attribute {

	protected DataInputStream in;
	protected final int index;
	protected ClassFile cls;
	protected JL5TypeSystem ts;
	protected Position position;
	protected ClassSig classSignature;
	protected MethodSig methodSignature;
	protected FieldSig fieldSignature;
	protected List typeVars;
	protected ClassDef curClass;
	protected ClassFileLazyClassInitializer cllz;

	/**
	 * Grammar:
	 * class_sig = 
	 *  formal_type_params_opt super_class_sig super_inter_sig_list_opt
	 * formal_type_params_opt =
	 *  * empty *
	 *  | LEFT_ANGLE formal_type_param_list RIGHT_ANGLE
	 * formal_type_param_list =
	 *  formal_type_param
	 *  | formal_type_param_list formal_type_param
	 * formal_type_param =
	 *  ID class_bound inter_bound_list_opt
	 * class_bound =
	 *  COLON field_type_sig_opt
	 * inter_bound_list_opt = 
	 *  * empty *
	 *  | inter_bound_list
	 * inter_bound_list = 
	 *  inter_bound
	 *  inter_bound_list inter_bound
	 * inter_bound = 
	 *  COLON field_type_sig
	 * super_class_sig =
	 *  class_type_sig
	 * super_inter_sig_list_opt = 
	 *  * empty *
	 *  | super_inter_sig_list
	 * super_inter_sig_list =
	 *  super_inter_sig
	 *  | super_inter_sig_list | super_inter_sig
	 * super_inter_sig =
	 *  class_type_sig
	 * field_type_sig =
	 *  class_type_sig
	 *  | array_type_sig
	 *  | type_var_sig
	 * class_type_sig =
	 *  L pack_spec_list_opt simple_class_type_sig 
	 *      class_type_sig_suffix_list_opt SEMI_COLON
	 * pack_spec_list_opt =
	 *  * empty *
	 *  | pack_spec_list
	 * pack_spec_list =
	 *  pack_spec
	 *  | pack_spec_list pack_spec
	 * pack_spec =
	 *  ID SLASH
	 * simple_class_type_sig = 
	 *  ID type_args_opt
	 * class_type_sig_suffix_list_opt =
	 *  * empty *
	 *  | class_type_sig_suffix_list
	 * class_type_sig_suffix_list =
	 *  class_type_sig_suffix
	 *  | class_type_sig_suffix_list class_type_sig_suffix
	 * class_type_sig_suffix =
	 *  DOT simple_class_type_sig
	 * type_var_sig =
	 *  T ID SEMI_COLON
	 * type_args =
	 *  LEFT_ANGLE type_arg_list RIGHT_ANGLE
	 * type_arg_list = 
	 *  type_arg
	 *  | type_arg_list type_arg
	 * type_arg =
	 *  wild_card_ind_opt field_type_sig
	 *  | STAR
	 * wild_card_ind_opt = 
	 *  * empty *
	 *  | wild_card_ind
	 * wild_card_ind = 
	 *  PLUS
	 *  | MINUS
	 * array_type_sig =
	 *  LEFT_SQUARE type_sig
	 * type_sig =
	 *  field_type_sig
	 *  | base_type
	 * 
	 * method_type_sig =
	 *  formal_type_params_opt LEFT_BRACE type_sig_list_opt RIGHT_BRACE 
	 *      return_type throws_sig_list_opt
	 * return_type =
	 *  type_sig
	 *  | V
	 * throws_sig_list_opt = 
	 *  * empty *
	 *  | throws_sig_list
	 * throws_sig_list =
	 *  throws_sig 
	 *  | throws_sig_list throws_sig
	 * throws_sig =
	 *  HAT class_type_sig
	 *  | HAT type_var_sig
	 * 
	 * base_type =
	 *  B | C | D | F | I | J | S | Z 
	 *  
	 */

	Signature(DataInputStream in, int nameIndex, int length, ClassFile clazz) throws IOException{
		super(nameIndex, length);
		this.index = in.readUnsignedShort();
		this.cls = clazz;
	}

	// tokens
	private final char LEFT_ANGLE = '<';
	private final char RIGHT_ANGLE = '>';
	private final char COLON = ':';
	private final char L = 'L';
	private final char SEMI_COLON = ';';
	private final char SLASH = '/';
	private final char DOT = '.';
	private final char T = 'T';
	private final char STAR = '*';
	private final char PLUS = '+';
	private final char MINUS = '-';
	private final char LEFT_SQUARE = '[';
	private final char LEFT_BRACE = '(';
	private final char RIGHT_BRACE = ')';
	private final char V = 'V';
	private final char HAT = '^';
	private final char B = 'B';
	private final char C = 'C';
	private final char D = 'D';
	private final char F = 'F';
	private final char I = 'I';
	private final char J = 'J';
	private final char S = 'S';
	private final char Z = 'Z';
	private boolean createTypeVars;


	class ClassSig {
		public ClassSig(List<Ref<? extends Type>> typeVars, Ref<? extends Type> superType, List<Ref<? extends Type>> interfaces){
			this.typeVars = typeVars;
			this.superType = superType;
			this.interfaces = interfaces;
		}
		protected List<Ref<? extends Type>> typeVars;   // list of intersection types
		public List<Ref<? extends Type>> typeVars(){
			return typeVars;
		}
		protected Ref<? extends Type> superType;
		public Ref<? extends Type> superType(){
			return superType;
		}
		protected List<Ref<? extends Type>> interfaces; // list of types 
		public List<Ref<? extends Type>> interfaces(){
			return interfaces;
		}
	}

	class MethodSig {
		public MethodSig(List<Ref<? extends Type>> typeVars, List<Ref<? extends Type>> formalTypes, Ref<? extends Type> returnType, List<Ref<? extends Type>> throwTypes){
			this.typeVars = typeVars;
			this.formalTypes = formalTypes;
			this.returnType = returnType;
			this.throwTypes = throwTypes;
		}
		protected List<Ref<? extends Type>> typeVars;     // list of intersection types
		public List<Ref<? extends Type>> typeVars(){
			return typeVars;
		}
		protected List<Ref<? extends Type>> formalTypes;  // list of types
		public List<Ref<? extends Type>> formalTypes(){
			return formalTypes;
		}
		protected Ref<? extends Type> returnType; 
		public Ref<? extends Type> returnType(){
			return returnType;
		}
		protected List<Ref<? extends Type>> throwTypes;   // list of types
		public List<Ref<? extends Type>> throwTypes(){
			return throwTypes;
		}
	}

	class FieldSig {
		protected Ref<? extends Type> type;
	}

	class Result {
		public Result(Object result, int pos){
			this.result = result;
			this.pos = pos;
		}
		protected int pos;
		protected Object result;
		// CHECK do we need position ?
		public int pos(){
			return pos;
		}
		public Object result(){
			return result;
		}
	}

	public Result classSig(String value, int pos){
		List<Ref<? extends Type>> typeFormals = Collections.EMPTY_LIST;
		char token = value.charAt(pos);
		
		Result fres = null;
		if (token == LEFT_ANGLE){
			fres = formalTypeParamList(value, ++pos);
			pos = fres.pos();
			if(fres != null) { typeFormals = (List<Ref<? extends Type>>) fres.result();}
			typeVars = (List)fres.result();
		}
		Result sres = classTypeSig(value, pos);
		Ref<? extends Type> superType = (Ref<? extends Type>) sres.result();
		List<Ref<? extends Type>> superInterfaces = new ArrayList();
		pos = sres.pos();
		while (pos < value.length()){
			Result ires = classTypeSig(value, pos);
			pos = ires.pos();
			superInterfaces.add((Ref<? extends Type>) ires.result());
			//pos++;
		}
		return new Result(new ClassSig(typeFormals, superType, superInterfaces), pos);
	}

	public Result formalTypeParamList(String value, int pos){
		typeVars = null;
		int oldpos = pos;
		createTypeVars = true;
		//do it twice. second time, bounds will be resolved
		//CHECK The first pass creates the typevar: and calls to typeVarSig should fail as map is empty
		//CHECK The second just appears to do the lookup
		List list = new ArrayList();
		char token = value.charAt(pos);
		while (token != RIGHT_ANGLE){
			Result fres = formalTypeParam(value, pos);
			list.add(fres.result());
			pos = fres.pos();
			token = value.charAt(pos);
		}

		typeVars = list;
		pos = oldpos;
		createTypeVars = false;
		list = new ArrayList();
		token = value.charAt(pos);
		while (token != RIGHT_ANGLE){
			Result fres = formalTypeParam(value, pos);
			list.add(fres.result());
			pos = fres.pos();
			token = value.charAt(pos);
		}

		pos++;
		return new Result(list, pos);
	}

	// ID classBound interfaceBoundList(opt)
	public Result formalTypeParam(String value, int pos){
		String id = "";
		char token = value.charAt(pos);
		while (token != COLON){
			id += token;
			pos++;
			token = value.charAt(pos);
		}
		Result cres = classBound(value, pos);
		pos = cres.pos();
		Result ires = null;
		List bounds = new ArrayList();
		token = value.charAt(pos);
		while (token != RIGHT_ANGLE){
			if (value.charAt(pos) != COLON) break;
			ires = classBound(value, pos);
			pos = ires.pos();
			bounds.add(ires.result());
		}
		if (createTypeVars) {
			return new Result(Types.ref(ts.typeVariable(position, id, bounds)), pos);
		}
		else {
			TypeVariable tv = findTypeVar(id);
			tv.bounds(bounds);
			return new Result(tv, pos);
		}
	}

	// : fieldTypeSig
	public Result classBound(String value, int pos){
		return fieldTypeSig(value, ++pos);
	}
 
	/**
	 * classTypeSig L...;
	 * typeVarSig T...;
	 * arrayTypeSig [...;
	 */
	public Result fieldTypeSig(String value, int pos){
		Result res = null;
		char token = value.charAt(pos);
		switch(token){
		case L: { res = classTypeSig(value, pos); break; }
		case LEFT_SQUARE: { res = arrayTypeSig(value, pos); break; }
		case T: { res = typeVarSig(value, pos); break; }                 
		// Enforce notice in ClassFileLazyClassInitializer that we shouldn't call ts.Object()
		case COLON: { res = new Result(cllz.typeForName("java.lang.Object"), pos); break; }
		}
		return res;
	}

	/**
	 * L PackageSpecifier* SimpleClassTypeSignature ClassTypeSignatureSuffix* 
	 * 
	 * PackageSpecifier:
	 * 		Identifier / PackageSpecifier* 
	 * 
	 * SimpleClassTypeSignature: 
	 * 		Identifier TypeArgumentsopt
	 * 
	 * ClassTypeSignatureSuffix:
	 *  	. SimpleClassTypeSignatur
	 * @param value
	 * @param pos
	 * @return
	 */
	public Result classTypeSig(String value, int pos){
		char token = value.charAt(pos); // L
		String className = "";
		String id = "";
		Map<String,List> classArgsMap = new HashMap<String,List>();
		pos++;
		token = value.charAt(pos);   
		while (token != SEMI_COLON){
			switch(token){
			case SLASH: { // id is a package 
				className += id;
				className += "."; 
				id = "";
				pos++;
				token = value.charAt(pos);
				break; }
			case DOT: { // id is a className
				className += id;
				className += "$"; 
				id = "";
				pos++;
				token = value.charAt(pos);
				break; }
			case LEFT_ANGLE: { // type argument list
				// Process the list delimited by '<' '>'
				Result tres = typeArgList(value, pos);
				pos = tres.pos();
				classArgsMap.put(id, (List) tres.result());
				token = value.charAt(pos);
				break; }          
			default: { 
				id += token; 
				pos++;
				token = value.charAt(pos);
				break; }          
			}
		}
		className += id;
		// Gives a non-parameterized instance of the type
		Ref<? extends Type> ct = cllz.typeForName(className);
		// The map may contain type arguments that were under angle brackets
		// for example <K:Ljava/lang/Object;>
		if (classArgsMap.containsKey(className)){
			// If TypeArguments were available, instantiate a parameterized type
			ParameterizedType pt = ts.parameterizedType((JL5ParsedClassType)ct);
			pt.typeArguments(classArgsMap.get(className));
			//Need to update the type with the parameter args
			ct = Types.ref(pt);
		}
		ClassType current = (ClassType) ct.get();
		//ClassType outer = ct.outer();
		//CHECK is this to handles nested classes, i.e. if a signature contains refs to parameterized nested classes
		while (current.outer() != null) {
			if (classArgsMap.containsKey(current.outer().name())){
				ParameterizedType pt = ts.parameterizedType((JL5ParsedClassType)ct);
				pt.typeArguments(classArgsMap.get(current.outer().name()));
				((JL5ParsedClassType)current).def().outer(Types.ref(pt.def()));
			}
			if (current == current.outer()) break;
			current = current.outer();
		}
		pos++;
		return new Result(ct, pos);
	}


	/**
	 * TypeVariableSignature:
	 * 		T Identifer ;
	 */ 
	public Result typeVarSig(String value, int pos){
		Result res = null;
		char token = value.charAt(pos);
		switch(token){
		case T: { 
			String id = "";
			pos++;
			token = value.charAt(pos);    
			while (token != SEMI_COLON){
				id += token;
				pos++;
				token = value.charAt(pos);
			}
			pos++;
			// CHECK sounds that if we're here it means we already resolved the TypeVariable somewhere ?
			res = new Result(findTypeVar(id), pos);
			}
		}
		return res;
	}

	public Result typeArgList(String value, int pos){
		List typeArgs = new ArrayList();
		char token = value.charAt(pos++);
		while (token != RIGHT_ANGLE){
			Result tres = typeArg(value, pos);
			pos = tres.pos();
			typeArgs.add(tres.result());
			token = value.charAt(pos);
		}
		pos++;
		return new Result(typeArgs, pos);
	}   

	public Result typeArg(String value, int pos){
		Result res = null;
		char token = value.charAt(pos);
		switch(token){
		case PLUS: { Result fres = fieldTypeSig(value, ++pos);
		res = new Result(Types.ref(ts.anySubType((ReferenceType)fres.result())), fres.pos());
		break;
		}
		case MINUS: { Result fres = fieldTypeSig(value, ++pos);
		res = new Result(Types.ref(ts.anySuperType((ReferenceType)fres.result())), fres.pos());
		break;
		}
		case STAR: { pos++;
		res = new Result(Types.ref(ts.anyType()), pos);
		break;  
		}
		case L:
		case LEFT_SQUARE:
		case T: { res = fieldTypeSig(value, pos);  
		break;}
		}
		return res;
	}

	/**
	 * ArrayTypeSignature:
	 *		[TypeSignature
	 */
	public Result arrayTypeSig(String value, int pos){
		Result res = null;
		char token = value.charAt(pos);
		switch(token){
		case LEFT_SQUARE : {
			pos++;
			Result tres = typeSig(value, pos);
			Ref<? extends Type> type = (Ref<? extends Type>) tres.result();
			res = new Result(cllz.arrayOf(type, 1), tres.pos());
			break;
			}
		}
		return res;
	}

	public Result typeSigList(String value, int pos){
		List formals = new ArrayList();
		char token = value.charAt(pos);
		while (token != RIGHT_BRACE){
			Result ares = typeSig(value, pos);
			pos = ares.pos();
			formals.add(ares.result());
			token = value.charAt(pos);
		}
		pos++;
		return new Result(formals, pos);
	}

	/**
	 * Get a string and return an associated type
	 * Class, Array, TypeVariables => fieldTypeSig
	 * Primitives=> baseType
	 * @param value
	 * @param pos
	 * @return
	 */
	public Result typeSig(String value, int pos){
		Result res = null;
		char token = value.charAt(pos);
		switch(token) {
		case L: 
		case LEFT_SQUARE:
		case T: { res = fieldTypeSig(value, pos);
		break;
		}
		case B:
		case C:
		case D:
		case F:
		case I:
		case J:
		case S:
		case Z: { res = baseType(value, pos);
		break;
		}
		}
		return res;
	}

	/**
	 * Iterates over the String containing a method signature
	 * @param value
	 * @param pos
	 * @return
	 */
	public Result methodTypeSig(String value, int pos){
		char token = value.charAt(pos);
		List<Ref<? extends Type>> typeVariables = Collections.EMPTY_LIST;
		typeVars = typeVariables;
		List<Ref<? extends Type>> typeFormals = Collections.EMPTY_LIST;
		Ref<? extends Type> typeReturned = null;
		List<Ref<? extends Type>> typeThrown = Collections.EMPTY_LIST;

		if (token == LEFT_ANGLE){
			// '<' : starts type variables list
			Result fres = formalTypeParamList(value, ++pos);
			pos = fres.pos(); // set position to continue parsing
			if (fres != null) { typeVariables = (List)fres.result(); }
			typeVars = typeVariables; // CHECK why do we put that in a class field ??
		}
		if ((token = value.charAt(pos)) == LEFT_BRACE){
			//'(' : starts the formals list
			Result ares = typeSigList(value, ++pos);
			pos = ares.pos();
			if (ares != null) { typeFormals = (List)ares.result(); }			
		}
		// return type 
		Result rres = returnType(value, pos);
		typeReturned = (Ref<? extends Type>) rres.result();
		pos = rres.pos();
		// '^' : look for thrown type
		if ((pos < value.length()) && ((token = value.charAt(pos)) == HAT)){
			Result tres = throwsSigList(value, pos);
			pos = tres.pos();
			if (typeThrown != null) { typeThrown = (List)tres.result(); }
		}
		return new Result(new MethodSig(typeVariables, typeFormals, typeReturned, typeThrown), pos);
	}

	// returnType used in methodSig
	// starts pointing at char 
	// ends after (may be end of string
	public Result returnType(String value, int pos){
		Result res = null;
		char token = value.charAt(pos);
		switch(token){
		case L: 
		case LEFT_SQUARE:
		case T: 
		case B:
		case C:
		case D:
		case F:
		case I:
		case J:
		case S:
		case Z: { res = typeSig(value, pos);
		break;
		}
		case V: { pos++;
		res = new Result(ts.Void(), pos); break;}
		}
		return res;
	}

	// list of throwSigs ^L...;^L...;^T...;
	// starts at ^ may advance beyond end of string
	// this is okay as throwsSigList is last part 
	// of methodTypeSig
	public Result throwsSigList(String value, int pos){
		List throwsList = new ArrayList();
		char token;
		while (pos < value.length()){
			Result tres = throwsSig(value, pos);
			pos = tres.pos();
			throwsList.add(tres.result());
		}
		return new Result(throwsList, pos);    
	}

	// throwsSig used in throwsSigList
	// ^L...; or ^T...;
	// starts at ^ and advances past ; 
	public Result throwsSig(String value, int pos){
		Result res = null;
		char token = value.charAt(pos);
		switch(token){
		case HAT: { token = value.charAt(++pos);
		switch(token){
		case L: { res = classTypeSig(value, pos); 
		}
		case T: { res = typeVarSig(value, pos);                                                 }
		}
		}
		}
		return res;
	}

	/**
	 * baseType used in typeSig one of:
	 * B, C, D, F, I, J, S, Z
	 * starts pointing to the char and ends
	 * advanced to next char
	 */
	public Result baseType(String value, int pos){
		Result res = null;
		char token = value.charAt(pos);
		switch(token) {
		case B: { res = new Result(Types.<Type>ref(ts.Byte()), ++pos); 
		break;
		}
		case C: { res = new Result(Types.<Type>ref(ts.Char()), ++pos); 
		break;
		}
		case D: { res = new Result(Types.<Type>ref(ts.Double()), ++pos); 
		break;
		}
		case F: { res = new Result(Types.<Type>ref(ts.Float()), ++pos); 
		break;
		}
		case I: { res = new Result(Types.<Type>ref(ts.Int()), ++pos); 
		break;
		}
		case J: { res = new Result(Types.<Type>ref(ts.Long()), ++pos); 
		break;
		}
		case S: { res = new Result(Types.<Type>ref(ts.Short()), ++pos); 
		break;
		}
		case Z: { res = new Result(Types.<Type>ref(ts.Boolean()), ++pos); 
		break;
		}

		}
		return res;
	}

	public void parseClassSignature(ClassFileLazyClassInitializer cllz,
			TypeSystem ts, Position pos) throws IOException, SemanticException{
//		this.ts = (JL5TypeSystem)ts;
//		this.position = pos;
		this.cllz = cllz;
		String sigValue = (String)cls.getConstants()[index].value();
		classSignature = (ClassSig)classSig(sigValue, 0).result();
	}

	public void parseMethodSignature(ClassFileLazyClassInitializer cllz, 
			TypeSystem ts, Position pos, ClassDef ct) throws IOException {
		this.ts = (JL5TypeSystem)ts;
		this.position = pos;
		this.curClass = ct;
		this.cllz = cllz;
		String sigValue = (String)cls.getConstants()[index].value();
		methodSignature = (MethodSig)methodTypeSig(sigValue, 0).result();
	}

	public void parseFieldSignature(ClassFileLazyClassInitializer cllz,
			TypeSystem ts, Position pos) throws IOException, SemanticException{
		this.ts = (JL5TypeSystem)ts;
		this.position = pos;
		this.cllz = cllz;
		String sigValue = (String)cls.getConstants()[index].value();
		fieldSignature = (FieldSig)fieldTypeSig(sigValue, 0).result();
	}

	//CHECK should return a ref on the type variable ?
	private TypeVariable findTypeVar(String next){
		if (typeVars != null){
			for (Iterator it = typeVars.iterator(); it.hasNext(); ){
				TypeVariable iType = (TypeVariable)it.next();
				if (iType.name().toString().equals(next)) return iType;
			}
		}
		if (curClass != null && curClass instanceof JL5ParsedClassType){
			if (((JL5ParsedClassType)curClass).typeVariables() != null){
				for (Iterator it = ((JL5ParsedClassType)curClass).typeVariables().iterator(); it.hasNext(); ){
					TypeVariable iType = (TypeVariable)it.next();
					if (iType.name().toString().equals(next)) return iType;
				}
			}
		}
		return null;
	}


	/*public List typeVariables(){
        return typeVars;
    }*/

    public String toString(){
    	return (String)cls.getConstants()[index].value();
    }
}
