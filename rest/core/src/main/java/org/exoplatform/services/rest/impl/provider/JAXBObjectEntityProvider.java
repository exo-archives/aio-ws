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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.ContextResolver;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.UnmarshalException;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.provider.EntityProvider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Consumes({ MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML })
@Produces({ MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.APPLICATION_XHTML_XML,
    MediaTypeHelper.WADL })
public class JAXBObjectEntityProvider implements EntityProvider<Object> {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(JAXBObjectEntityProvider.class.getName());
  
  /**
   * See {@link JAXBContextResolver}.
   */
  private ContextResolver<JAXBContext> jaxbContextResolver;

  /**
   * This method should be called before using method
   * {@link #readFrom(Class, Type, Annotation[], MediaType, MultivaluedMap, InputStream)}
   * or
   * {@link #writeTo(Object, Class, Type, Annotation[], MediaType, MultivaluedMap, OutputStream)}.
   * 
   * @param jaxbContexts See {@link JAXBContextResolver}
   */
  public void setContexResolver(ContextResolver<JAXBContext> jaxbContexts) {
    this.jaxbContextResolver = jaxbContexts;
  }

  // EntityProvider

  /**
   * {@inheritDoc}
   */
  public boolean isReadable(Class<?> type,
                            Type genericType,
                            Annotation[] annotations,
                            MediaType mediaType) {
    return type.getAnnotation(XmlRootElement.class) != null;
  }

  /**
   * {@inheritDoc}
   */
  public Object readFrom(Class<Object> type,
                         Type genericType,
                         Annotation[] annotations,
                         MediaType mediaType,
                         MultivaluedMap<String, String> httpHeaders,
                         InputStream entityStream) throws IOException {
    try {
      return jaxbContextResolver.getContext(type).createUnmarshaller().unmarshal(entityStream);
    } catch (UnmarshalException e) {
      // if can't read from stream (e.g. steam is empty)
      if (LOG.isDebugEnabled())
        e.printStackTrace();
      return null;
    } catch (JAXBException e) {
      throw new IOException("Can't read from input stream " + e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public long getSize(Object t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType) {
    return -1;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isWriteable(Class<?> type,
                             Type genericType,
                             Annotation[] annotations,
                             MediaType mediaType) {
    return type.getAnnotation(XmlRootElement.class) != null;
  }

  /**
   * {@inheritDoc}
   */
  public void writeTo(Object t,
                      Class<?> type,
                      Type genericType,
                      Annotation[] annotations,
                      MediaType mediaType,
                      MultivaluedMap<String, Object> httpHeaders,
                      OutputStream entityStream) throws IOException {
    try {
      Marshaller m = jaxbContextResolver.getContext(type).createMarshaller();
      // Must respect application specified character set.
      String charset = mediaType.getParameters().get("charset");
      if (charset != null)
        m.setProperty(Marshaller.JAXB_ENCODING, charset);

      m.marshal(t, entityStream);
    } catch (JAXBException e) {
      throw new IOException("Can't write to output stream " + e);
    }
  }

}
