package jltools.util;

import java.util.*;

public class CollectionUtil
{
	public static List add(List l, Object o) {
		l.add(o);
		return l;
	}

	/**
	 * Return true if <code>a</code> and <code>b</code> are
	 * pointer equal, or if iterators over both return the same
	 * sequence of pointer equal elements.
	 */
	public static boolean equals(Collection a, Collection b) {
		if (a == b) {
			return true;
		}
		
		// the case where both are null is handled in the previous if.
		if (a == null ^ b == null) {
			return false;
		}
		
		Iterator i = a.iterator();
		Iterator j = b.iterator();

		while (i.hasNext() && j.hasNext()) {
			Object o = i.next();
			Object p = j.next();

			if (o != p) {
				return false;
			}
		}

		if (i.hasNext() || j.hasNext()) {
			return false;
		}

		return true;
	}

	public static List list(Object o) {
		return Collections.singletonList(o);
	}

	public static List list(Object o1, Object o2) {
		List l = new ArrayList(2);
		l.add(o1);
		l.add(o2);
		return l;
	}

	public static List list(Object o1, Object o2, Object o3) {
		List l = new ArrayList(3);
		l.add(o1);
		l.add(o2);
		l.add(o3);
		return l;
	}

	public static List list(Object o1, Object o2, Object o3, Object o4) {
		List l = new ArrayList(3);
		l.add(o1);
		l.add(o2);
		l.add(o3);
		l.add(o4);
		return l;
	}
}
