/* The following code was generated by JFlex 1.3.2 on 12/10/01 2:04 PM */

package polyglot.util.ppg.lex;

import java.io.InputStream;
import polyglot.util.ppg.parse.*;
//import polyglot.util.Position;


/**
 * This class is a scanner generated by 
 * <a href="http://www.jflex.de/">JFlex</a> 1.3.2
 * on 12/10/01 2:04 PM from the specification file
 * <tt>file:/c:/michael/research/polyglot/polyglot/util/ppg/lex/ppg.flex</tt>
 */
public class Lexer {

  /** This character denotes the end of file */
  final public static int YYEOF = -1;

  /** initial size of the lookahead buffer */
  final private static int YY_BUFFERSIZE = 16384;

  /** lexical states */
  final public static int CODE = 3;
  final public static int STRING = 2;
  final public static int YYINITIAL = 0;
  final public static int COMMENT = 1;

  /** 
   * Translates characters to character classes
   */
  final private static char [] yycmap = {
     0,  0,  0,  0,  0,  0,  0,  0,  0,  4,  6,  0,  4,  4,  0,  0, 
     0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0,  0, 
    12, 11,  7, 11, 11, 11, 11, 11, 11, 11,  8, 11, 41, 11, 40,  5, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  9, 39, 11, 38, 11, 11, 
    18,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2, 14,  2, 
     2,  2,  2,  2,  2,  2,  2,  2,  2,  2,  2, 43, 13, 44, 17,  3, 
    18, 30,  2, 20, 23, 24, 32, 34, 37, 19,  2, 33, 21, 35, 15, 27, 
    28,  2, 26, 31, 16, 22, 29, 36, 25,  2,  2, 42, 45, 10, 18,  0
  };

  /** 
   * Translates a state to a row index in the transition table
   */
  final private static int yy_rowMap [] = { 
        0,    46,    92,   138,   184,   230,   276,   322,   184,   184, 
      368,   184,   414,   460,   506,   552,   598,   644,   690,   736, 
      782,   828,   874,   920,   966,   184,   184,   184,  1012,   184, 
      184,   184,  1058,  1058,   184,  1104,   184,  1150,   184,  1196, 
     1242,  1242,  1288,  1334,   184,  1380,  1426,  1472,  1518,   230, 
     1564,  1610,  1656,  1702,  1748,  1794,  1840,  1886,  1932,  1978, 
     2024,  2070,  2116,  2162,   184,  1104,   184,  2208,  2254,  2300, 
     1288,   184,   184,  2346,  2392,  2438,  2484,  2530,  2576,  2622, 
     2668,  2714,  2760,  2806,  2852,  2898,  2944,  2990,  3036,  3082, 
     3128,  3174,  3220,  3266,  3312,  3358,  3404,   230,  3450,  3496, 
      230,   230,   230,  3542,  3588,  3634,  3680,  3726,  3772,  3818, 
     3864,   230,   230,  3910,  3956,  4002,  4048,  4094,  4140,  4186, 
      230,  4232,  4278,  4324,  4370,  4416,   230,  4462,  4508,  4554, 
     4600,  4646,   230,   230,  4692,  4738,  4784,   230,   230,  4830, 
     4876,  4922,  4968,   230,  5014,  5060,   230,  5106,   230,   230, 
      230,   230,  5152,  5198,  5244,  5290,   230,   230
  };

