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

package org.exoplatform.services.rest.transformer;

import java.io.InputStream;
import java.io.IOException;

/**
 * This type of transformers can work whith objects which implement interface
 * DeserializableEntity. Transformer use own method of Object for reading object
 * from input stream.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class DeserializableTransformer extends InputEntityTransformer {

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.transformer.InputEntityTransformer#readFrom(java.io.InputStream)
   */
  public final DeserializableEntity readFrom(final InputStream entityDataStream)
      throws IOException {
    try {
      DeserializableEntity e = (DeserializableEntity) entityType_.newInstance();
      e.readObject(entityDataStream);
      return e;
    } catch (IllegalAccessException iae) {
      throw new IOException("Can't read from input stream. Exception: " + iae);
    } catch (InstantiationException ie) {
      throw new IOException("Can't read from input stream. Exception: " + ie);
    }
  }
}
