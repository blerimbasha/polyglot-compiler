package polyglot.util.ppg.spec;

import polyglot.util.ppg.*;
import polyglot.util.ppg.code.*;

public abstract class Spec implements Unparse
{
	protected Vector imports, symbols, prec;
	protected ActionCode actionCode;
	protected PPGSpec child;
		child = null;
	}
	public void setPkgName (String pkgName) {
			packageName = pkgName; 
	}
	
	public void replaceCode (Vector codeParts) {
		for (int i=0; i < codeParts.size(); i++) {
			try {
					if (code != null)
			} catch (Exception e) {
				System.err.println(PPG.HEADER+" Spec::replaceCode(): not a code segment "+
								   "found in code Vector: "+
				System.exit(1);
	public void addImports (Vector imp) {
		if (imp == null)
		
	
	public void setChild (PPGSpec childSpec) {
	}
	public void parseChain(String basePath) {}

	/**
}