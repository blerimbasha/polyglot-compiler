package polyglot.util.ppg.atoms;

import java.util.*;

public class Precedence
{
	public static final int LEFT = 0;
	public static final int RIGHT = 1;
	public static final int NONASSOC = 2;
	
	private	int type;
	private Vector symbols;
	
	public Precedence(int type, Vector syms) {
		this.type = type;
		symbols = syms;
	}
	
		Vector newSyms = new Vector();
	}
	public String toString () {
		String result = "precedence ";
		switch (type) {
			case (LEFT): result += "left "; break;
			case (RIGHT): result += "right "; break;
			case (NONASSOC): result += "nonassoc "; break;
		}

		for (int i=0; i < symbols.size(); i++) {
			result += symbols.elementAt(i);
			if (i < symbols.size() - 1)
				result += ", ";
		}
		return result + ";";
	}
}