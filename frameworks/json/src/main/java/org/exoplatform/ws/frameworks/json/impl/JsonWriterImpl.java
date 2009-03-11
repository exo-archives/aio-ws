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

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.Stack;

import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonUtils.JsonToken;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JsonWriterImpl implements JsonWriter {

  /**
   * Stack for control position in document.
   */
  private final Stack<JsonToken> jsonTokens = new Stack<JsonToken>();
  
  /**
   * Writer.
   */
  private final Writer writer;

  /**
   * Indicate is current value is the first, if not before value must be written comma.  
   */
  private boolean commaFirst = false;

  /**
   * Constructs JsonWriter.
   * @param writer Writer.
   */
  public JsonWriterImpl(Writer writer) {
    this.writer = writer;
  }

  /**
   * Constructs JsonWriter.
   * @param out OutputStream.
   */
  public JsonWriterImpl(OutputStream out) {
    this(new OutputStreamWriter(out, Charset.forName("UTF-8")));
  }

  /**
   * {@inheritDoc}
   */
  public void writeStartObject() throws JsonException {
    if (!jsonTokens.isEmpty()) {
      // Object can be stated after key with followed ':' or as array item.  
      if (jsonTokens.peek() != JsonToken.key && jsonTokens.peek() != JsonToken.array)
        throw new JsonException("Syntax error. Unexpected element '{'.");
    }
    try {
      if (commaFirst) // needed ',' before 
        writer.write(',');
      writer.write('{');
     // if at the top of stack is 'key' then remove it.
      if (!jsonTokens.isEmpty() && jsonTokens.peek() == JsonToken.key)
        jsonTokens.pop(); 
      jsonTokens.push(JsonToken.object); // remember new object opened
      commaFirst = false;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void writeEndObject() throws JsonException {
    try {
      if (jsonTokens.pop() != JsonToken.object) // wrong JSON structure.
        throw new JsonException("Sysntax error. Unexpected element '}'.");
      writer.write('}');
      commaFirst = true;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void writeStartArray() throws JsonException {
    if (jsonTokens.isEmpty()
        || (jsonTokens.peek() != JsonToken.key && jsonTokens.peek() != JsonToken.array))
      throw new JsonException("Sysntax error. Unexpected element '['..");
    try {
      if (commaFirst) // needed ',' before 
        writer.write(',');
      writer.write('[');
      if (jsonTokens.peek() == JsonToken.key)
        // if at the top of stack is 'key' then remove it.
        jsonTokens.pop();
      jsonTokens.push(JsonToken.array); // remember new array opened
      commaFirst = false;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void writeEndArray() throws JsonException {
    try {
      if (jsonTokens.pop() != JsonToken.array) // wrong JSON structure 
        throw new JsonException("Sysntax error. Unexpected element ']'.");
      writer.write(']');
      commaFirst = true;
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void writeKey(String key) throws JsonException {
    if (key == null)
      throw new JsonException("Key is null.");
    if (jsonTokens.isEmpty() || jsonTokens.peek() != JsonToken.object)
      throw new JsonException("Sysntax error. Unexpected characters '"
          + key + "'." + jsonTokens);
    try {
      if (commaFirst)
        writer.write(',');
      // create JSON representation for given string.
      writer.write(JsonUtils.getJsonString(key));
      writer.write(':');
      commaFirst = false;
      jsonTokens.push(JsonToken.key);
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void writeString(String value) throws JsonException {
    write(JsonUtils.getJsonString(value));
  }

  /**
   * {@inheritDoc}
   */
  public void writeValue(long value) throws JsonException {
    write(Long.toString(value));
  }

  /**
   * {@inheritDoc}
   */
  public void writeValue(double value) throws JsonException {
    write(Double.toString(value));
  }

  /**
   * {@inheritDoc}
   */
  public void writeValue(boolean value) throws JsonException {
    write(Boolean.toString(value));
  }

  /**
   * {@inheritDoc}
   */
  public void writeNull() throws JsonException {
    write("null");
  }
  
  /**
   * {@inheritDoc}
   */
  public void flush() throws JsonException {
    try {
      writer.flush();
    } catch (IOException e) {
      new JsonException(e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void close() throws JsonException {
    try {
      writer.close();
    } catch (IOException e) {
      new JsonException(e);
    }
  }

  /**
   * Write single String.
   * @param value String.
   * @throws JsonException if any errors occurs.
   */
  private void write(String value) throws JsonException {
    try {
      if (jsonTokens.isEmpty()
          || (jsonTokens.peek() != JsonToken.key
              && jsonTokens.peek() != JsonToken.array))
        throw new JsonException("Sysntax error. Unexpected characters '" + value + "'.");
      if (commaFirst) 
        writer.write(',');
      writer.write(value);
      commaFirst = true;
      if (jsonTokens.peek() == JsonToken.key)
        jsonTokens.pop();
    } catch (IOException e) {
      throw new JsonException(e);
    }
  }
}
