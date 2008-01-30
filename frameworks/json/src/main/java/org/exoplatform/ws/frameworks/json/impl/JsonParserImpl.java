/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.ws.frameworks.json.impl;

import java.io.CharArrayWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PushbackReader;
import java.io.Reader;
import java.util.Stack;

import org.exoplatform.ws.frameworks.json.impl.JsonDefaultHandler;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.JsonParser;
import org.exoplatform.ws.frameworks.json.impl.JsonUtils.JsonToken;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonParserImpl implements JsonParser {
  
  private JsonHandler jsonHandler_;
  
  private PushbackReader reader_;
  
  private Stack<JsonToken> jsonTokens_ = new Stack<JsonToken>();
  
  /**
   * Default constructor.
   */
  public JsonParserImpl() {
    jsonHandler_ = new JsonDefaultHandler();
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonParser#setHandler(
   * org.exoplatform.services.rest.frameworks.json.JsonHandler)
   */
  public void setHandler(JsonHandler handler) throws JsonException {
    if (handler == null)
      throw new JsonException("JsonHandler is null!");
    jsonHandler_ = handler;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonParser#getJsonHandler()
   */
  public JsonHandler getJsonHandler() {
    return jsonHandler_;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonParser#parse(java.io.Reader)
   */
  public void parse(Reader reader) throws JsonException {
    this.reader_ = new PushbackReader(reader);
    try {
      char c = 0;
      while ((c = next()) != 0) {
        if (c == '{')
          readObject();
        else
          throw new JsonException("Syntax error. Unexpected '" + c + "'. Must be '{'.");
      }
      if (!jsonTokens_.isEmpty())
        throw new JsonException("Syntax error. Missing one or more close bracket(s).");
    } catch (Exception e) {
      throw new JsonException(e);
    }
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonParser#parse(java.io.InputStream)
   */
  public void parse(InputStream sream) throws JsonException {
    parse(new InputStreamReader(sream));
  }
  

  /**
   * Read JSON object token, it minds all characters
   * from '{' to '}'.
   * @throws JsonException
   */
  private void readObject() throws JsonException {
    char c = 0;
    // inform handler about start of object
    jsonHandler_.startObject();
    jsonTokens_.push(JsonToken.object);
    for (;;) {
      switch (c = next()) {
      case 0:
        throw new JsonException("Syntax error. Unexpected end of object." +
        		"Object must end by '}'.");
      case '{':
        readObject();
        break;
      case '}':
        // inform handler about end of object
        jsonHandler_.endObject();
        // check  is allowed end of object now
        if (JsonToken.object != jsonTokens_.pop())
          throw new JsonException("Syntax error. Unexpected end of object.");
          
        // check is allowed char after end of json object 
        switch(c = next()) {
          // end of stream
          case 0:
            break;
          case ',':
          case ']':
          case '}':
            back(c);
            break;
          default:
            // must not happen
            throw new JsonException("Syntax error. Excpected "
                + "for ',' or ']' or '}' but found '" + c + "'.");
        }
        return;  // end for(;;)
      case '[':
        readArray();
        break;
      case ',': // nothing to do just must not be default
        break;
      default:
        back(c);
        // all characters from start object to ':' - key.
        readKey();
        next(':');
        c = next();
        back(c);
        // object/array/value
        if (c != '{' && c != '[') 
          readValue();
        break;
      }
    }
  }
  
  /**
   * Read JSON array token, it minds all characters
   * from '[' to ']'.
   * @throws JsonException
   */
  private void readArray() throws JsonException {
    char c = 0;
    // inform handler about start of array
    jsonHandler_.startArray();
    jsonTokens_.push(JsonToken.array);
    for (;;) {
      switch (c = next()) {
      case 0:
        throw new JsonException("Syntax error. Unexpected end of array." +
            "Array must end by ']'.");
      case ']':
        // inform handler about end of array
        jsonHandler_.endArray();
        // check  is allowed end of array now
        if (JsonToken.array != jsonTokens_.pop())
          throw new JsonException("Syntax error. Unexpected end of array.");
        // check is allowed char after end of json array 

        c = next(",]}");
        back(c);
        return;   // end for(;;)
      case '[':
        readArray();
        break;
      case '{':
        readObject();
        break;
      case ',':   // nothing to do just must not be default
        break;
      default:
        back(c);
        readValue();
        break;
      }
    }
  }
  
  /**
   * Read key from stream.
   * @throws JsonException
   */
  private void readKey() throws JsonException {
    char c = next();
    if (c != '"')
      throw new JsonException("Syntax error. Key must start from quote, but found '" + c + "'.");
    back(c);
    String s = new String(nextString());
    // if key as ""
    if (s.length() == 2)
      throw new JsonException("Missing key.");
    jsonHandler_.key(s.substring(1, s.length() -1 ));
  }

  /**
   * Read value from stream.
   * @throws JsonException
   */
  private void readValue() throws JsonException {
    char c = next();
    back(c);
    if (c == '"') {
      // value will be read as string
      jsonHandler_.characters(nextString());
    } else {
      // not string (numeric or boolean or null)
      CharArrayWriter cw = new CharArrayWriter();
      while("{[,]}\"".indexOf(c = next()) < 0)
        cw.append(c);
      back(c);
      jsonHandler_.characters(cw.toCharArray());
    }
    c = next(",]}");
    back(c);
  }
  
  /**
   * Get next char from stream, skipping whitespace and comments.
   * Comments:
   * One line comment from // to end of line;
   * Multi-line comments from / and * to * and /
   * @return the next char.
   * @throws JsonException
   */
  private char next() throws JsonException {
    try {
      int c = 0;
      while ((c = reader_.read()) != -1) {
        if (c == '/') {
          c = reader_.read();
          if (c == '/') {
            do {
              c = reader_.read();
            } while(c != -1 && c != '\n' && c != '\r');
          } else if (c == '*') {
            for (;;) {
              c = reader_.read();
              if (c == '*') {
                c = reader_.read();
                if (c == '/')
                  break;
              }
              if (c == -1) {
                throw new JsonException("Syntax error. Missing end of comment.");
              }
            }
            
          } else {
            back((char) c);
            return '/';
          }
          
        } else if (c == -1 || c > ' ')
          break;
      }
      return (c == -1) ? 0 : (char) c;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }
  
  /**
   * Get next char from stream.
   * @return the next char.
   * @throws JsonException
   */
  private char nextAny() throws JsonException {
    try {
      int c = reader_.read();
      return (c == -1) ? 0 : (char) c;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }
  
  /**
   * Get next char from stream. And check is this char equals expected.
   * @param c the expected char.
   * @return the next char.
   * @throws JsonException
   */
  private char next(char c) throws JsonException {
    char n = next();
    if (n != c)
      throw new JsonException("Expected for '" + c
          + "' but found '" + n + "'.");
    return n;
  }
  
  /**
   * Get next char from stream. And check is this char presents in given string. 
   * @param s the string.
   * @return the next char.
   * @throws JsonException
   */
  private char next(String s) throws JsonException {
    char n = next();
    // if char present in string
    if (s.indexOf(n) >= 0)
      return n;
    // else error
    char[] ch = s.toCharArray();
    StringBuffer sb = new StringBuffer();
    int i = 0;
    for (char c : ch) {
      if (i > 0)
        sb.append(" or ");
      i++;
      sb.append('\'').append(c).append('\'');
    }
    throw new JsonException("Expected for " + sb.toString()
        + " but found '" + n + "'.");
  }
  
  /**
   * Get next n characters from stream.
   * @param n the number of characters.
   * @return the array of characters.
   * @throws JsonException
   */
  private char[] next(int n) throws JsonException {
    char[] buff = new char[n];
    try {
      int i = reader_.read(buff);
      if (i == -1)
        throw new JsonException("Unexpected end of stream.");
      return buff;
    } catch (Exception e) {
      throw new JsonException(e);
    }
  }

  /**
   * Get array chars up to given and include it.
   * @param n the delimiter char.
   * @return the char array.
   * @throws JsonException
   */
  private char[] nextString() throws JsonException {
    CharArrayWriter cw = new CharArrayWriter();
    char c = nextAny(); // read '"'
    cw.append(c);
    for (;;) {
      switch (c = nextAny()) {
        case 0:
        case '\n':
        case '\r':
          throw new JsonException("Syntax error. Unterminated string.");
        case '\\':
          switch (c = nextAny()) {
            case 0:
            case '\n':
            case '\r':
              throw new JsonException("Syntax error. Unterminated string");
            case 'n':
              cw.append('\n');
              break;
            case 'r':
              cw.append('\r');
              break;
            case 'b':
              cw.append('\b');
              break;
            case 't':
              cw.append('\t');
              break;
            case 'f':
              cw.append('\f');
              break;
            case 'u':    // unicode
              String s = new String(next(4));
              cw.append((char) Integer.parseInt(s, 16));
              break;
            default:
              cw.append(c);
              break;
          }
          break;
        default:
          cw.append(c);
          if (c == '"')
            return cw.toCharArray();
          break;
      }
    }
  }
  
  /**
   * Push back given char to stream.
   * @param c the char for pushing back.
   * @throws JsonException
   */
  private void back(char c) throws JsonException {
    try {
      reader_.unread(c);
    } catch (Exception e) {
      throw new JsonException(e);
    }
  }
  
}
