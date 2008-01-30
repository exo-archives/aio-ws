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

                                                                              // org.exoplatform.services.rest.frameworks.json.value.impl.FloatValue;
// import org.exoplatform.services.rest.frameworks.json.value.impl.IntValue;
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
 * @version $Id: $
 */
public class JsonDefaultHandler implements JsonHandler {

  private String key_;

  private JsonValue current_;
  
  private Stack<JsonValue> values_;

  public JsonDefaultHandler() {
    values_ = new Stack<JsonValue>();
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonHandler#characters(char[])
   */
  public void characters(char[] characters) {
    if (current_.isObject())
      current_.addElement(key_, parseCharacters(characters));
    else if (current_.isArray())
      current_.addElement(parseCharacters(characters));
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonHandler#endArray()
   */
  public void endArray() {
    current_ = values_.pop();
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonHandler#endObject()
   */
  public void endObject() {
    current_ = values_.pop();
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonHandler#key(java.lang.String)
   */
  public void key(String key) {
    key_ = key;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonHandler#startArray()
   */
  public void startArray() {
    ArrayValue o = new ArrayValue();
    if (current_.isObject())
      current_.addElement(key_, o);
    else if (current_.isArray())
      current_.addElement(o);
    values_.push(current_);
    current_ = o;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonHandler#startObject()
   */
  public void startObject() {
    if (current_ == null) {
      current_ = new ObjectValue();
      values_.push(current_);
      return;
    }
    ObjectValue o = new ObjectValue();
    if (current_.isObject())
      current_.addElement(key_, o);
    else if (current_.isArray())
      current_.addElement(o);
    values_.push(current_);
    current_ = o;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JsonHandler#getJsonObject()
   */
  public JsonValue getJsonObject() {
    return current_;
  }

  /**
   * Parse characters array dependent of context.
   * @param characters the characters array.
   * @return
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
