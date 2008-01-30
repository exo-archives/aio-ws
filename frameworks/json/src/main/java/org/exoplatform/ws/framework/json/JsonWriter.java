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

package org.exoplatform.ws.framework.json;

import org.exoplatform.ws.framework.json.impl.JsonException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface JsonWriter {
  
  /**
   * Write the start of JSON object '{'.  
   * @throws JsonException
   */
  void writeStartObject() throws JsonException ;
  
  /**
   * Write the end of JSON object '}'.
   * @throws JsonException
   */
  void writeEndObject() throws JsonException ;
  
  /**
   * Write the start of JSON array '['.  
   * @throws JsonException
   */
  void writeStartArray() throws JsonException ;
  
  /**
   * Write the key. After key will go the value.
   * In this way data represented in JSON object.
   * @param key
   * @throws JsonException
   */
  void writeKey(String key) throws JsonException ;
  
  /**
   * Write the end of JSON array ']'.
   * @throws JsonException
   */
  void writeEndArray() throws JsonException ;
  
  /**
   * Write the String to stream.
   * @param value the String.
   * @throws JsonException
   */
  void writeString(String value) throws JsonException ;
  
  /**
   * Write the value of long type to stream.
   * @param value the value of long type.
   * @throws JsonException
   */
  void writeValue(long value) throws JsonException ;
  
  /**
   * Write the value of double type to stream.
   * @param value the value of double type.
   * @throws JsonException
   */
  void writeValue(double value) throws JsonException ;
  
  /**
   * Write the value of boolean type to stream.
   * @param value the value of boolean type.
   * @throws JsonException
   */
  void writeValue(boolean value) throws JsonException ;
  
  /**
   * Write the null data to stream.
   * @throws JsonException
   */
  void writeNull() throws JsonException ;
  
  /**
   * Flush output writer.
   * @throws JsonException
   */
  void flush() throws JsonException;
  
  /**
   * Close output writer.
   * @throws JsonException
   */
  void close() throws JsonException;
}

