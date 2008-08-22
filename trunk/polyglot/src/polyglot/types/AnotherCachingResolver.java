/*
 * This file is part of the Polyglot extensible compiler framework.
 *
 * Copyright (c) 2000-2006 Polyglot project group, Cornell University
 * 
 */

package polyglot.types;

import java.util.*;

import polyglot.main.Report;
import polyglot.util.*;

/**
 * A <code>CachingResolver</code> memoizes another Resolver
 */
public class AnotherCachingResolver implements Resolver, Copy {

    protected Resolver inner;
    private Map<Name,Object> cache;
    private boolean cacheNotFound;

    /**
     * Create a caching resolver.
     * @param inner The resolver whose results this resolver caches.
     */
    public AnotherCachingResolver(Resolver inner, boolean cacheNotFound) {
	this.inner = inner;
	this.cacheNotFound = cacheNotFound;
	this.cache = new HashMap<Name, Object>();
    }

    public AnotherCachingResolver(Resolver inner) {
	this(inner, true);
    }

    protected boolean shouldReport(int level) {
	return Report.should_report(TOPICS, level);
    }

    public Object copy() {
	try {
	   AnotherCachingResolver r = (AnotherCachingResolver) super.clone();
	    r.cache = new HashMap<Name, Object>(this.cache);
	    return r;
	}
	catch (CloneNotSupportedException e) {
	    throw new InternalCompilerError("clone failed");
	}
    }

    /**
     * The resolver whose results this resolver caches.
     */
     public Resolver inner() {
	 return this.inner;
     }

     public String toString() {
	 return "(cache " + inner.toString() + ")";
     }

     protected Collection<Named> cachedObjects() {
	 ArrayList<Named> l = new ArrayList<Named>();
	 for (Object o : cache.values()) {
	     if (o instanceof Named)
		 l.add((Named) o);
	 }
	 return l;
     }

     /**
      * Find a type object by name.
      * @param name The name to search for.
      */
     public Named find(Matcher<Named> matcher) throws SemanticException {
	 Name name = matcher.name();
	 
	 if (shouldReport(2))
	     Report.report(2, "CachingResolver: find: " + name);

	 Object o = cache.get(name);

	 if (o instanceof SemanticException) throw ((SemanticException) o);

	 Named q = (Named) o;

	 if (q == null) {
	     if (shouldReport(3))
		 Report.report(3, "CachingResolver: not cached: " + name);

	     try {
		 q = inner.find(matcher);
	     }
	     catch (NoClassException e) {
		 if (shouldReport(3)) {
		     Report.report(3, "CachingResolver: " + e.getMessage());
		     Report.report(3, "CachingResolver: installing " + name + "-> (not found) in resolver cache");
		 }
		 if (cacheNotFound) {
		     cache.put(name, e);
		 }
		 throw e;
	     }

	     addNamed(name, q);

	     if (shouldReport(3))
		 Report.report(3, "CachingResolver: loaded: " + name);
	 }
	 else {
	     if (shouldReport(3))
		 Report.report(3, "CachingResolver: cached: " + name);
	 }

	 return q;
     }

     /**
      * Check if a type object is in the cache, returning null if not.
      * @param name The name to search for.
      */
     public Named check(Name name) {
	 Object o = cache.get(name);
	 if (o instanceof Throwable)
	     return null;
	 return (Named) o;
     }

     /**
      * Install a qualifier in the cache.
      * @param name The name of the qualifier to insert.
      * @param q The qualifier to insert.
      */
     public void install(Name name, Named q) {
	 if (shouldReport(3))
	     Report.report(3, "CachingResolver: installing " + name + "->" + q + " in resolver cache");
	 if (shouldReport(5))
	     new Exception().printStackTrace();

	 Object old = cache.get(name);
	 if (old != null && old != q && old instanceof Type)
	     assert false : name + "->" + old + " " + old.getClass().getName() + " is already in the cache; cannot replace with " + q + " " + q.getClass().getName();

	 cache.put(name, q);
     }

     /**
      * Install a qualifier in the cache.
      * @param name The name of the qualifier to insert.
      * @param q The qualifier to insert.
      */
     public void addNamed(Name name, Named q) throws SemanticException {
	 install(name, q);
     }

     public void dump() {
	 Report.report(1, "Dumping " + this);
	 for (Iterator i = cache.entrySet().iterator(); i.hasNext(); ) {
	     Map.Entry e = (Map.Entry) i.next();
	     Report.report(2, e.toString());
	 }
     }

     private static final Collection TOPICS =
	 CollectionUtil.list(Report.types,
	                     Report.resolver);
}