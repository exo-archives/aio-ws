package org.exoplatform.services.rest;

/**
 * An utility class for manipulating "parametrized" strings. 
 * A parametrized is a string constructed by substituting placeholders
 * in the pattern string where pattern string is any string containing 
 * placeholders in the form of "{placeholderName}". 
 * @author Alexander Tereshkin
 */

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class URIPattern {

  private static Pattern paramPattern_ = Pattern.compile("\\{[^\\}^\\{]*\\}");
  private String[] tokens_;
  private String[] paramNames_;
  private String pattern_;
  private Set<String> params_;

  /**
   * Creates a new instance of ParametrizedStringParser.
   * @param patternString pattern.
   */
  public URIPattern(String patternString) {
    tokens_ = paramPattern_.split(patternString, -1);
    for (int i = 0; i < tokens_.length; i++) {
      if (tokens_[i].length() == 0) {
        throw new IllegalArgumentException("Invalid pattern:" + patternString);
      }
    }
    int numParams = tokens_.length - 1;
    paramNames_ = new String[numParams];
    Matcher matcher = paramPattern_.matcher(patternString);
    for (int i = 0; matcher.find(); i++) {
      paramNames_[i] = patternString.substring(matcher.start() + 1, matcher
          .end() - 1);
    }
    if (numParams > 0) {
      assert paramNames_[numParams - 1] != null;
    }
    this.pattern_ = patternString;
    params_ = Collections.unmodifiableSet(new HashSet<String>(Arrays
        .asList(paramNames_)));
  }

  /**
   * Returns a <code>Set</code> of names of parameters (placeholders)
   * participating in the underlying pattern string.
   * @return <code>Set</code> of string parameters
   */
  public Set<String> getParamNames() {
    return params_;
  }

  /**
   * Parses the given string against the underlying pattern and returns a
   * <code>Map</code> of parameter names to values.
   * @param string string to parse
   * @return <code>Map</code> of parameter names to values
   * @throws IllegalArgumentException if the string doesn't match the pattern
   */
  public Map<String, String> parse(String string) {
    if (string == null) {
      throw new NullPointerException();
    }
    Map<String, String> ret = new HashMap<String, String>();
    if (paramNames_.length == 0) {
      if (string.equals(pattern_)) {
        return ret;
      }
      throw new IllegalArgumentException("Pattern not matched: " + pattern_ +
          ", " + string);
    }
    if (!matches(string)) {
      throw new IllegalArgumentException("Pattern not matched: " + pattern_ +
          ", " + string);
    }
    int pos = tokens_[0].length();
    for (int i = 0; i < paramNames_.length; i++) {
      int nextPos = tokens_[i + 1].length() > 0 ? string.indexOf(tokens_[i + 1],
          pos) : string.length();
      if (nextPos < 0) {
        throw new IllegalArgumentException("Pattern not matched: " + pattern_ +
            ", " + string);
      }
      if (i == paramNames_.length - 1 && tokens_[i + 1].equals("/")) {
        ret.put(paramNames_[i], string.substring(pos, string.length() - 1));
      } else {
        ret.put(paramNames_[i], string.substring(pos, nextPos));
      }
      pos = nextPos + tokens_[i + 1].length();
    }
    return ret;
  }

  /**
   * Checks if the given string matches the underlying pattern.
   * @param string string to test
   * @return true if the string matches the pattern false otherwise
   */
  public boolean matches(String string) {
    if (string.indexOf(tokens_[0], 0) != 0) {
      return false;
    }
    int pos = tokens_[0].length();
    for (int i = 0; i < paramNames_.length; i++) {
      int nextPos = (tokens_[i + 1].length() > 0) ? string.indexOf(
          tokens_[i + 1], pos) : string.length();
      if (nextPos < 0) {
        return false;
      }
      pos = nextPos + tokens_[i + 1].length();
    }
    if (string.lastIndexOf(tokens_[tokens_.length - 1], string.length()) < 0) {
      return false;
    }
    return true;
  }

  /**
   * check is two URIPattern matches.
   * @param another the another URIPattern
   * @return the result of comparison
   */
  public boolean matches(URIPattern another) {
    Pattern p = Pattern.compile("\\{.*\\}");
    return matches(p.matcher(another.getString()).replaceAll("x"));

// int minSize = (tokens.length <= another.getTokens().length) ? tokens.length :
// another.getTokens().length;
// for (int i = 0; i < minSize; i++) {
// if (!tokens[i].equals(another.getTokens()[i])) {
// return false;
// }
// }
// return true;
  }

  /**
   * Returns the underlying pattern string.
   * @return the underlying pattern string.
   */
  public String getString() {
    return pattern_;
  }
  
  public String[] getTokens() {
    return tokens_;
  }

}
