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

package org.exoplatform.ws.framework.json.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Stack;

import org.exoplatform.ws.framework.json.JsonWriter;
import org.exoplatform.ws.framework.json.impl.JsonUtils.JsonToken;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonWriterImpl implements JsonWriter {

  /**
   * Stack for control position in document.
   */
  private final Stack<JsonToken> jsonTokens_ = new Stack<JsonToken>();
  
  private final Writer writer_;

  private boolean commaFirst_ = false;

  public JsonWriterImpl(Writer writer) {
    writer_ = writer;
  }

  public JsonWriterImpl(OutputStream out) {
    this(new OutputStreamWriter(out));
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeStartObject()
   */
  public void writeStartObject() throws JsonException {
    if (!jsonTokens_.isEmpty()) {
      // Object can be stated after key with followed ':' or as array item.  
      if (jsonTokens_.peek() != JsonToken.key
          && jsonTokens_.peek() != JsonToken.array)
        throw new JsonException("Syntax error. Unexpected element '{'.");
    }
    try {
      if (commaFirst_) // needed ',' before 
        writer_.write(',');
      writer_.write('{');
      if (!jsonTokens_.isEmpty()
          && jsonTokens_.peek() == JsonToken.key)
     // if at the top of stack is 'key' then remove it.
        jsonTokens_.pop(); 
      jsonTokens_.push(JsonToken.object); // remember new object opened
      commaFirst_ = false;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeEndObject()
   */
  public void writeEndObject() throws JsonException {
    try {
      if (jsonTokens_.pop() != JsonToken.object) // wrong JSON structure.
        throw new JsonException("Sysntax error. Unexpected element '}'.");
      writer_.write('}');
      commaFirst_ = true;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeStartArray()
   */
  public void writeStartArray() throws JsonException {
    if (jsonTokens_.isEmpty()
        || (jsonTokens_.peek() != JsonToken.key
            && jsonTokens_.peek() != JsonToken.array))
      throw new JsonException("Sysntax error. Unexpected element '['..");
    try {
      if (commaFirst_) // needed ',' before 
        writer_.write(',');
      writer_.write('[');
      if (jsonTokens_.peek() == JsonToken.key)
        // if at the top of stack is 'key' then remove it.
        jsonTokens_.pop();
      jsonTokens_.push(JsonToken.array); // remember new array opened
      commaFirst_ = false;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeEndArray()
   */
  public void writeEndArray() throws JsonException {
    try {
      if (jsonTokens_.pop() != JsonToken.array) // wrong JSON structure 
        throw new JsonException("Sysntax error. Unexpected element ']'.");
      writer_.write(']');
      commaFirst_ = true;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeKey(java.lang.String)
   */
  public void writeKey(String key) throws JsonException {
    if (key == null)
      throw new JsonException("Key is null.");
    if (jsonTokens_.isEmpty() || jsonTokens_.peek() != JsonToken.object)
      throw new JsonException("Sysntax error. Unexpected characters '"
          + key + "'." + jsonTokens_);
    try {
      if (commaFirst_)
        writer_.write(',');
      // create JSON representation for given string.
      writer_.write(JsonUtils.getJsonString(key));
      writer_.write(':');
      commaFirst_ = false;
      jsonTokens_.push(JsonToken.key);
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeString(java.lang.String)
   */
  public void writeString(String value) throws JsonException {
    write(JsonUtils.getJsonString(value));
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeValue(long)
   */
  public void writeValue(long value) throws JsonException {
    write(Long.toString(value));
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeValue(double)
   */
  public void writeValue(double value) throws JsonException {
    write(Double.toString(value));
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONEncoder#writeValue(boolean)
   */
  public void writeValue(boolean value) throws JsonException {
    write(Boolean.toString(value));
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONWriter#writeNull()
   */
  public void writeNull() throws JsonException {
    write("null");
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONWriter#flush()
   */
  public void flush() throws JsonException {
    try {
      writer_.flush();
    } catch (IOException e) {
      new JsonException(e);
    }
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.JSONWriter#close()
   */
  public void close() throws JsonException {
    try {
      writer_.close();
    } catch (IOException e) {
      new JsonException(e);
    }
  }

  private void write(String value) throws JsonException {
    try {
      if (jsonTokens_.isEmpty()
          || (jsonTokens_.peek() != JsonToken.key
              && jsonTokens_.peek() != JsonToken.array))
        throw new JsonException("Sysntax error. Unexpected characters '" + value + "'.");
      if (commaFirst_) 
        writer_.write(',');
      writer_.write(value);
      commaFirst_ = true;
      if (jsonTokens_.peek() == JsonToken.key)
        jsonTokens_.pop();
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }
}
