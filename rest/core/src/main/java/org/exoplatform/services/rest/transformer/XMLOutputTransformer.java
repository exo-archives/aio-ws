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
import java.io.OutputStream;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

/**
 * This type of transformer can write XML in output stream.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class XMLOutputTransformer extends OutputEntityTransformer {

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.transformer.OutputEntityTransformer#writeTo
   *      (java.lang.Object, java.io.OutputStream)
   */
  @Override
  public final void writeTo(final Object entity, final OutputStream entityDataStream)
      throws IOException {
    Document e = (Document) entity;
    try {
      TransformerFactory.newInstance().newTransformer().transform(
          new DOMSource(e), new StreamResult(entityDataStream));
    } catch (TransformerException tre) {
      throw new IOException("Can't write to output stream " + tre);
    }
  }

}
