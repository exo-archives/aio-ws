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

package org.exoplatform.services.rest.impl.provider;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.exoplatform.services.rest.provider.EntityProvider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Provider
public class ByteEntityProvider implements EntityProvider<byte[]> {

  /**
   * {@inheritDoc}
   */
  public boolean isReadable(Class<?> type,
                            Type genericType,
                            Annotation[] annotations,
                            MediaType mediaType) {
    return type == byte[].class;
  }

  /**
   * {@inheritDoc}
   */
  public byte[] readFrom(Class<byte[]> type,
                         Type genericType,
                         Annotation[] annotations,
                         MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders,
                         InputStream entityStream) throws IOException {
    
    ByteArrayOutputStream out = new ByteArrayOutputStream();
    IOHelper.write(entityStream, out);
    return out.toByteArray();
  }

  /**
   * {@inheritDoc}
   */
  public long getSize(byte[] t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType) {
    return t.length;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isWriteable(Class<?> type,
                             Type genericType,
                             Annotation[] annotations,
                             MediaType mediaType) {
    return type == byte[].class;
  }

  /**
   * {@inheritDoc}
   */
  public void writeTo(byte[] t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream) throws IOException {
    entityStream.write(t);
  }

}
