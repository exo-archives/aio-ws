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
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;

/**
 * Create Object from given InputStream using JAXB transformation. Java
 * Architecture for XML Binding (JAXB) allows create and edit XML using familiar
 * Java objects.<br/> JAXB is particularly useful when the specification is
 * complex and changing. This class can unmarshal XML given a stream into Java
 * object.<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class JAXBInputTransformer extends InputEntityTransformer {

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.transformer.InputEntityTransformer#readFrom(java.io.InputStream)
   */
  @Override
  public final Object readFrom(final InputStream entityDataStream) throws IOException {
    try {
      JAXBContext jaxbContext = JAXBContext.newInstance(entityType_);
      return jaxbContext.createUnmarshaller().unmarshal(entityDataStream);
    } catch (JAXBException jaxbe) {
      throw new IOException("Can't transform InputStream to Object: " + jaxbe);
    }
  }

}
