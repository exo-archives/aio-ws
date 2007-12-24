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
    if (!stack_.isEmpty())
    if (/*stack_.isEmpty() || */(stack_.peek() != 'k' && stack_.peek() != 'a'))
      throw new JSONException("Wrong place start object.");
    try {
      if (commaFirst_)
        writer_.write(",{");
      else
        writer_.write("{");
      if (!stack_.isEmpty() && stack_.peek() == 'k')
        stack_.pop();
      stack_.push('o');
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
      if (stack_.pop() != 'o')
        throw new JSONException("Wrong place end object.");
      writer_.write("}");
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
    if (stack_.isEmpty() || (stack_.peek() != 'k' && stack_.peek() != 'a'))
      throw new JSONException("Wrong place start array.");
    try {
      if (commaFirst_)
        writer_.write(",[");
      else
        writer_.write("[");
      if (stack_.peek() == 'k')
        stack_.pop();
      stack_.push('a');
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
      if (stack_.pop() != 'a')
        throw new JSONException("Wrong place end array.");
      writer_.write("]");
      commaFirst_ = true;
      if (stack_.peek() == 'k')
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
    if (stack_.isEmpty() || stack_.peek() != 'o')
      throw new JSONException("Wrong place of key '" + key + "'.");
    try {
      if (commaFirst_)
        writer_.write(",");
      writer_.write(JSONUtils.getJSONString(key) + ":");
      commaFirst_ = false;
      stack_.push('k');
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
      if (stack_.isEmpty() || (stack_.peek() != 'k' && stack_.peek() != 'a'))
        throw new JSONException("Wrong place of value '" + value + "'.");
      if (commaFirst_)
        writer_.write("," + value);
      else
        writer_.write(value);
      commaFirst_ = true;
      if (stack_.peek() == 'k')
        stack_.pop();
    } catch (IOException e) {
      throw new JSONException(e);
    }
  }
}