  /** 
   * The packed transition table of the DFA (part 0)
   */
  final private static String yy_packed0 = 
    "\2\5\1\6\1\5\1\7\1\10\1\7\1\11\1\12"+
    "\1\13\1\14\1\5\1\7\1\5\1\6\1\15\1\16"+
    "\2\5\1\17\1\20\1\21\1\6\1\22\1\23\1\6"+
    "\1\24\1\25\1\26\1\6\1\27\1\30\4\6\1\31"+
    "\1\6\1\5\1\32\1\33\1\34\1\35\1\36\1\37"+
    "\1\40\6\41\1\42\1\43\1\44\45\41\1\45\3\46"+
    "\1\45\1\46\1\0\1\47\5\46\1\50\40\46\6\51"+
    "\1\52\2\51\1\53\44\51\57\0\3\6\12\0\3\6"+
    "\2\0\23\6\14\0\1\7\1\0\1\7\5\0\1\7"+
    "\46\0\1\54\2\0\1\55\56\0\1\56\45\0\3\6"+
    "\12\0\3\6\2\0\10\6\1\57\12\6\11\0\3\6"+
    "\12\0\3\6\2\0\5\6\1\60\1\6\1\61\1\62"+
    "\12\6\11\0\3\6\12\0\1\6\1\63\1\6\2\0"+
    "\20\6\1\64\2\6\11\0\3\6\12\0\3\6\2\0"+
    "\10\6\1\65\12\6\11\0\3\6\12\0\3\6\2\0"+
    "\5\6\1\66\15\6\11\0\3\6\12\0\3\6\2\0"+
    "\7\6\1\67\13\6\11\0\3\6\12\0\3\6\2\0"+
    "\6\6\1\70\14\6\11\0\3\6\12\0\3\6\2\0"+
    "\1\71\22\6\11\0\3\6\12\0\3\6\2\0\12\6"+
    "\1\72\10\6\11\0\3\6\12\0\3\6\2\0\7\6"+
    "\1\73\3\6\1\74\7\6\11\0\3\6\12\0\3\6"+
    "\2\0\1\6\1\75\21\6\11\0\3\6\12\0\2\6"+
    "\1\76\2\0\1\6\1\77\21\6\11\0\3\6\12\0"+
    "\3\6\2\0\1\100\22\6\21\0\1\101\44\0\7\42"+
    "\1\0\1\102\52\42\1\103\1\42\1\0\1\102\45\42"+
    "\1\0\3\46\1\0\1\46\1\0\1\47\5\46\1\50"+
    "\40\46\1\0\1\104\2\0\1\105\1\0\1\105\1\46"+
    "\4\0\1\105\4\46\1\106\34\0\11\52\1\107\53\52"+
    "\1\0\1\52\1\107\1\110\43\52\6\54\1\0\47\54"+
    "\46\0\1\111\10\0\3\6\12\0\1\6\1\112\1\6"+
    "\2\0\23\6\11\0\3\6\12\0\3\6\2\0\7\6"+
    "\1\113\13\6\11\0\3\6\12\0\3\6\2\0\13\6"+
    "\1\114\7\6\11\0\3\6\12\0\3\6\2\0\1\115"+
    "\1\116\21\6\11\0\3\6\12\0\3\6\2\0\11\6"+
    "\1\117\11\6\11\0\3\6\12\0\3\6\2\0\4\6"+
    "\1\120\16\6\11\0\3\6\12\0\3\6\2\0\15\6"+
    "\1\121\5\6\11\0\3\6\12\0\3\6\2\0\10\6"+
    "\1\122\12\6\11\0\3\6\12\0\2\6\1\123\2\0"+
    "\23\6\11\0\3\6\12\0\3\6\2\0\17\6\1\124"+
    "\3\6\11\0\3\6\12\0\3\6\2\0\5\6\1\125"+
    "\15\6\11\0\3\6\12\0\3\6\2\0\5\6\1\126"+
    "\15\6\11\0\3\6\12\0\3\6\2\0\1\6\1\127"+
    "\5\6\1\130\13\6\11\0\3\6\12\0\2\6\1\131"+
    "\2\0\23\6\11\0\3\6\12\0\3\6\2\0\13\6"+
    "\1\132\7\6\11\0\3\6\12\0\3\6\2\0\13\6"+
    "\1\133\7\6\11\0\3\6\12\0\2\6\1\134\2\0"+
    "\23\6\11\0\1\135\60\0\1\105\1\0\1\105\5\0"+
    "\1\105\1\46\42\0\2\46\6\0\1\46\2\0\31\46"+
    "\4\0\4\46\1\0\3\6\12\0\2\6\1\136\2\0"+
    "\13\6\1\137\7\6\11\0\3\6\12\0\3\6\2\0"+
    "\20\6\1\140\2\6\11\0\3\6\12\0\1\6\1\141"+
    "\1\6\2\0\23\6\11\0\3\6\12\0\2\6\1\142"+
    "\2\0\23\6\11\0\3\6\12\0\3\6\2\0\2\6"+
    "\1\143\20\6\11\0\3\6\12\0\3\6\2\0\10\6"+
    "\1\144\12\6\11\0\3\6\12\0\3\6\2\0\5\6"+
    "\1\145\15\6\11\0\3\6\12\0\2\6\1\146\2\0"+
    "\23\6\11\0\3\6\12\0\3\6\2\0\11\6\1\147"+
    "\11\6\11\0\3\6\12\0\3\6\2\0\5\6\1\150"+
    "\15\6\11\0\3\6\12\0\3\6\2\0\22\6\1\151"+
    "\11\0\3\6\12\0\3\6\2\0\7\6\1\152\13\6"+
    "\11\0\3\6\12\0\3\6\2\0\1\6\1\153\21\6"+
    "\11\0\3\6\12\0\3\6\2\0\16\6\1\154\4\6"+
    "\11\0\3\6\12\0\3\6\2\0\14\6\1\155\6\6"+
    "\11\0\3\6\12\0\3\6\2\0\1\156\22\6\11\0"+
    "\3\6\12\0\3\6\2\0\7\6\1\157\13\6\11\0"+
    "\3\6\12\0\1\6\1\160\1\6\2\0\23\6\11\0"+
    "\3\6\12\0\3\6\2\0\22\6\1\161\11\0\1\46"+
    "\55\0\3\6\12\0\3\6\2\0\5\6\1\162\15\6"+
    "\11\0\3\6\12\0\3\6\2\0\14\6\1\163\6\6"+
    "\11\0\3\6\12\0\3\6\2\0\1\164\22\6\11\0"+
    "\3\6\12\0\3\6\2\0\14\6\1\165\6\6\11\0"+
    "\3\6\12\0\3\6\2\0\3\6\1\166\17\6\11\0"+
    "\3\6\12\0\3\6\2\0\7\6\1\167\13\6\11\0"+
    "\3\6\12\0\1\6\1\170\1\6\2\0\23\6\11\0"+
    "\3\6\12\0\2\6\1\171\2\0\23\6\11\0\3\6"+
    "\12\0\3\6\2\0\7\6\1\172\13\6\11\0\3\6"+
    "\12\0\3\6\2\0\5\6\1\173\15\6\11\0\3\6"+
    "\12\0\3\6\2\0\13\6\1\174\7\6\11\0\3\6"+
    "\12\0\3\6\2\0\5\6\1\175\15\6\11\0\3\6"+
    "\12\0\3\6\2\0\10\6\1\176\12\6\11\0\3\6"+
    "\12\0\2\6\1\177\2\0\23\6\11\0\3\6\12\0"+
    "\3\6\2\0\7\6\1\200\13\6\11\0\3\6\12\0"+
    "\3\6\2\0\14\6\1\201\6\6\11\0\3\6\12\0"+
    "\1\6\1\202\1\6\2\0\23\6\11\0\3\6\12\0"+
    "\3\6\2\0\15\6\1\203\5\6\11\0\3\6\12\0"+
    "\3\6\2\0\4\6\1\204\16\6\11\0\3\6\12\0"+
    "\2\6\1\205\2\0\23\6\11\0\3\6\12\0\3\6"+
    "\2\0\4\6\1\206\16\6\11\0\3\6\12\0\3\6"+
    "\2\0\1\207\22\6\11\0\3\6\12\0\3\6\2\0"+
    "\4\6\1\210\16\6\11\0\3\6\12\0\3\6\2\0"+
    "\17\6\1\211\3\6\11\0\3\6\12\0\3\6\2\0"+
    "\7\6\1\212\13\6\11\0\3\6\12\0\1\6\1\213"+
    "\1\6\2\0\23\6\11\0\3\6\12\0\3\6\2\0"+
    "\20\6\1\214\2\6\11\0\3\6\12\0\3\6\2\0"+
    "\10\6\1\215\12\6\11\0\3\6\12\0\3\6\2\0"+
    "\13\6\1\216\7\6\11\0\3\6\12\0\3\6\2\0"+
    "\5\6\1\217\15\6\11\0\3\6\12\0\3\6\2\0"+
    "\5\6\1\220\15\6\11\0\3\6\12\0\3\6\2\0"+
    "\4\6\1\221\16\6\11\0\3\6\12\0\3\6\2\0"+
    "\5\6\1\222\15\6\11\0\3\6\12\0\3\6\2\0"+
    "\5\6\1\223\15\6\11\0\3\6\12\0\3\6\2\0"+
    "\1\224\22\6\11\0\3\6\12\0\3\6\2\0\1\6"+
    "\1\225\21\6\11\0\3\6\12\0\3\6\2\0\2\6"+
    "\1\226\20\6\11\0\3\6\12\0\3\6\2\0\7\6"+
    "\1\227\13\6\11\0\3\6\12\0\3\6\2\0\5\6"+
    "\1\230\15\6\11\0\3\6\12\0\1\6\1\231\1\6"+
    "\2\0\23\6\11\0\3\6\12\0\1\6\1\232\1\6"+
    "\2\0\23\6\11\0\3\6\12\0\3\6\2\0\1\6"+
    "\1\233\21\6\11\0\3\6\12\0\3\6\2\0\13\6"+
    "\1\234\7\6\11\0\3\6\12\0\3\6\2\0\5\6"+
    "\1\235\15\6\11\0\3\6\12\0\3\6\2\0\2\6"+
    "\1\236\20\6\10\0";

