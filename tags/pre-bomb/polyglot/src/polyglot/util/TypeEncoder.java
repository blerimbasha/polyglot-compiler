package jltools.util;

import jltools.types.*;

import java.io.*;
import java.util.zip.*;

/**
 * The <code>TypeEncoder</code> gives the ability to encode a jltools 
 * <code>Type</code> as a Java string.
 * <p>
 * It uses a form of serialization to encode the <code>Type</code> into
 * a byte stream and then converts the byte stream to a standard Java string.
 * <p>
 * The difference between the encoder and a normal serialization process is
 * that in order to encode this type, we need to sever any links to other types
 * in the current environment. So any <code>ClassType</code> other than the 
 * the type being encoded is replaced in the stream with an 
 * <code>AmbiguousType</code> that contains the fully qualified name.
 */
public class TypeEncoder
{
  protected TypeSystem ts;

  public TypeEncoder( TypeSystem ts)
  {
    this.ts = ts;
  }

  public String encode( Type t) throws IOException
  {
    ByteArrayOutputStream baos;
    ObjectOutputStream oos;
    byte[] b;
    StringBuffer sb;

    baos = new ByteArrayOutputStream();
    oos = new TypeOutputStream( new GZIPOutputStream( baos), ts, t);

    oos.writeObject( t);
    oos.flush();
    oos.close();
    b = baos.toByteArray();

    sb = new StringBuffer();
    for (int i = 0; i < b.length; i++)
	sb.append((char) b[i]);
    return sb.toString();
  }

  public Type decode( String s) throws IOException, ClassNotFoundException
  {
    char[] source;
    byte[] b;
    ObjectInputStream ois;

    source = s.toCharArray();
    b = new byte[ source.length];
    for (int i = 0; i < source.length; i++)
	b[i] = (byte) source[i];

    ois = new TypeInputStream( new GZIPInputStream( 
                                 new ByteArrayInputStream( b)), ts);

    return (Type)ois.readObject();
  }
}