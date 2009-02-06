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

package org.exoplatform.services.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.exoplatform.services.rest.provider.EntityProvider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface EntityProviderResolver {

  /**
   * Add new {@link EntityProvider}.
   * 
   * @param provider See {@link EntityProvider}
   */
  void addEntityProvider(EntityProvider<?> provider);

  /**
   * Get all media type which can be reflected to Java types.
   * 
   * @param type the entity type
   * @param genericType the generic entity type
   * @param annotations class annotations
   * @return list of {@link MediaType} which is sorted in order:
   * x/y &lt; x/* &lt; *\\/*
   */
  List<MediaType> getAcceptableWriterMediaTypes(Class<?> type,
                                                Type genericType,
                                                Annotation[] annotations);
  /**
   * Get {@link MessageBodyReader}.
   * 
   * @param type the entity type
   * @param genericType the generic entity type
   * @param annotations class annotations
   * @param contentType entity content type
   * @return message body reader or null if no one appropriate writer found
   */
  MessageBodyReader<?> getMessageBodyReader(Class<?> type,
                                            Type genericType,
                                            Annotation[] annotations,
                                            MediaType contentType);

  /**
   * Get {@link MessageBodyWriter}.
   * 
   * @param type the entity type
   * @param genericType the generic entity type
   * @param annotations class annotations
   * @param contentType entity content type
   * @return message body writer or null if no one appropriate writer found
   */
  MessageBodyWriter<?> getMessageBodyWriter(Class<?> type,
                                            Type genericType,
                                            Annotation[] annotations,
                                            MediaType contentType);

}
