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

package org.exoplatform.services.rest.frameworks.json;

import org.exoplatform.services.rest.frameworks.json.impl.JSONException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface JSONWriter {
  
//  /**
//   * Write the start of JSON document.
//   * @throws JSONException
//   */
//  void writeStart() throws JSONException;
//
//  /**
//   * Write the end of JSON document.
//   * @throws JSONException
//   */
//  void writeEnd() throws JSONException;
  
  /**
   * Write the start of JSON object '{'.  
   * @throws JSONException
   */
  void writeStartObject() throws JSONException ;
  
  /**
   * Write the end of JSON object '}'.
   * @throws JSONException
   */
  void writeEndObject() throws JSONException ;
  
  /**
   * Write the start of JSON array '['.  
   * @throws JSONException
   */
  void writeStartArray() throws JSONException ;
  
  /**
   * Write the key. After key will go the value.
   * In this way data represented in JSON object.
   * @param key
   * @throws JSONException
   */
  void writeKey(String key) throws JSONException ;
  
  /**
   * Write the end of JSON array ']'.
   * @throws JSONException
   */
  void writeEndArray() throws JSONException ;
  
  /**
   * Write the String to JSON object.
   * @param value the String.
   * @throws JSONException
   */
  void writeString(String value) throws JSONException ;
  
  /**
   * Write the value of long type to JSON object.
   * @param value the value of long type.
   * @throws JSONException
   */
  void writeValue(long value) throws JSONException ;
  
  /**
   * Write the value of double type to JSON object.
   * @param value the value of double type.
   * @throws JSONException
   */
  void writeValue(double value) throws JSONException ;
  
  /**
   * Write the value of boolean type to JSON object.
   * @param value the value of boolean type.
   * @throws JSONException
   */
  void writeValue(boolean value) throws JSONException ;
  
  /**
   * Write the null data in JSON object.
   * @throws JSONException
   */
  void writeNull() throws JSONException ;
  
  /**
   * Flush output writer.
   * @throws JSONException
   */
  void flush() throws JSONException;
  
  /**
   * Close output writer.
   * @throws JSONException
   */
  void close() throws JSONException;
}

