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

import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.header.AbstractHeaderDelegate;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AcceptMediaTypeHeaderDelegate extends AbstractHeaderDelegate<AcceptMediaType> {

  /**
   * {@inheritDoc}
   */
  @Override
  public Class<AcceptMediaType> support() {
    return AcceptMediaType.class;
  }

  /**
   * {@inheritDoc}
   */
  public AcceptMediaType fromString(String header) {
    if (header == null)
      throw new IllegalArgumentException();

    MediaType mediaType = MediaType.valueOf(header);

    return new AcceptMediaType(mediaType.getType(),
                                 mediaType.getSubtype(),
                                 mediaType.getParameters());

  }

  /**
   * {@inheritDoc}
   */
  public String toString(AcceptMediaType acceptedMediaType) {
    throw new UnsupportedOperationException("Accepted media type header used only for request.");
  }

}
