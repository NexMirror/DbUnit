// copyright 2001-2002 by The Mind Electric

package electric.util.encoding;

import java.io.*;
import java.util.*;
import electric.util.*;

/**
 * <tt>Encodings</tt> defines a set of static methods for deciphering encoding
 * information.  This includes mapping an encoding in Java to an encoding in xml,
 * switching back and forth between java and xml encoding, and manipulating the default
 * encodings to check through.
 *
 * @author <a href="http://www.themindelectric.com">The Mind Electric</a>
 */

public class Encodings
  {
  private static final String[] DEFAULT_SUGGESTED_ENCODINGS = new String[]
    {
    "UTF8", // ansi standard
    "UTF-16BE", // big endian utf-16 doesn't require BOM
    "UTF-16LE", // little endian utf-16 doesn't require BOM
    "cp037" // 7 bit charsets (EBCDIC-US)
    };

  private static String[] suggestedEncodings = new String[ 0 ];
  private static byte[][] suggestedEncodingBytes = new byte[ 0 ][ 0 ];
  private static final String ENCODING = "encoding";
  private static final String XML_DECL = "<?xml";
  private static final Hashtable xmlToJavaEncodings = new Hashtable();
  private static final Hashtable javaToXMLEncodings = new Hashtable();
  private static String systemEncoding;

  // *********** INITIALIZATION *********************************************

  static
    {
    initSystemEncoding();
    initXMLToJavaEncodings();
    }

  // ********** SUGGESTED ENCODINGS *****************************************

  /**
   * In order to know the encoding of an XML document, we must get the encoding
   * attribute of the xml decl.  In order to read the xml decl, we need to know
   * what encoding the xml document is in.  Since the first 5 characters of an
   * xml decl are guaranteed to be "<?xml", and the encoding must be in a latin
   * character set, we use suggestions about families of encodings to attempt to
   * decode the xml decl.  These families all share the same basic encoding for
   * the latin character set. UTF-8 is the default if we don't find an xml decl.
   * @param encoding
   */
  public static void addSuggestedEncoding( String encoding )
    throws UnsupportedEncodingException
    {
    byte[] bytes = XML_DECL.getBytes( encoding );
    suggestedEncodingBytes = (byte[][]) ArrayUtil.addElement( suggestedEncodingBytes, bytes );
    suggestedEncodings = (String[]) ArrayUtil.addElement( suggestedEncodings, encoding );
    }

  /**
   *
   */
  public static String[] getSuggestedEncodings()
    {
    return suggestedEncodings;
    }

  // ********** HEADER DECODING *********************************************

  /**
   * determine what encoding the byte array claims it's in.  this searches through
   * the bytes for the xml decl, and looks at the encoding stated there.
   * @param header
   */
  static public String getJavaEncoding( byte[] header )
    throws UnsupportedEncodingException
    {
    if( Strings.isUTF16( header ) )
      return "UTF-16";

    if( suggestedEncodingBytes.length == 0 )
      initSuggestedEncodings( DEFAULT_SUGGESTED_ENCODINGS );

    for( int i = 0; i < suggestedEncodingBytes.length; i++ )
      if( matches( header, suggestedEncodingBytes[ i ] ) )
        {
        String encoding = getJavaEncoding( header, suggestedEncodings[ i ] );

        if( encoding != null )
          return encoding;
        }

    return "UTF8";
    }

  /**
   * @param header
   * @param suggestedEncodingBytes
   */
  static private boolean matches( byte[] header, byte[] suggestedEncodingBytes )
    {
    // gg: this could be a utility method in ArrayUtil
    if( header.length < suggestedEncodingBytes.length )
      return false;

    for( int i = 0; i < suggestedEncodingBytes.length; i++ )
      if( header[ i ] != suggestedEncodingBytes[ i ] )
        return false;

    return true;
    }

  /**
   * @param header
   * @param suggestedEncoding
   */
  private static String getJavaEncoding( byte[] header, String suggestedEncoding )
    throws UnsupportedEncodingException
    {
    String string = new String( header, suggestedEncoding ).toLowerCase().trim();

    if( !string.startsWith( XML_DECL ) )
      return null;

    int index = string.indexOf( ENCODING );
    int endDecl = string.indexOf( "?>" );

    // no encoding means this is a malformed document, handle it as best we can
    if( index == -1 || endDecl < index )
      return suggestedEncoding;

    // possible bug here if spaces are allowed around equals on attributes
    int equals = string.indexOf( '=', index + ENCODING.length() );
    int apostrophe = string.indexOf( '\'', equals );
    int quote = string.indexOf( '\"', equals );
    int start = apostrophe + 1;
    char delimiter = '\'';

    // missing equals or both ' and " means malformed attribute in xml
    if( equals == -1 || apostrophe == -1 && quote == -1 )
      return suggestedEncoding;

    if( quote != -1 )
      if( apostrophe == -1 || quote < apostrophe )
        {
        start = quote + 1;
        delimiter = '\"';
        }

    int stop = string.indexOf( delimiter, start );
    return getJavaEncoding( string.substring( start, stop ) );
    }

  // ********** JAVA TO XML CHARACTER ENCODING CONVERSION *******************

  /**
   * @param xmlEncoding
   */
  public static String getJavaEncoding( String xmlEncoding )
    {
    String javaEncoding = (String) xmlToJavaEncodings.get( xmlEncoding );
    return (javaEncoding == null ? xmlEncoding : javaEncoding);
    }

  /**
   * @param javaEncoding
   */
  public static String getXMLEncoding( String javaEncoding )
    {
    String xmlEncoding = (String) javaToXMLEncodings.get( javaEncoding );
    return (xmlEncoding == null ? javaEncoding : xmlEncoding);
    }

  /**
   * @param xmlEncoding
   * @param javaEncoding
   */
  public static void addBidirectionalEncoding( String xmlEncoding, String javaEncoding )
    {
    xmlToJavaEncodings.put( xmlEncoding, javaEncoding );
    javaToXMLEncodings.put( javaEncoding, xmlEncoding );
    }

  /**
   * @param xmlEncoding
   * @param javaEncoding
   */
  public static void addXMLToJavaEncoding( String xmlEncoding, String javaEncoding )
    {
    xmlToJavaEncodings.put( xmlEncoding, javaEncoding );
    }

  /**
   * @param javaEncoding
   * @param xmlEncoding
   */
  public static void addJavaToXMLEncoding( String javaEncoding, String xmlEncoding )
    {
    javaToXMLEncodings.put( javaEncoding, xmlEncoding );
    }

  // ********** SYSTEM DEFAULT ENCODING *************************************

  /**
   * return the default java encoding for the system
   */
  public static String getSystemEncoding()
    {
    return systemEncoding;
    }

  /**
   * return the default xml encoding for the system
   */
  public static String getXMLSystemEncoding()
    {
    return getXMLEncoding( systemEncoding );
    }

  // *********** INITIALIZATION *********************************************

  /**
   *
   */
  private static void initSystemEncoding()
    {
    // unfortunately, there seems to be no straightforward way to get this info
    ByteArrayInputStream input = new ByteArrayInputStream( new byte[ 0 ] );
    InputStreamReader reader = new InputStreamReader( input );
    systemEncoding = reader.getEncoding();

    try
      {
      reader.close();
      }
    catch( IOException exception )
      {
      // should never happen
      }
    }

  /**
   * Warning! This is dangerous territory to play in.  If you don't
   * reinitialize suggestedEncodings after calling this method, every
   * document will be assumed to be UTF-8.
   */
  public static void clearSuggestedEncodings()
    {
    suggestedEncodings = new String[ 0 ];
    suggestedEncodingBytes = new byte[ 0 ][ 0 ];
    }

  /**
   * Add encodings to try when attempting to parse the document.  Returns
   * encodings that could not be resolved.
   * @param userSuggestedEncodings
   */
  public static String[] initSuggestedEncodings( String[] userSuggestedEncodings )
    {
    clearSuggestedEncodings();

    if( suggestedEncodingBytes.length > 0 )
      return new String[ 0 ];

    if( userSuggestedEncodings != null || userSuggestedEncodings.length == 0 )
      userSuggestedEncodings = DEFAULT_SUGGESTED_ENCODINGS;

    Vector unsupportedEncodings = new Vector();

    for( int i = 0; i < userSuggestedEncodings.length; i++ )
      try
        {
        addSuggestedEncoding( userSuggestedEncodings[ i ] );
        }
      catch( Throwable exception )
        {
        // must be logged by caller
        unsupportedEncodings.addElement( userSuggestedEncodings[ i ] );
        }

    String[] retVal = new String[ unsupportedEncodings.size() ];
    unsupportedEncodings.copyInto( retVal );
    return retVal;
    }

  /**
   *
   */
  public static void initXMLToJavaEncodings()
    {
    String string = System.getProperty( "java.version" );

    // if we're on 1.1, there is no MS932, so pretend it's Shift-JIS
    if( string.equals( "1.1" ) || string.startsWith( "1.1." ) )
      addXMLToJavaEncoding( "SJIS", "WINDOWS-31J" );
    else
      addBidirectionalEncoding( "MS932", "WINDOWS-31J" );

    addBidirectionalEncoding( "UTF-8", "UTF8" );
    addBidirectionalEncoding( "US-ASCII", "ASCII" );
    addBidirectionalEncoding( "WINDOWS-1250", "Cp1250" );
    addBidirectionalEncoding( "WINDOWS-1251", "Cp1251" );
    addBidirectionalEncoding( "WINDOWS-1252", "Cp1252" );
    addBidirectionalEncoding( "WINDOWS-1253", "Cp1253" );
    addBidirectionalEncoding( "WINDOWS-1254", "Cp1254" );
    addBidirectionalEncoding( "WINDOWS-1255", "Cp1255" );
    addBidirectionalEncoding( "WINDOWS-1256", "Cp1256" );
    addBidirectionalEncoding( "WINDOWS-1257", "Cp1257" );
    addBidirectionalEncoding( "WINDOWS-1258", "Cp1258" );
    addBidirectionalEncoding( "EBCDIC-CP-US", "CP037" );
    addBidirectionalEncoding( "EBCDIC-CP-DK", "CP277" );
    addBidirectionalEncoding( "EBCDIC-CP-FI", "CP278" );
    addBidirectionalEncoding( "EBCDIC-CP-IT", "CP280" );
    addBidirectionalEncoding( "EBCDIC-CP-ES", "CP284" );
    addBidirectionalEncoding( "EBCDIC-CP-GB", "CP285" );
    addBidirectionalEncoding( "EBCDIC-CP-FR", "CP297" );
    addBidirectionalEncoding( "EBCDIC-CP-AR1", "CP420" );
    addBidirectionalEncoding( "EBCDIC-CP-HE", "CP424" );
    addBidirectionalEncoding( "EBCDIC-CP-CH", "CP500" );
    addBidirectionalEncoding( "CP-AR", "CP868" );
    addBidirectionalEncoding( "CP-GR", "CP869" );
    addBidirectionalEncoding( "EBCDIC-CP-ROECE", "CP870" );
    addBidirectionalEncoding( "EBCDIC-CP-IS", "CP871" );
    addBidirectionalEncoding( "EBCDIC-CP-AR2", "CP918" );
    addBidirectionalEncoding( "TIS-620", "TIS620" );
    addBidirectionalEncoding( "ISO-2022-CN", "ISO2022CN" );
    addBidirectionalEncoding( "X0201", "JIS0201" );
    addBidirectionalEncoding( "X0208", "JIS0208" );
    addBidirectionalEncoding( "ISO-IR-159", "JIS0212" );

    addXMLToJavaEncoding( "ISO-IR-100", "ISO8859_1" );
    addXMLToJavaEncoding( "ISO_8859-1", "ISO8859_1" );
    addXMLToJavaEncoding( "LATIN1", "ISO8859_1" );
    addXMLToJavaEncoding( "L1", "ISO8859_1" );
    addXMLToJavaEncoding( "IBM819", "ISO8859_1" );
    addXMLToJavaEncoding( "CP819", "ISO8859_1" );
    addXMLToJavaEncoding( "ISO-IR-101", "ISO8859_2" );
    addXMLToJavaEncoding( "ISO_8859-2", "ISO8859_2" );
    addXMLToJavaEncoding( "LATIN2", "ISO8859_2" );
    addXMLToJavaEncoding( "L2", "ISO8859_2" );
    addXMLToJavaEncoding( "ISO-IR-109", "ISO8859_3" );
    addXMLToJavaEncoding( "ISO_8859-3", "ISO8859_3" );
    addXMLToJavaEncoding( "LATIN3", "ISO8859_3" );
    addXMLToJavaEncoding( "L3", "ISO8859_3" );
    addXMLToJavaEncoding( "ISO-IR-110", "ISO8859_4" );
    addXMLToJavaEncoding( "ISO_8859-4", "ISO8859_4" );
    addXMLToJavaEncoding( "LATIN4", "ISO8859_4" );
    addXMLToJavaEncoding( "L4", "ISO8859_4" );
    addXMLToJavaEncoding( "ISO-IR-144", "ISO8859_5" );
    addXMLToJavaEncoding( "ISO_8859-5", "ISO8859_5" );
    addXMLToJavaEncoding( "CYRILLIC", "ISO8859_5" );
    addXMLToJavaEncoding( "ISO-IR-127", "ISO8859_6" );
    addXMLToJavaEncoding( "ISO_8859-6", "ISO8859_6" );
    addXMLToJavaEncoding( "ECMA-114", "ISO8859_6" );
    addXMLToJavaEncoding( "ASMO-708", "ISO8859_6" );
    addXMLToJavaEncoding( "ARABIC", "ISO8859_6" );
    addXMLToJavaEncoding( "ISO-IR-126", "ISO8859_7" );
    addXMLToJavaEncoding( "ISO_8859-7", "ISO8859_7" );
    addXMLToJavaEncoding( "ELOT_928", "ISO8859_7" );
    addXMLToJavaEncoding( "ECMA-118", "ISO8859_7" );
    addXMLToJavaEncoding( "GREEK", "ISO8859_7" );
    addXMLToJavaEncoding( "GREEK8", "ISO8859_7" );
    addXMLToJavaEncoding( "ISO-IR-138", "ISO8859_8" );
    addXMLToJavaEncoding( "ISO_8859-8", "ISO8859_8" );
    addXMLToJavaEncoding( "HEBREW", "ISO8859_8" );
    addXMLToJavaEncoding( "ISO-IR-148", "ISO8859_9" );
    addXMLToJavaEncoding( "ISO_8859-9", "ISO8859_9" );
    addXMLToJavaEncoding( "LATIN5", "ISO8859_9" );
    addXMLToJavaEncoding( "L5", "ISO8859_9" );
    addXMLToJavaEncoding( "ISO-2022-JP", "ISO2022JP" );
    addXMLToJavaEncoding( "SHIFT_JIS", "SJIS" );
    addXMLToJavaEncoding( "MS_Kanji", "SJIS" );
    addXMLToJavaEncoding( "ISO-2022-KR", "ISO2022KR" );
    addXMLToJavaEncoding( "EBCDIC-CP-CA", "CP037" );
    addXMLToJavaEncoding( "EBCDIC-CP-NL", "CP037" );
    addXMLToJavaEncoding( "EBCDIC-CP-WT", "CP037" );
    addXMLToJavaEncoding( "EBCDIC-CP-NO", "CP277" );
    addXMLToJavaEncoding( "EBCDIC-CP-SE", "CP278" );
    addXMLToJavaEncoding( "EBCDIC-CP-BE", "CP500" );
    addXMLToJavaEncoding( "EBCDIC-CP-YU", "CP870" );
    addXMLToJavaEncoding( "X0212", "JIS0212" );

    addJavaToXMLEncoding( "ISO8859_1", "ISO-8859-1" );
    addJavaToXMLEncoding( "ISO8859_2", "ISO-8859-2" );
    addJavaToXMLEncoding( "ISO8859_3", "ISO-8859-3" );
    addJavaToXMLEncoding( "ISO8859_4", "ISO-8859-4" );
    addJavaToXMLEncoding( "ISO8859_5", "ISO-8859-5" );
    addJavaToXMLEncoding( "ISO8859_6", "ISO-8859-6" );
    addJavaToXMLEncoding( "ISO8859_7", "ISO-8859-7" );
    addJavaToXMLEncoding( "ISO8859_8", "ISO-8859-8" );
    addJavaToXMLEncoding( "ISO8859_9", "ISO-8859-9" );
    addJavaToXMLEncoding( "ISO2022JP", "ISO-2022-JP" );
    addJavaToXMLEncoding( "SJIS", "Shift_JIS" );
    addJavaToXMLEncoding( "MS932", "WINDOWS-31J" );
    addJavaToXMLEncoding( "EUC_JP", "EUC-JP" );
    addJavaToXMLEncoding( "EUC_KR", "EUC-KR" );
    addJavaToXMLEncoding( "ISO2022KR", "ISO-2022-KR" );
    addJavaToXMLEncoding( "KOI8_R", "KOI8-R" );
    addJavaToXMLEncoding( "JIS0212", "ISO-IR-159" );
    }
  }