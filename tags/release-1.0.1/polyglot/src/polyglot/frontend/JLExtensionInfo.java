package polyglot.ext.jl;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import polyglot.ast.NodeFactory;
import polyglot.ext.jl.ast.NodeFactory_c;
import polyglot.ext.jl.parse.Grm;
import polyglot.ext.jl.parse.Lexer_c;
import polyglot.ext.jl.types.TypeSystem_c;
import polyglot.frontend.BarrierPass;
import polyglot.frontend.CupParser;
import polyglot.frontend.FileSource;
import polyglot.frontend.Job;
import polyglot.frontend.JobExt;
import polyglot.frontend.OutputPass;
import polyglot.frontend.Parser;
import polyglot.frontend.ParserPass;
import polyglot.frontend.Pass;
import polyglot.frontend.VisitorPass;
import polyglot.types.LoadedClassResolver;
import polyglot.types.SemanticException;
import polyglot.types.SourceClassResolver;
import polyglot.types.TypeSystem;
import polyglot.util.ErrorQueue;
import polyglot.util.InternalCompilerError;
import polyglot.visit.AddMemberVisitor;
import polyglot.visit.AmbiguityRemover;
import polyglot.visit.ClassSerializer;
import polyglot.visit.ConstructorCallChecker;
import polyglot.visit.ExceptionChecker;
import polyglot.visit.ExitChecker;
import polyglot.visit.InitChecker;
import polyglot.visit.ReachChecker;
import polyglot.visit.Translator;
import polyglot.visit.TypeBuilder;
import polyglot.visit.TypeChecker;

/**
 * This is the default <code>ExtensionInfo</code> for the Java language.
 *
 * Compilation passes and visitors:
 * <ol>
 * <li> parse </li>
 * <li> build-types (TypeBuilder) </li>
 * <hr>
 * <center>BARRIER</center>
 * <hr>
 * <li> clean-super (AmbiguityRemover) </li>
 * <hr>
 * <center>BARRIER</center>
 * <hr>
 * <li> clean-sigs (AmbiguityRemover) </li>
 * <li> add-members (AddMemberVisitor) </li>
 * <hr>
 * <center>BARRIER</center>
 * <hr>
 * <li> disambiguate (AmbiguityRemover) </li>
 * <li> constant folding (ConstantFolder)
 * <hr>
 * <center>BARRIER</center>
 * <hr>
 * <li> type checking (TypeChecker) </li>
 * <li> exception checking (ExceptionChecker)
 * <hr>
 * <center>BARRIER</center>
 * <hr>
 * <li> serialization (ClassSerializer), optional </li>
 * <li> translation (Translator) </li>
 * </ol>
 */
public class ExtensionInfo extends polyglot.frontend.AbstractExtensionInfo {
    protected void initTypeSystem() {
	try {
            LoadedClassResolver lr;
            lr = new SourceClassResolver(compiler, this, getOptions().constructFullClasspath(),
                                         compiler.loader(), true);
            ts.initialize(lr);
	}
	catch (SemanticException e) {
	    throw new InternalCompilerError(
		"Unable to initialize type system: " + e.getMessage());
	}
    }

    public String defaultFileExtension() {
        return "jl";
    }

    public String compilerName() {
	return "jlc";
    }

    public polyglot.main.Version version() {
	return new Version();
    }

    /** Create the type system for this extension. */
    protected TypeSystem createTypeSystem() {
	return new TypeSystem_c();
    }

    /** Create the node factory for this extension. */
    protected NodeFactory createNodeFactory() {
	return new NodeFactory_c();
    }

    public JobExt jobExt() {
      return null;
    }

    public Parser parser(Reader reader, FileSource source, ErrorQueue eq) {
	polyglot.lex.Lexer lexer = new Lexer_c(reader, source.name(), eq);
	polyglot.parse.BaseParser parser = new Grm(lexer, ts, nf, eq);

	return new CupParser(parser, source, eq);
    }

    public List passes(Job job) {
        ArrayList l = new ArrayList(15);

	l.add(new ParserPass(Pass.PARSE, compiler, job));

        l.add(new VisitorPass(Pass.BUILD_TYPES, job, new TypeBuilder(job, ts, nf)));
	l.add(new BarrierPass(Pass.BUILD_TYPES_ALL, job));
	l.add(new VisitorPass(Pass.CLEAN_SUPER, job,
                              new AmbiguityRemover(job, ts, nf, AmbiguityRemover.SUPER)));
	l.add(new BarrierPass(Pass.CLEAN_SUPER_ALL, job));
	l.add(new VisitorPass(Pass.CLEAN_SIGS, job,
                              new AmbiguityRemover(job, ts, nf, AmbiguityRemover.SIGNATURES)));
	l.add(new VisitorPass(Pass.ADD_MEMBERS, job, new AddMemberVisitor(job, ts, nf)));
	l.add(new BarrierPass(Pass.ADD_MEMBERS_ALL, job));
	l.add(new VisitorPass(Pass.DISAM, job, new
                              AmbiguityRemover(job, ts, nf, AmbiguityRemover.ALL)));
	l.add(new BarrierPass(Pass.DISAM_ALL, job));
        l.add(new VisitorPass(Pass.TYPE_CHECK, job, new TypeChecker(job, ts, nf)));
	l.add(new VisitorPass(Pass.EXC_CHECK, job, new ExceptionChecker(ts, compiler.errorQueue())));
        l.add(new VisitorPass(Pass.REACH_CHECK, job, new ReachChecker(job, ts, nf)));
        l.add(new VisitorPass(Pass.EXIT_CHECK, job, new ExitChecker(job, ts, nf)));
        l.add(new VisitorPass(Pass.INIT_CHECK, job, new InitChecker(job, ts, nf)));
        l.add(new VisitorPass(Pass.CONSTRUCTOR_CHECK, job, new ConstructorCallChecker(job, ts, nf)));
	l.add(new BarrierPass(Pass.PRE_OUTPUT_ALL, job));

	if (compiler.serializeClassInfo()) {
	    l.add(new VisitorPass(Pass.SERIALIZE,
				  job, new ClassSerializer(ts, nf,
							   job.source().lastModified(),
							   compiler.errorQueue(),
                                                           version())));
	}

	l.add(new OutputPass(Pass.OUTPUT, job,
                             new Translator(job, ts, nf, targetFactory())));

        return l;
    }

    static { Topics t = new Topics(); }
}