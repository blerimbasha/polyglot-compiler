/*
 * Assert.java
 */

package jltools.util;

/**
 * Assert
 *
 * Overview:
 *    Assert contains a few methods helpful for implementing assertions in
 *    Java.
 **/
public final class Assert {

  /**
   * static void assert(boolean ok)
   *
   * Throws an error if not <ok>.
   **/
  public static void assert(boolean ok) {
    if (!ok)
      throw new AssertionFailedError("Assertion failed");
  }

  /**
   * static void assert(String condition, boolean ok)
   *
   * Asserts that <condition> holds -- in other words, that <ok> is true.
   * Throws an error otherwise.
   **/
  public static void assert(String condition, boolean ok) {
    if (!ok)
      throw new AssertionFailedError("Assertion \"" + 
				     condition + "\" failed.");
  }
  
  // This class cannot be instantiated.
  private Assert() {}
  // The error thrown.
  private static class AssertionFailedError extends Error {
    public AssertionFailedError() { super(); }
    public AssertionFailedError(String s) { super(s); }
  }

}


