/*
 * CatchBlock.java
 */

package jltools.ast;

import jltools.util.CodeWriter;
import jltools.types.LocalContext;
import jltools.visit.SymbolReader;

/**
 * Overview: Represents a mutable pair of BlockStatements and
 * FormalParameters which represent a catch block.
 */

public class CatchBlock extends Node {
  
  /**
   * Effects: creates a new CatchBlock with FormalParameter
   * <formalParameter> and BlockStatement <block>.
   */
  public CatchBlock(FormalParameter formalParameter,
		    BlockStatement block) {
    this.formalParameter = formalParameter;
    this.block = block;
  }

  /**
   * Effects: returns the FormalParameter associated with this
   * CatchBlock.
   */
  public FormalParameter getFormalParameter() {
    return formalParameter;
  }

  /**
   * Effects: sets the FormalParameter associated with this CatchBlock
   * to <newParam>.
   */
  public void setFormalParameter(FormalParameter newParam) {
    formalParameter = newParam;
  }

  /**
   * Effects: returns the BlockStatement for this.
   */
  public BlockStatement getBlockStatement() {
    return block;
  }

  /**
   * Effects: sets the block statement for this to be <newBlock>.
   */
  public void setBlockStatement(BlockStatement newBlock) {
    block = newBlock;
  }

  public void translate ( LocalContext c, CodeWriter w)
  {
    w.write ( " catch ( " );
    formalParameter.translate( c, w);
    w.write ( " )");
    w.beginBlock();
    block.translate (c, w);
    w.endBlock();
  }
  
  public Node dump( CodeWriter w)
  {
    w.write( "( CATCH BLOCK ");
    dumpNodeInfo( w);
    w.write( ")"); 
    return null;
  }
  
  public Node readSymbols( SymbolReader sr)
  {
    return this;
  }

  public Node typeCheck( LocalContext c)
  {
    // FIXME: implement;
    return this;
  }


  public void visitChildren(NodeVisitor v) {
    formalParameter.setType((TypeNode) formalParameter.getTypeNode().visit(v));
    block = (BlockStatement) block.visit(v);
  }

  public Node copy() {
    CatchBlock cb = new CatchBlock(formalParameter, block);
    cb.copyAnnotationsFrom(this);
    return cb;
  }

  public Node deepCopy() {
    CatchBlock cb = 
      new CatchBlock((FormalParameter)formalParameter.deepCopy(), 
		     (BlockStatement) block.deepCopy());
    cb.copyAnnotationsFrom(this);
    return cb;
  }

  private FormalParameter formalParameter;
  private BlockStatement block;
}
