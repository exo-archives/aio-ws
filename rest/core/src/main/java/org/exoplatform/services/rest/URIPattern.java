package org.exoplatform.services.rest;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility class for manipulating "parametrized" strings. 
 * A parametrized is a string constructed by substituting placeholders
 * in the pattern string where pattern string is any string containing 
 * placeholders in the form of "{placeholderName}". 
 * @author Alexander Tereshkin
 */
public class URIPattern {

  /**
   * Pattern for URI. URI must have form /test/{param1}/.
   * At the place param1 can be any value. 
   */
  private static Pattern paramPattern = Pattern.compile("\\{[^\\}^\\{]*\\}");
  
  /**
   * Array of tokens. 
   */
  private String[] tokens;
  
  /**
   * Array of parameter names.
   */
  private String[] paramNames;
  
  /**
   * Pattern.
   */
  private String pattern;
  
  /**
   * Result set of URI parameters.
   */
  private Set<String> params;
  
  /**
   * Total length of all tokens. Used for searching priority
   * Resources for some URI. The higher total length of tokens 
   * the more precision URIPattern for one URI.
   */
  private int totalTokensLength = 0;

  /**
   * Creates a new instance of ParametrizedStringParser.
   * @param patternString pattern.
   */
  public URIPattern(String patternString) {
    tokens = paramPattern.split(patternString, -1);
    for (String t : tokens) {
      if (t.length() == 0) {
        throw new IllegalArgumentException("Invalid pattern:"
            + patternString);
      }
      totalTokensLength += t.length(); 
    }
    
    int numParams = tokens.length - 1;
    paramNames = new String[numParams];
    Matcher matcher = paramPattern.matcher(patternString);
    for (int i = 0; matcher.find(); i++) {
      paramNames[i] = patternString.substring(matcher.start() + 1, matcher.end() - 1);
    }
    
    if (numParams > 0) {
      assert paramNames[numParams - 1] != null;
    }
    pattern = patternString;
    params = Collections.unmodifiableSet(new HashSet<String>(Arrays
        .asList(paramNames)));
  }

  /**
   * Returns a <code>Set</code> of names of parameters (placeholders)
   * participating in the underlying pattern string.
   * @return <code>Set</code> of string parameters.
   */
  public Set<String> getParamNames() {
    return params;
  }

  /**
   * Parses the given string against the underlying pattern and returns a
   * <code>Map</code> of parameter names to values.
   * @param string string to parse.
   * @return <code>Map</code> of parameter names to values.
   */
  public Map<String, String> parse(String string) {
    if (string == null) {
      throw new NullPointerException();
    }
    Map<String, String> ret = new HashMap<String, String>();
    if (paramNames.length == 0) {
      if (string.equals(pattern)) {
        return ret;
      }
      throw new IllegalArgumentException("Pattern not matched: "
          + pattern
          + ", "
          + string);
    }
    if (!matches(string)) {
      throw new IllegalArgumentException("Pattern not matched: "
          + pattern
          + ", "
          + string);
    }
    int pos = tokens[0].length();
    for (int i = 0; i < paramNames.length; i++) {
      int nextPos = tokens[i + 1].length() > 0 ? string.indexOf(tokens[i + 1],
          pos) : string.length();
      if (nextPos < 0) {
        throw new IllegalArgumentException("Pattern not matched: "
            + pattern
            + ", "
            + string);
      }
      if (i == paramNames.length - 1 && tokens[i + 1].equals("/")) {
        ret.put(paramNames[i], string.substring(pos, string.length() - 1));
      } else {
        ret.put(paramNames[i], string.substring(pos, nextPos));
      }
      pos = nextPos + tokens[i + 1].length();
    }
    return ret;
  }

  /**
   * Checks if the given string matches the underlying pattern.
   * @param string string to test.
   * @return true if the string matches the pattern false otherwise.
   */
  public boolean matches(String string) {
    if (paramNames.length == 0 && !pattern.equals(string)) {
      return false;
    }
    if (string.indexOf(tokens[0], 0) != 0) {
      return false;
    }
    int pos = tokens[0].length();
    for (int i = 0; i < paramNames.length; i++) {
      int nextPos = (tokens[i + 1].length() > 0) ? string.indexOf(
          tokens[i + 1], pos) : string.length();
      if (nextPos < 0) {
        return false;
      }
      pos = nextPos + tokens[i + 1].length();
    }
    if (string.lastIndexOf(tokens[tokens.length - 1], string.length()) < 0) {
      return false;
    }
    return true;
  }

  /**
   * check is two URIPattern matches.
   * @param another the another URIPattern.
   * @return the result of comparison.
   */
  public boolean matches(URIPattern another) {
//    Pattern p = Pattern.compile("\\{.*\\}");
//    return matches(p.matcher(another.getString()).replaceAll("x"));
    return matches(another.getString());
  }

  /**
   * Returns the underlying pattern string.
   * @return the underlying pattern string.
   */
  public String getString() {
    return pattern;
  }
  
  /**
   * Get total tokens length.
   * @return the total tokens length. 
   */
  public int getTotalTokensLength() {
    return totalTokensLength;
  }

}
