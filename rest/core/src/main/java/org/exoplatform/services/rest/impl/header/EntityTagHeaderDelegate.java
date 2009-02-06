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

package org.exoplatform.services.rest.impl.header;

import javax.ws.rs.core.EntityTag;

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EntityTagHeaderDelegate extends AbstractHeaderDelegate<EntityTag> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<EntityTag> support() {
    return EntityTag.class;
  }

  /**
   * {@inheritDoc}
   */
  public EntityTag fromString(String header) {
    if (header == null)
      throw new IllegalArgumentException();

    boolean isWeak = header.startsWith("W/") ? true : false;

    String value;
    // cut 'W/' prefix if exists
    if (isWeak)
      value = header.substring(2);
    else
      value = header;
    // remove quotes
    value = value.substring(1, value.length() - 1);
    value = HeaderHelper.filterEscape(value);

    return new EntityTag(value, isWeak);
  }

  /**
   * {@inheritDoc}
   */
  public String toString(EntityTag entityTag) {
    StringBuffer sb = new StringBuffer();
    if (entityTag.isWeak())
      sb.append('W').append('/');

    sb.append('"');
    HeaderHelper.appendEscapeQuote(sb, entityTag.getValue());
    sb.append('"');

    return sb.toString();
  }

}
