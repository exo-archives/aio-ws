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

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonException;
import org.exoplatform.ws.frameworks.json.impl.JsonUtils;
import org.exoplatform.ws.frameworks.json.value.JsonValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ObjectValue extends JsonValue {

  private final Map<String, JsonValue> children_ = new HashMap<String, JsonValue>();
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#addElement(
   * java.lang.String, org.exoplatform.services.rest.frameworks.json.value.JsonValue)
   */
  @Override
  public void addElement(String key, JsonValue child) {
    children_.put(key, child);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#isObject()
   */
  @Override
  public boolean isObject() {
    return true;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getKeys()
   */
  @Override
  public Iterator<String> getKeys() {
    return children_.keySet().iterator();
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getElement(java.lang.String)
   */
  @Override
  public JsonValue getElement(String key) {
    return children_.get(key);
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#toString()
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append('{');
    int i = 0;
    for (String key : children_.keySet()) {
      if (i > 0)
        sb.append(',');
      i++;
      sb.append(JsonUtils.getJsonString(key));
      sb.append(':');
      sb.append(children_.get(key).toString());
    }
    sb.append('}');
    return sb.toString();
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#writeTo(
   * org.exoplatform.services.rest.frameworks.json.JsonWriter)
   */
  @Override
  public void writeTo(JsonWriter writer) throws JsonException {
    writer.writeStartObject();
    for (String key : children_.keySet()) {
      writer.writeKey(key);
      JsonValue v = children_.get(key);
      v.writeTo(writer);
    }
    writer.writeEndObject();
  }

}

