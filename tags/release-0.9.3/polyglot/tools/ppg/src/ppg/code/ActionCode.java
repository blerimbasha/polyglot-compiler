package polyglot.util.ppg.code;

public class ActionCode extends Code
{
	public ActionCode (String actionCode) {
		value = actionCode;
	}

		return new ActionCode(value.toString());	
	}
	public String toString () {
		return "action code {:\n" + value + "\n:}\n";
	}
}