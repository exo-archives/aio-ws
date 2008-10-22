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

import org.exoplatform.ws.frameworks.json.JsonWriter;
import org.exoplatform.ws.frameworks.json.impl.JsonException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DoubleValue extends NumericValue {

  /**
   * Value.
   */
  private final double value;

  /**
   * Constructs new DoubleValue.
   * @param value the value.
   */
  public DoubleValue(double value) {
    this.value = value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isDouble() {
    return true;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String getStringValue() {
    return Double.toString(value);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public byte getByteValue() {
    return (byte) value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public short getShortValue() {
    return (short) value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public int getIntValue() {
    return (int) value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public long getLongValue() {
    return (long) value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public float getFloatValue() {
    return (float) value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public double getDoubleValue() {
    return value;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    return getStringValue();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public void writeTo(JsonWriter writer) throws JsonException {
    writer.writeValue(value);
  }

}
