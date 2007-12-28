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

package org.exoplatform.services.rest.frameworks.json.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
//import java.util.Map;
//import java.util.Set;
import java.util.Stack;

import org.exoplatform.services.rest.frameworks.json.JSONWriter;
import org.exoplatform.services.rest.frameworks.json.utils.JSONUtils;

import static org.exoplatform.services.rest.frameworks.json.JSONConstants.KEY;
import static org.exoplatform.services.rest.frameworks.json.JSONConstants.OBJECT;
import static org.exoplatform.services.rest.frameworks.json.JSONConstants.ARRAY;
import static org.exoplatform.services.rest.frameworks.json.JSONConstants.START_OBJECT;
import static org.exoplatform.services.rest.frameworks.json.JSONConstants.END_OBJECT;
import static org.exoplatform.services.rest.frameworks.json.JSONConstants.START_ARRAY;
import static org.exoplatform.services.rest.frameworks.json.JSONConstants.END_ARRAY;
import static org.exoplatform.services.rest.frameworks.json.JSONConstants.COMMA;
import static org.exoplatform.services.rest.frameworks.json.JSONConstants.COLON;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JSONWriterImpl implements JSONWriter {

  /**
   * Stack for control position in document.
   */
  private final Stack<Character> stack_ = new Stack<Character>();
  
  private final Writer writer_;

  private boolean commaFirst_ = false;

  public JSONWriterImpl(Writer writer) {
    writer_ = writer;
  }

  public JSONWriterImpl(OutputStream out) {
    this(new OutputStreamWriter(out));
  }

//  /*
//   * (non-Javadoc)
//   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeStart()
//   */
//  public void writeStart() throws JSONException {
//    if (!stack_.isEmpty())
//      throw new JSONException("Wrong start document!");
//    try {
//      writer_.write("{");
//      stack_.push('o');
//    } catch (IOException e) {
//      throw new JSONException(e);
//    }
//  }
//
//  /*
//   * (non-Javadoc)
//   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeEnd()
//   */
//  public void writeEnd() throws JSONException {
//    if (stack_.size() != 1 || stack_.pop() != 'o')
//      throw new JSONException("Wrong end document!");
//    try {
//      writer_.write("}");
//      writer_.flush();
//      writer_.close();
//    } catch (IOException e) {
//      throw new JSONException(e);
//    }
//  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeStartObject()
   */
  public void writeStartObject() throws JSONException {
    if (!stack_.isEmpty()) {
      if (/*stack_.isEmpty() || */(stack_.peek() != KEY && stack_.peek() != ARRAY))
        throw new JSONException("Wrong place start object.");
    }
    try {
      if (commaFirst_) {
        writer_.write(COMMA);
        writer_.write(START_OBJECT);
      } else
        writer_.write(START_OBJECT);
      if (!stack_.isEmpty() && stack_.peek() == KEY)
        stack_.pop();
      stack_.push(OBJECT);
      commaFirst_ = false;
    } catch (IOException e) {
      throw new JSONException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeEndObject()
   */
  public void writeEndObject() throws JSONException {
    try {
      if (stack_.pop() != OBJECT)
        throw new JSONException("Wrong place end object.");
      writer_.write(END_OBJECT);
      commaFirst_ = true;
    } catch (IOException e) {
      throw new JSONException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeStartArray()
   */
  public void writeStartArray() throws JSONException {
    if (stack_.isEmpty() || (stack_.peek() != KEY && stack_.peek() != ARRAY))
      throw new JSONException("Wrong place start array.");
    try {
      if (commaFirst_) {
        writer_.write(COMMA);
        writer_.write(START_ARRAY);
      }
      else
        writer_.write(START_ARRAY);
      if (stack_.peek() == KEY)
        stack_.pop();
      stack_.push(ARRAY);
      commaFirst_ = false;
    } catch (IOException e) {
      throw new JSONException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeEndArray()
   */
  public void writeEndArray() throws JSONException {
    try {
      if (stack_.pop() != ARRAY)
        throw new JSONException("Wrong place end array.");
      writer_.write(END_ARRAY);
      commaFirst_ = true;
      if (stack_.peek() == KEY)
        stack_.pop();
    } catch (IOException e) {
      throw new JSONException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeKey(java.lang.String)
   */
  public void writeKey(String key) throws JSONException {
    if (key == null)
      throw new JSONException("Key is null.");
    if (stack_.isEmpty() || stack_.peek() != OBJECT)
      throw new JSONException("Wrong place of key '" + key + "'.");
    try {
      if (commaFirst_)
        writer_.write(COMMA);
      writer_.write(JSONUtils.getJSONString(key));
      writer_.write(COLON);
      commaFirst_ = false;
      stack_.push(KEY);
    } catch (IOException e) {
      throw new JSONException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeString(java.lang.String)
   */
  public void writeString(String value) throws JSONException {
    write(JSONUtils.getJSONString(value));
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeValue(long)
   */
  public void writeValue(long value) throws JSONException {
    write(Long.toString(value));
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeValue(double)
   */
  public void writeValue(double value) throws JSONException {
    write(Double.toString(value));
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeValue(boolean)
   */
  public void writeValue(boolean value) throws JSONException {
    write(Boolean.toString(value));
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONWriter#writeNull()
   */
  public void writeNull() throws JSONException {
    write("null");
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONWriter#flush()
   */
  public void flush() throws JSONException {
    try {
      writer_.flush();
    } catch (IOException e) {
      new JSONException(e);
    }
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONWriter#close()
   */
  public void close() throws JSONException {
    try {
      writer_.close();
    } catch (IOException e) {
      new JSONException(e);
    }
  }

  private void write(String value) throws JSONException {
    try {
      if (stack_.isEmpty() || (stack_.peek() != KEY && stack_.peek() != ARRAY))
        throw new JSONException("Wrong place of value '" + value + "'.");
      if (commaFirst_) {
        writer_.write(COMMA);
        writer_.write(value);
      } else
        writer_.write(value);
      commaFirst_ = true;
      if (stack_.peek() == KEY)
        stack_.pop();
    } catch (IOException e) {
      throw new JSONException(e);
    }
  }
}