  /** 
   * The transition table of the DFA
   */
  final private static int yytrans [] = yy_unpack();


  /* error codes */
  final private static int YY_UNKNOWN_ERROR = 0;
  final private static int YY_ILLEGAL_STATE = 1;
  final private static int YY_NO_MATCH = 2;
  final private static int YY_PUSHBACK_2BIG = 3;

  /* error messages for the codes above */
  final private static String YY_ERROR_MSG[] = {
    "Unkown internal scanner error",
    "Internal error: unknown state",
    "Error: could not match input",
    "Error: pushback value was too large"
  };

  /**
   * YY_ATTRIBUTE[aState] contains the attributes of state <code>aState</code>
   */
  private final static byte YY_ATTRIBUTE[] = {
     0,  0,  1,  0,  9,  1,  1,  1,  9,  9,  1,  9,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  9,  9,  9,  1,  9,  9,  9, 
     1,  0,  9,  1,  9,  1,  9,  1,  1,  0,  1,  1,  9,  0,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     9,  0,  9,  0,  0,  0,  0,  9,  9,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  0,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1, 
     1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1,  1
  };

  /** the input device */
  private java.io.Reader yy_reader;

  /** the current state of the DFA */
  private int yy_state;

  /** the current lexical state */
  private int yy_lexical_state = YYINITIAL;

