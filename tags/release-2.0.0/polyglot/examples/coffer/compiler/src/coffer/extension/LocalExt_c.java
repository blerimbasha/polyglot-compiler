package coffer.extension;

import polyglot.ast.*;
import polyglot.types.*;
import polyglot.util.*;
import coffer.ast.*;
import coffer.types.*;

import java.util.*;

public class LocalExt_c extends CofferExt_c {
    public void checkHeldKeys(KeySet held, KeySet stored) throws SemanticException {
        Local e = (Local) node();

        if (e.type() instanceof CofferClassType) {
            Key key = ((CofferClassType) e.type()).key();

            if (key != null) {
                if (! stored.contains(key)) {
                    throw new SemanticException(
                        "Can evaluate expression of type \"" +
                        e.type() + "\" only if key \"" + key +
                        "\" is held by \"" + e.name() + "\".", e.position());

                }
            }
        }
    }
}