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

package org.exoplatform.services.rest.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.MessageBodyWriter;

import org.exoplatform.services.rest.ContainerResponseWriter;
import org.exoplatform.services.rest.GenericContainerResponse;

/**
 * Mock object that can be used for any tests.
 * 
 * @see ContainerResponseWriter
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ByteArrayContainerResponseWriter implements ContainerResponseWriter {

  /**
   * Message body.
   */
  private byte[]                         body;

  /**
   * HTTP headers.
   */
  private MultivaluedMap<String, Object> headers;

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void writeBody(GenericContainerResponse response, MessageBodyWriter entityWriter) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    Object entity = response.getEntity();
    if (entity != null) {
      entityWriter.writeTo(entity,
                           entity.getClass(),
                           response.getEntityType(),
                           null,
                           response.getContentType(),
                           response.getHttpHeaders(),
                           out);
      body = out.toByteArray();
    }
  }

  /**
   * {@inheritDoc}
   */
  public void writeHeaders(GenericContainerResponse response) throws IOException {
    headers = response.getHttpHeaders();
  }

  /**
   * @return message body
   */
  public byte[] getBody() {
    return body;
  }

  /**
   * @return HTTP headers
   */
  public MultivaluedMap<String, Object> getHeaders() {
    return headers;
  }

  /**
   * Clear message body and HTTP headers map.
   */
  public void reset() {
    body = null;
    headers = null;
  }

}