  /** this buffer contains the current text to be matched and is
      the source of the yytext() string */
  private char yy_buffer[] = new char[YY_BUFFERSIZE];

  /** the textposition at the last accepting state */
  private int yy_markedPos;

  /** the textposition at the last state to be included in yytext */
  private int yy_pushbackPos;

  /** the current text position in the buffer */
  private int yy_currentPos;

  /** startRead marks the beginning of the yytext() string in the buffer */
  private int yy_startRead;

  /** endRead marks the last character in the buffer, that has been read
      from input */
  private int yy_endRead;

  /** number of newlines encountered up to the start of the matched text */
  private int yyline;

  /** the number of characters up to the start of the matched text */
  private int yychar;

  /**
   * the number of characters from the last newline up to the start of the 
   * matched text
   */
  private int yycolumn; 

  /** 
   * yy_atBOL == true <=> the scanner is currently at the beginning of a line
   */
  private boolean yy_atBOL = true;

  /** yy_atEOF == true <=> the scanner is at the EOF */
  private boolean yy_atEOF;

  /* user code: */

	private int lastId = -1;
	private String filename = "";
	private String lineSeparator;
/*
    private Position pos() {
        return new Position(filename, yyline+1, yycolumn);
    }
*/
	public Lexer(InputStream in, String filename) {
		this(in);
		this.filename = filename;
	}

