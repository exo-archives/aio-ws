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


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LongValue extends NumericValue {

  private final long value_;
  
  public LongValue(long value) {
    value_ = value;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#isLong()
   */
  @Override
  public boolean isLong() {
    return true;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getStringValue()
   */
  @Override
  public String getStringValue() {
    return Long.toString(value_);
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getByteValue()
   */
  @Override
  public byte getByteValue() {
    return (byte)value_;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getShortValue()
   */
  @Override
  public short getShortValue() {
    return (short)value_;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getIntValue()
   */
  @Override
  public int getIntValue() {
    return (int)value_;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getLongValue()
   */
  @Override
  public long getLongValue() {
    return value_;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getDoubleValue()
   */
  @Override
  public double getDoubleValue() {
    return value_;
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#getFloatValue()
   */
  @Override
  public float getFloatValue() {
    return value_;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.impl.NumericValue#toString()
   */
  @Override
  public String toString() {
    return getStringValue();
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.rest.frameworks.json.value.JsonValue#writeTo(
   * org.exoplatform.services.rest.frameworks.json.JsonWriter)
   */
  @Override
  public void writeTo(JsonWriter writer) throws JsonException {
    writer.writeValue(value_);
  }

}

