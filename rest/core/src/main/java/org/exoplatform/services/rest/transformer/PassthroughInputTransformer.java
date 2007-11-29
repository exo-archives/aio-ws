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

/**
 * This transformer does not do any transformation. It can be usefull when
 * ResourceContainer does not request about building Object from stream and
 * ResourceContainer needs input stream without any changes.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class PassthroughInputTransformer extends InputEntityTransformer {

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.transformer.InputEntityTransformer#readFrom(java.io.InputStream)
   */
  @Override
  public final Object readFrom(final InputStream entityDataStream)
      throws IOException {
    return entityDataStream;
  }

}