	private void error(String message) throws LexicalError {
		throw new LexicalError(filename, yyline+1, message);
	}

	private Token t(int id, Object value) {
		lastId = id;
		return new Token(id, filename, yyline + 1, yychar, yychar + yylength(), value);
	}

	private Token t(int id) {
		return t(id, yytext());
	}



  /**
   * Creates a new scanner
   * There is also a java.io.InputStream version of this constructor.
   *
   * @param   in  the java.io.Reader to read input from.
   */
  public Lexer(java.io.Reader in) {
      lineSeparator = System.getProperty("line.separator", "\n");
    this.yy_reader = in;
  }

  /**
   * Creates a new scanner.
   * There is also java.io.Reader version of this constructor.
   *
   * @param   in  the java.io.Inputstream to read input from.
   */
  public Lexer(java.io.InputStream in) {
    this(new java.io.InputStreamReader(in));
  }

  /** 
   * Unpacks the split, compressed DFA transition table.
   *
   * @return the unpacked transition table
   */
  private static int [] yy_unpack() {
    int [] trans = new int[5336];
    int offset = 0;
    offset = yy_unpack(yy_packed0, offset, trans);
    return trans;
  }

  /** 
   * Unpacks the compressed DFA transition table.
   *
   * @param packed   the packed transition table
   * @return         the index of the last entry
   */
  private static int yy_unpack(String packed, int offset, int [] trans) {
    int i = 0;       /* index in packed string  */
    int j = offset;  /* index in unpacked array */
    int l = packed.length();
    while (i < l) {
      int count = packed.charAt(i++);
      int value = packed.charAt(i++);
      value--;
      do trans[j++] = value; while (--count > 0);
    }
    return j;
  }


  /**
   * Gets the next input character.
   *
   * @return      the next character of the input stream, EOF if the
   *              end of the stream is reached.
   * @exception   IOException  if any I/O-Error occurs
   */
  private int yy_advance() throws java.io.IOException {

    /* standard case */
    if (yy_currentPos < yy_endRead) return yy_buffer[yy_currentPos++];

    /* if the eof is reached, we don't need to work hard */ 
    if (yy_atEOF) return YYEOF;

    /* otherwise: need to refill the buffer */

    /* first: make room (if you can) */
    if (yy_startRead > 0) {
      System.arraycopy(yy_buffer, yy_startRead, 
                       yy_buffer, 0, 
                       yy_endRead-yy_startRead);

      /* translate stored positions */
      yy_endRead-= yy_startRead;
      yy_currentPos-= yy_startRead;
      yy_markedPos-= yy_startRead;
      yy_pushbackPos-= yy_startRead;
      yy_startRead = 0;
    }

    /* is the buffer big enough? */
    if (yy_currentPos >= yy_buffer.length) {
      /* if not: blow it up */
      char newBuffer[] = new char[yy_currentPos*2];
      System.arraycopy(yy_buffer, 0, newBuffer, 0, yy_buffer.length);
      yy_buffer = newBuffer;
    }

    /* finally: fill the buffer with new input */
    int numRead = yy_reader.read(yy_buffer, yy_endRead, 
                                            yy_buffer.length-yy_endRead);

    if ( numRead == -1 ) return YYEOF;

    yy_endRead+= numRead;

    return yy_buffer[yy_currentPos++];
  }


