package jltools.frontend;

import jltools.util.Enum;
import java.util.*;

/** A <code>Pass</code> represents a compiler pass. */
public interface Pass
{
    public static class ID extends Enum {
        public ID(String name) { super(name); }
    }

    public ID id();

    public static final ID PARSE = new ID("parse");
    public static final ID BUILD_TYPES = new ID("build-types");
    public static final ID BUILD_TYPES_ALL = new ID("build-types-barrier");
    public static final ID CLEAN_SUPER = new ID("clean-super");
    public static final ID CLEAN_SUPER_ALL = new ID("clean-super-barrier");
    public static final ID CLEAN_SIGS = new ID("clean-sigs");
    public static final ID ADD_MEMBERS = new ID("add-members");
    public static final ID ADD_MEMBERS_ALL = new ID("add-members-barrier");
    public static final ID DISAM = new ID("disam");
    public static final ID DISAM_ALL = new ID("disam-barrier");
    public static final ID TYPE_CHECK = new ID("type-check");
    public static final ID SET_EXPECTED_TYPES = new ID("set-expected-types");
    public static final ID EXC_CHECK = new ID("exc-check");
    public static final ID FOLD = new ID("fold");
    public static final ID DUMP = new ID("dump");
    public static final ID PRE_OUTPUT_ALL = new ID("pre-output-barrier");
    public static final ID SERIALIZE = new ID("serialize");
    public static final ID OUTPUT = new ID("output");

    /** Run the pass. */
    public boolean run();
}
