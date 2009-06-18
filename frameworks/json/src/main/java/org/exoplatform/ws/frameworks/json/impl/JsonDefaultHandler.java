/**
 * Copyright (C) 2003-2008 eXo Platform SAS.
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

import java.util.Stack;

import org.exoplatform.ws.frameworks.json.JsonHandler;
import org.exoplatform.ws.frameworks.json.value.JsonValue;
import org.exoplatform.ws.frameworks.json.value.impl.ArrayValue;
import org.exoplatform.ws.frameworks.json.value.impl.BooleanValue;
import org.exoplatform.ws.frameworks.json.value.impl.DoubleValue;
import org.exoplatform.ws.frameworks.json.value.impl.LongValue;
import org.exoplatform.ws.frameworks.json.value.impl.NullValue;
import org.exoplatform.ws.frameworks.json.value.impl.ObjectValue;
import org.exoplatform.ws.frameworks.json.value.impl.StringValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class JsonDefaultHandler implements JsonHandler {

  /**
   * The key.
   */
  private String key;

  /**
   * JsonValue which is currently in process.
   */
  private JsonValue current;
  
  /**
   * Stack of JsonValues.
   */
  private Stack<JsonValue> values;

  /**
   * Constructs new JsonHandler.
   */
  public JsonDefaultHandler() {
    this.values = new Stack<JsonValue>();
  }

  /**
   * {@inheritDoc}
   */
  public void characters(char[] characters) {
    if (current.isObject())
      current.addElement(key, parseCharacters(characters));
    else if (current.isArray())
      current.addElement(parseCharacters(characters));
  }

  /**
   * {@inheritDoc}
   */
  public void endArray() {
    current = values.pop();
  }

  /**
   * {@inheritDoc}
   */
  public void endObject() {
    current = values.pop();
  }

  /**
   * {@inheritDoc}
   */
  public void key(String key) {
    this.key = key;
  }

  /**
   * {@inheritDoc}
   */
  public void startArray() {
    ArrayValue o = new ArrayValue();
    if (current.isObject())
      current.addElement(key, o);
    else if (current.isArray())
      current.addElement(o);
    values.push(current);
    current = o;
  }

  /**
   * {@inheritDoc}
   */
  public void startObject() {
    if (current == null) {
      current = new ObjectValue();
      values.push(current);
      return;
    }
    ObjectValue o = new ObjectValue();
    if (current.isObject())
      current.addElement(key, o);
    else if (current.isArray())
      current.addElement(o);
    values.push(current);
    current = o;
  }

  /**
   * {@inheritDoc}
   */
  public JsonValue getJsonObject() {
    return current;
  }

  /**
   * Parse characters array dependent of context.
   * @param characters the characters array.
   * @return JsonValue.
   */
  private JsonValue parseCharacters(char[] characters) {
    String s = new String(characters);
    
    if (characters[0] == '"' && characters[characters.length - 1] == '"') {
      return new StringValue(s.substring(1, s.length() - 1));
    } else if ("true".equalsIgnoreCase(new String(characters))
        || "false".equalsIgnoreCase(s)) {
      return new BooleanValue(Boolean.parseBoolean(new String(characters)));
    } else if ("null".equalsIgnoreCase(new String(characters))) {
      return new NullValue();
    } else {
      char c = characters[0];
      if ((c >= '0' && c <= '9') || c == '.' || c == '-' || c == '+') {
        // first try read as hex is start from '0'
        if (c == '0') {
          if (s.length() > 2 && (s.charAt(1) == 'x' || s.charAt(1) == 'X')) {
            try {
              return new LongValue(Long.parseLong(s.substring(2), 16));
            } catch (NumberFormatException e) {
              // nothing to do!
            }
          } else {
            // as oct long
            try {
              return new LongValue(Long.parseLong(s.substring(1), 8));
            } catch (NumberFormatException e) {
              // if fail, then it is not oct
              try {
                //try as dec long
                return new LongValue(Long.parseLong(s));
              } catch (NumberFormatException l) {
                try {
                  // and last try as double
                  return new DoubleValue(Double.parseDouble(s));
                } catch (NumberFormatException d) {
                  // nothing to do!
                }
              }
              // nothing to do!
            }
          }
        } else {
          // if char set start not from '0'
          try {
            // try as long
            return new LongValue(Long.parseLong(s));
          } catch (NumberFormatException l) {
            try {
              // try as double if above failed
              return new DoubleValue(Double.parseDouble(s));
            } catch (NumberFormatException d) {
              // nothing to do!
            }
          }
        }
      }
    }
    // if can't parse return as string
    /////////////////////////////////////////////
    //TODO may be better generate exception here
    /////////////////////////////////////////////
    return new StringValue(s);
  }

}
