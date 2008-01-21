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

package org.exoplatform.ws.framework.json.value;

import java.util.ArrayList;
import java.util.Iterator;

import org.exoplatform.ws.framework.json.JsonWriter;
import org.exoplatform.ws.framework.json.impl.JsonException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class JsonValue {
  
  //  set defaults for specific types
  // It will be overridden.
  public boolean isObject() {
    return false;
  }
  public boolean isArray() {
    return false;
  }
  public boolean isNumeric() {
    return false;
  }
  public boolean isLong() {
    return false;
  }
  public boolean isDouble() {
    return false;
  }
  public boolean isString() {
    return false;
  }
  public boolean isBoolean() {
    return false;
  }
  public boolean isNull() {
    return false;
  }
  
  /**
   * Add child value. This method must be used if isArray() gives true. 
   * @param child the child value.
   */
  public void addElement(JsonValue child) {
    throw new UnsupportedOperationException("This type of JsonValue can't have child.");
  }

  /**
   * Add child value. This method must be used if isObject() gives true. 
   * @param key the key.
   * @param child the child value.
   */
  public void addElement(String key, JsonValue child) {
    throw new UnsupportedOperationException("This type of JsonValue can't have child.");
  }
  
  /**
   * Get all element of this value.
   * @return 
   */
  public Iterator<JsonValue> getElements() {
    return new ArrayList<JsonValue>().iterator();
  }
  
  /**
   * Get all keys for access values.
   * @return
   */
  public Iterator<String> getKeys() {
    return new ArrayList<String>().iterator();
  }
  
  /**
   * Get value by key.
   * @param key the key.
   * @return
   */
  public JsonValue getElement(String key) {
    return null;
  }
  
  /**
   * @return number of child elements.
   */
  public int size() {
    return 0;
  }
  
  // Prepared values of know type.
  // It will be overridden.
  public String getStringValue() {
    return null;
  }
  
  public boolean getBooleanValue() {
    return false;
  }
  
  public Number getNumberValue() {
    return Integer.valueOf(getIntValue());
  }
  
  public byte getByteValue() {
    return 0;
  }

  public short getShortValue() {
    return 0;
  }

  public int getIntValue() {
    return 0;
  }
  
  public long getLongValue() {
    return 0L;
  }
  
  public float getFloatValue() {
    return 0.0F;
  }                                                                           

  public double getDoubleValue() {
    return 0.0;
  }
  
  //  must be implemented
  @Override
  public abstract String toString(); 
  
  public abstract void writeTo(JsonWriter writer) throws JsonException; 
  
}

