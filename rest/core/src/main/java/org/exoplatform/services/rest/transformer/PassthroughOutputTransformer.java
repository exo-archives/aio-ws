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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * This transformer read data from given InputStream and write it to
 * OutputStream. This type of transformers can be usefull when ResourceContainer
 * produce InputStream representation of requested resource.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PassthroughOutputTransformer extends OutputEntityTransformer {

  /**
   * Buffer.
   */
  private static final int BUFF_SIZE = 4096;
  
  /**
   * {@inheritDoc}} .
   */
  @Override
  public final void writeTo(final Object entity, final OutputStream entityDataStream)
      throws IOException {
    InputStream e = (InputStream) entity;
    byte[] buf = new byte[BUFF_SIZE];
    int rd = -1;
    
    while ((rd = e.read(buf)) != -1)
      entityDataStream.write(buf, 0, rd);
  }

}
