package jltools.ast;

import jltools.frontend.Compiler;
import jltools.types.*;
import jltools.util.*;
import jltools.visit.*;
import jltools.main.Main;

/**
 * An <code>ImportNode</code> is an immutable representation of a Java
 * <code>import</code> statement.  It consists of the string representing the
 * item being imported and the kind  which is either indicating that a class
 * is being imported, or that an entire package is being imported.
 */

public class ImportNode extends Node 
{
  /** Indicates that a single class is being imported. */
  public static final int CLASS = 0;
  /** Indicates that an entire package is being imported. */
  public static final int PACKAGE = 1;
 
  protected static final int MAX_KIND = PACKAGE;

  protected final int kind;
  protected final String imports;

  /**
   * Creates new <code>ImportNode</code>.
   */
  public ImportNode( Node ext, int kind, String imports) 
  { 
    if (kind < 0 || kind > MAX_KIND) {
      throw new IllegalArgumentException ("Invalid kind for ImportNode.");
    }
    this.ext = ext;
    this.kind = kind;
    this.imports = imports;
  }

    public ImportNode( int kind, String imports) {
	this(null, kind, imports);
    }

  public ImportNode reconstruct( Node ext, int kind, String imports) 
  {
    if( this.kind == kind && this.ext == ext && this.imports.equals( imports)) {
      return this;
    }
    else {
      ImportNode n = new ImportNode( ext, kind, imports);
      n.copyAnnotationsFrom( this);
      return n;
    }
  }

    public ImportNode reconstruct( int kind, String imports) {
	return reconstruct(this.ext, kind, imports);
    }

  /**
   * Returns the kind of this <code>ImportNode</code>.
   */
  public int getKind() 
  {
    return kind;
  }

  /**
   * Returns the string indicating the item being imported.
   */
  public String getImports() 
  {
    return imports;
  }

  /**
   * Visit the children of this node. 
   */
  public Node visitChildren( NodeVisitor v) 
  {
    return this;
  }

  public Node readSymbols( SymbolReader sr) throws SemanticException
  {
    ImportTable it = sr.getImportTable();

    switch( kind)
    {
    case CLASS:
      it.addClassImport( imports);
      break;

    case PACKAGE:
      it.addPackageImport( imports);
      break;
    }

    visitChildren(sr);

    return this;
  }
   
  public Node typeCheck( LocalContext c)
  {
    // FIXME; implement
    return this;
  }

  public void translate_no_override( LocalContext c, CodeWriter w)
  {
    if (!Main.options.fully_qualified_names) {
      w.write( "import " + imports + (kind == PACKAGE ? ".*;" : ";"));
      w.newline(0);
    }
  }

  public void dump( CodeWriter w)
  {
    w.write( "IMPORT < " + (kind == PACKAGE ? "PACKAGE" : "CLASS"));
    w.write( " > < " + imports + " > ");
    dumpNodeInfo( w);
  }
}