  /**
   * Closes the input stream.
   */
  final public void yyclose() throws java.io.IOException {
    yy_atEOF = true;            /* indicate end of file */
    yy_endRead = yy_startRead;  /* invalidate buffer    */

    if (yy_reader != null)
      yy_reader.close();
  }


  /**
   * Closes the current stream, and resets the
   * scanner to read from a new input stream.
   *
   * All internal variables are reset, the old input stream 
   * <b>cannot</b> be reused (internal buffer is discarded and lost).
   * Lexical state is set to <tt>YY_INITIAL</tt>.
   *
   * @param reader   the new input stream 
   */
  final public void yyreset(java.io.Reader reader) throws java.io.IOException {
    yyclose();
    yy_reader = reader;
    yy_atBOL  = true;
    yy_atEOF  = false;
    yy_endRead = yy_startRead = 0;
    yy_currentPos = yy_markedPos = yy_pushbackPos = 0;
    yyline = yychar = yycolumn = 0;
    yy_lexical_state = YYINITIAL;
  }


  /**
   * Returns the current lexical state.
   */
  final public int yystate() {
    return yy_lexical_state;
  }


  /**
   * Enters a new lexical state
   *
   * @param newState the new lexical state
   */
  final public void yybegin(int newState) {
    yy_lexical_state = newState;
  }


  /**
   * Returns the text matched by the current regular expression.
   */
  final public String yytext() {
    return new String( yy_buffer, yy_startRead, yy_markedPos-yy_startRead );
  }


  /**
   * Returns the character at position <tt>pos</tt> from the 
   * matched text. 
   * 
   * It is equivalent to yytext().charAt(pos), but faster
   *
   * @param pos the position of the character to fetch. 
   *            A value from 0 to yylength()-1.
   *
   * @return the character at position pos
   */
  final public char yycharat(int pos) {
    return yy_buffer[yy_startRead+pos];
  }


  /**
   * Returns the length of the matched text region.
   */
  final public int yylength() {
    return yy_markedPos-yy_startRead;
  }


  /**
   * Reports an error that occured while scanning.
   *
   * In a wellformed scanner (no or only correct usage of 
   * yypushback(int) and a match-all fallback rule) this method 
   * will only be called with things that "Can't Possibly Happen".
   * If this method is called, something is seriously wrong
   * (e.g. a JFlex bug producing a faulty scanner etc.).
   *
   * Usual syntax/scanner level error handling should be done
   * in error fallback rules.
   *
   * @param   errorCode  the code of the errormessage to display
   */
  private void yy_ScanError(int errorCode) {
    String message;
    try {
      message = YY_ERROR_MSG[errorCode];
    }
    catch (ArrayIndexOutOfBoundsException e) {
      message = YY_ERROR_MSG[YY_UNKNOWN_ERROR];
    }

    throw new Error(message);
  } 


  /**
   * Pushes the specified amount of characters back into the input stream.
   *
   * They will be read again by then next call of the scanning method
   *
   * @param number  the number of characters to be read again.
   *                This number must not be greater than yylength()!
   */
  private void yypushback(int number)  {
    if ( number > yylength() )
      yy_ScanError(YY_PUSHBACK_2BIG);

    yy_markedPos -= number;
  }


