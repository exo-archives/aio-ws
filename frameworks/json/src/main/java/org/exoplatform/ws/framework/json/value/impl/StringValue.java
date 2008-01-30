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

package org.exoplatform.ws.framework.json.value.impl;

import org.exoplatform.ws.framework.json.JsonWriter;
import org.exoplatform.ws.framework.json.impl.JsonException;
import org.exoplatform.ws.framework.json.value.JsonValue;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class StringValue extends JsonValue {
  
  private final String value_;
  
  public StringValue(String value) {
    value_ = value;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#isString()
   */
  @Override
  public boolean isString() {
    return true;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getStringValue()
   */
  @Override
  public String getStringValue() {
    return value_;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#toString()
   */
  @Override
  public String toString() {
    return value_;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#writeTo(
   * org.exoplatform.services.rest.frameworks.json.JsonWriter)
   */
  @Override
  public void writeTo(JsonWriter writer) throws JsonException {
    writer.writeString(value_);
  }
  
}

