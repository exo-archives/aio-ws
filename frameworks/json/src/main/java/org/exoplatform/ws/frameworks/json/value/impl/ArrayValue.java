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

package org.exoplatform.ws.frameworks.json.value.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id$
 */
public class ArrayValue extends JsonValue {
  
  /**
   * List of children.
   */
  private final List<JsonValue> children = new ArrayList<JsonValue>(); 

  /**
   * {@inheritDoc}
   */
  @Override
  public void addElement(JsonValue child) {
    children.add(child);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isArray() {
    return true;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public Iterator<JsonValue> getElements() {
    return children.iterator();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public int size() {
    return children.size();
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append('[');
    int i = 0;
    for (JsonValue v : children) {
      if (i > 0)
        sb.append(',');
      i++;
      sb.append(v.toString());
    }
    sb.append(']');
    return sb.toString();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeTo(JsonWriter writer) throws JsonException {
    writer.writeStartArray();
    for (JsonValue v : children)
      v.writeTo(writer);
    writer.writeEndArray();
  }

}

