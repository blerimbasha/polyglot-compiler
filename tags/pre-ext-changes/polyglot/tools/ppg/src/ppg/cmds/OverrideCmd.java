package jltools.util.jlgen.cmds;


public class OverrideCmd implements Command
{
	private Production prod;
	
	public OverrideCmd(Production p) 
	{
		prod = p;
	}

	public Nonterminal getLHS() { return prod.getLHS(); }
	
		//cw.begin(0);
		prod.unparse(cw);
	}
}