package jltools.util.jlgen.cmds;

import java.util.*;
import jltools.util.jlgen.atoms.*;

public class TransferCmd implements Command
{
	private Nonterminal nonterminal;
	private Vector transferList;
	
	public TransferCmd(String nt, Vector tlist) {
		nonterminal = new Nonterminal(nt);
		transferList = tlist;
	}

	public Nonterminal getSource() { return nonterminal; }
	
		//cw.begin(0);
		cw.allowBreak(2);
		cw.write(nonterminal + " to ");
		for (int i=0; i < transferList.size(); i++) {
			prod = (Production) transferList.elementAt(i);
		}
	}
}