  /**
   * Resumes scanning until the next regular expression is matched,
   * the end of input is encountered or an I/O-Error occurs.
   *
   * @return      the next token
   * @exception   IOException  if any I/O-Error occurs
   */
  public Token getToken() throws java.io.IOException, 	LexicalError
 {
    int yy_input;
    int yy_action;


    while (true) {

      yychar+= yylength();

      boolean yy_r = false;
      for (yy_currentPos = yy_startRead; yy_currentPos < yy_markedPos;
                                                      yy_currentPos++) {
        switch (yy_buffer[yy_currentPos]) {
        case '\u000B':
        case '\u000C':
        case '\u0085':
        case '\u2028':
        case '\u2029':
          yyline++;
          yy_r = false;
          break;
        case '\r':
          yyline++;
          yy_r = true;
          break;
        case '\n':
          if (yy_r)
            yy_r = false;
          else {
            yyline++;
          }
          break;
        default:
          yy_r = false;
        }
      }

      if (yy_r) {
        if ( yy_advance() == '\n' ) yyline--;
        if ( !yy_atEOF ) yy_currentPos--;
      }

      yy_action = -1;

      yy_currentPos = yy_startRead = yy_markedPos;

      yy_state = yy_lexical_state;


      yy_forAction: {
        while (true) {

          yy_input = yy_advance();

          if ( yy_input == YYEOF ) break yy_forAction;

          int yy_next = yytrans[ yy_rowMap[yy_state] + yycmap[yy_input] ];
          if (yy_next == -1) break yy_forAction;
          yy_state = yy_next;

          int yy_attributes = YY_ATTRIBUTE[yy_state];
          if ( (yy_attributes & 1) > 0 ) {
            yy_action = yy_state; 
            yy_markedPos = yy_currentPos; 
            if ( (yy_attributes & 8) > 0 ) break yy_forAction;
          }

        }
      }


      switch (yy_action) {

        case 8: 
          { yybegin(STRING); }
        case 159: break;
        case 157: 
          {  return t(Constant.NONTERMINAL);  }
        case 160: break;
        case 156: 
          {  return t(Constant.PRECEDENCE);  }
        case 161: break;
        case 151: 
          {  return t(Constant.OVERRIDE);  }
        case 162: break;
        case 150: 
          {  return t(Constant.TRANSFER);  }
        case 163: break;
        case 149: 
          {  return t(Constant.TERMINAL);  }
        case 164: break;
        case 148: 
          {  return t(Constant.NONASSOC);  }
        case 165: break;
        case 146: 
          {  return t(Constant.PACKAGE);  }
        case 166: break;
        case 143: 
          {  return t(Constant.INCLUDE);  }
        case 167: break;
        case 138: 
          {  return t(Constant.ACTION);  }
        case 168: break;
        case 137: 
          {  return t(Constant.PARSER);  }
        case 169: break;
        case 133: 
          {  return t(Constant.EXTEND);  }
        case 170: break;
        case 132: 
          {  return t(Constant.IMPORT);  }
        case 171: break;
        case 31: 
          {  return t(Constant.BAR);  }
        case 172: break;
        case 30: 
          {  return t(Constant.RBRACK);  }
        case 173: break;
        case 29: 
          {  return t(Constant.LBRACK);  }
        case 174: break;
        case 28: 
          {  return t(Constant.LBRACE);  }
        case 175: break;
        case 27: 
          {  return t(Constant.COMMA);  }
        case 176: break;
        case 26: 
          {  return t(Constant.DOT);  }
        case 177: break;
        case 25: 
          {  return t(Constant.SEMI);  }
        case 178: break;
        case 5: 
        case 12: 
        case 13: 
        case 14: 
        case 15: 
        case 16: 
        case 17: 
        case 18: 
        case 19: 
        case 20: 
        case 21: 
        case 22: 
        case 23: 
        case 24: 
        case 46: 
        case 47: 
        case 48: 
        case 50: 
        case 51: 
        case 52: 
        case 53: 
        case 54: 
        case 55: 
        case 56: 
        case 57: 
        case 58: 
        case 59: 
        case 60: 
        case 61: 
        case 62: 
        case 63: 
        case 74: 
        case 75: 
        case 76: 
        case 77: 
        case 78: 
        case 79: 
        case 80: 
        case 81: 
        case 82: 
        case 83: 
        case 84: 
        case 85: 
        case 86: 
        case 87: 
        case 88: 
        case 89: 
        case 90: 
        case 91: 
        case 93: 
        case 94: 
        case 95: 
        case 96: 
        case 98: 
        case 99: 
        case 103: 
        case 104: 
        case 105: 
        case 106: 
        case 107: 
        case 108: 
        case 109: 
        case 110: 
        case 113: 
        case 114: 
        case 115: 
        case 116: 
        case 117: 
        case 118: 
        case 119: 
        case 121: 
        case 122: 
        case 123: 
        case 124: 
        case 125: 
        case 127: 
        case 128: 
        case 129: 
        case 130: 
        case 131: 
        case 134: 
        case 135: 
        case 136: 
        case 139: 
        case 140: 
        case 141: 
        case 142: 
        case 144: 
        case 145: 
        case 147: 
        case 152: 
        case 153: 
        case 154: 
        case 155: 
          {  return t(Constant.ID, yytext().intern());  }
        case 179: break;
        case 9: 
          {  return t(Constant.STAR);  }
        case 180: break;
        case 10: 
          {  return t(Constant.COLON);  }
        case 181: break;
        case 11: 
          {  return t(Constant.RBRACE);  }
        case 182: break;
        case 44: 
          {  yybegin (COMMENT);  }
        case 183: break;
        case 49: 
          {  return t(Constant.TO);  }
        case 184: break;
        case 64: 
          {  yybegin (CODE);  }
        case 185: break;
        case 66: 
          {  yybegin (YYINITIAL);  }
        case 186: break;
        case 72: 
          {  return t(Constant.COLON_COLON_EQUALS);  }
        case 187: break;
        case 73: 
          {  return t(Constant.NON);  }
        case 188: break;
        case 97: 
          {  return t(Constant.INIT);  }
        case 189: break;
        case 100: 
          {  return t(Constant.CODE);  }
        case 190: break;
        case 101: 
          {  return t(Constant.LEFT);  }
        case 191: break;
        case 102: 
          {  return t(Constant.DROP);  }
        case 192: break;
        case 111: 
          {  return t(Constant.SCAN);  }
        case 193: break;
        case 112: 
          {  return t(Constant.WITH);  }
        case 194: break;
        case 120: 
          {  return t(Constant.RIGHT);  }
        case 195: break;
        case 126: 
          {  return t(Constant.START);  }
        case 196: break;
        case 4: 
        case 7: 
          {  
	error("Invalid character: " + yytext());
 }
        case 197: break;
        case 38: 
          {  
	yybegin(YYINITIAL);
	String literal = yytext();
	return t(Constant.STRING_CONST, literal.substring(0, literal.length()-1));
 }
        case 198: break;
        case 36: 
          { 
	error("Illegal character in string literal: " + yytext());
 }
        case 199: break;
        case 32: 
        case 34: 
        case 35: 
          { 
	error("Illegal comment");
 }
        case 200: break;
        case 2: 
        case 37: 
          { 
	error("Unclosed string literal");
 }
        case 201: break;
        case 39: 
          { 
	error("Illegal escape character");
 }
        case 202: break;
        case 40: 
        case 42: 
          { 
	error("Invalid character in code block: '" + yytext() + "'");
 }
        case 203: break;
        case 71: 
          { 
	yybegin(YYINITIAL);
	String codeStr = yytext();
	// cut off ":}" from the end of the code string
	return t(Constant.CODE_STR, codeStr.substring(0, codeStr.length()-2));
 }
        case 204: break;
        case 6: 
        case 43: 
          {  }
        case 205: break;
        default: 
          if (yy_input == YYEOF && yy_startRead == yy_currentPos) {
            yy_atEOF = true;
              {     return t(Constant.EOF, "EOF");
    //return Constant.EOF;
 }
          } 
          else {
            yy_ScanError(YY_NO_MATCH);
          }
      }
    }
  }


}
