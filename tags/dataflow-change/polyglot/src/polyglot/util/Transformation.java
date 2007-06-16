/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

/*
 * Transformation.java
 */

package polyglot.util;

/**
 * Transformation
 *
 * Overview:
 *     This interface provides a general means for transforming objects.
 *     The object 'NOTHING' should be returned if an object is to be removed.
 **/
public interface Transformation { 
  public static final Object NOTHING = new Object();
  public Object transform(Object o);  
}


