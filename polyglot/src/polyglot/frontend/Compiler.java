package jltools.frontend;

import jltools.ast.*;
import jltools.types.*;
import jltools.types.reflect.*;
import jltools.util.*;
import jltools.visit.*;
import jltools.main.Options;
import jltools.main.Report;

import java.io.*;
import java.util.*;

/**
 * This is the main entry point for the compiler. It contains a work list that
 * contains entries for all classes that must be compiled (or otherwise worked
 * on).
 */
public class Compiler
{
    /** Command-line options */
    private Options options;

    /** The error queue handles outputting error messages. */
    private ErrorQueue eq;

    /**
     * Class file loader.  There should be only one of these so we can cache
     * across type systems.
     */
    private ClassFileLoader loader;

    /**
     * The output files generated by the compiler.  This is used to to call the
     * post-compiler (e.g., javac).
     */
    private Collection outputFiles = new HashSet();

    /**
     * Initialize the compiler.
     *
     * @param options Contains jltools options
     */
    public Compiler(Options options_) {
	options = options_;

	eq = new StdErrorQueue(System.err, options.error_count,
                               options.extension.compilerName());

        loader = new ClassFileLoader();

	// This must be done last.
	options.extension.initCompiler(this);
    }

    /** Return a set of output filenames resulting from a compilation. */
    public Collection outputFiles() {
	return outputFiles;
    }

    /**
     * Compile all the files listed in the set of strings <code>source</code>.
     * Return true on success. The method <code>outputFiles</code> can be
     * used to obtain the output of the compilation.  This is the main entry
     * point for the compiler, called from main().
     */
    public boolean compile(Collection sources) {
	boolean okay = false;

	try {
	    try {
                SourceLoader source_loader = sourceExtension().sourceLoader();

		for (Iterator i = sources.iterator(); i.hasNext(); ) {
		    String sourceName = (String) i.next();
		    Source source = source_loader.fileSource(sourceName);
		    sourceExtension().addJob(source);
		}

		okay = sourceExtension().runToCompletion();
	    }
	    catch (FileNotFoundException e) {
		eq.enqueue(ErrorInfo.IO_ERROR,
		    "Cannot find source file \"" + e.getMessage() + "\".");
	    }
	    catch (IOException e) {
		eq.enqueue(ErrorInfo.IO_ERROR, e.getMessage());
	    }
	    catch (InternalCompilerError e) {
		e.printStackTrace();
		eq.enqueue(ErrorInfo.INTERNAL_ERROR, e.message(), e.position());
	    }
	}
	catch (ErrorLimitError e) {
	}

	eq.flush();
	return okay;
    }

    /** Get the compiler's class file loader. */
    public ClassFileLoader loader() {
        return this.loader;
    }

    /** Should fully qualified class names be used in the output? */
    public boolean useFullyQualifiedNames() {
        return options.fully_qualified_names;
    }

    /** Get information about the language extension being compiled. */
    public ExtensionInfo sourceExtension() {
	return options.extension;
    }

    /** Maximum number of characters on each line of output */
    public int outputWidth() {
        return options.output_width;
    }

    /** Should class info be serialized into the output? */
    public boolean serializeClassInfo() {
	return options.serialize_type_info;
    }

    /** Should the AST be dumped? */
    public boolean dumpAst() {
        return options.dump_ast;
    }

    /** Get the compiler's error queue. */
    public ErrorQueue errorQueue() {
	return eq;
    }

    private static Collection topics = new ArrayList(1);
    private static Collection timeTopics = new ArrayList(1);

    static {
	topics.add("frontend");
	timeTopics.add("time");
    }

    /** Debug reporting for the frontend. */
    public static void report(int level, String msg) {
	Report.report(topics, level, msg);
    }

    /** Reports the time taken by every pass. */
    public static void reportTime(int level, String msg) {
	Report.report(timeTopics, level, msg);
    }

    static {
      // FIXME: if we get an io error (due to too many files open, for example)
      // it will throw an exception. but, we won't be able to do anything with
      // it since the exception handlers will want to load
      // jltools.util.CodeWriter and jltools.util.ErrorInfo to print and
      // enqueue the error; but the classes must be in memory since the io
      // can't open any files; thus, we force the classloader to load the class
      // file.
      try {
	ClassLoader loader = Compiler.class.getClassLoader();
	// loader.loadClass("jltools.util.CodeWriter");
	// loader.loadClass("jltools.util.ErrorInfo");
	loader.loadClass("jltools.util.StdErrorQueue");
      }
      catch (ClassNotFoundException e) {
	throw new InternalCompilerError(e.getMessage());
      }
    }
}
