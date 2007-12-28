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

import java.io.OutputStream;
import java.io.IOException;

/**
 * This type of transformers can work with objects which implement interface
 * SerializableEntity. Transformer use own method of Object for writing object
 * in output stream.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SerializableTransformer extends OutputEntityTransformer {

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.transformer.OutputEntityTransformer#writeTo(java.lang.Object,
   *      java.io.OutputStream)
   */
  public final void writeTo(final Object entity,
      final OutputStream entityDataStream) throws IOException {
    SerializableEntity e = (SerializableEntity) entity;
    e.writeObject(entityDataStream);
  }
}
