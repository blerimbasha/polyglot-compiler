/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2007 Polyglot project group, Cornell University
 * Copyright (c) 2006-2007 IBM Corporation
 */
package polyglot.frontend;

/**
 * Version information for the base compiler.
 */
public class JLVersion extends polyglot.main.Version {
    public String name() { return "jl"; }
    public int major() { return 3; }
    public int minor() { return 0; }
    public int patch_level() { return 2; }
    public String toString() { return "3.0.2 (2009-05-28 15:57:19)"; }
}