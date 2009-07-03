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

package org.exoplatform.ws.rest.ejbconnector21;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.ext.MessageBodyWriter;

import org.exoplatform.services.rest.ContainerResponseWriter;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.ext.transport.SerialInputData;
import org.exoplatform.services.rest.ext.transport.SerialResponse;
import org.exoplatform.services.rest.impl.header.HeaderHelper;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EJBContainerResponseWriter implements ContainerResponseWriter {

  private SerialResponse serialResponse;

  public EJBContainerResponseWriter(SerialResponse serialResponse) {
    this.serialResponse = serialResponse;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void writeBody(GenericContainerResponse response, MessageBodyWriter entityWriter) throws IOException {

    if (response.getEntity() != null) {

      final File file = File.createTempFile("ws_rs_ejb", null);
      OutputStream out = new FileOutputStream(file);

      Object entity = response.getEntity();
      if (entity != null) {
        entityWriter.writeTo(entity,
                             entity.getClass(),
                             response.getEntityType(),
                             null,
                             response.getContentType(),
                             response.getHttpHeaders(),
                             out);
        out.flush();
      }

      InputStream in = new FileInputStream(file) {
        private boolean removed = false;

        /**
         * {@inheritDoc}
         */
        @Override
        public void close() throws IOException {
          try {
            super.close();
          } finally {
            // file must be removed after using
            removed = file.delete();
          }
        }

        /**
         * {@inheritDoc}
         */
        @Override
        protected void finalize() throws IOException {
          try {
            // if file was not removed
            if (!removed)
              file.delete();

          } finally {
            super.finalize();
          }
        }
      };

      serialResponse.setData(new SerialInputData(in));
    }

  }

  /**
   * {@inheritDoc}
   */
  public void writeHeaders(GenericContainerResponse response) throws IOException {
    serialResponse.setStatus(response.getStatus());
    if (response.getHttpHeaders() != null) {
      for (Entry<String, List<Object>> e : response.getHttpHeaders().entrySet()) {
        String name = e.getKey();
        for (Object o : e.getValue()) {
          String value = null;
          if (o != null && (value = HeaderHelper.getHeaderAsString(o)) != null)
            serialResponse.addHeader(name, value);
        }
      }
    }
  }

}
