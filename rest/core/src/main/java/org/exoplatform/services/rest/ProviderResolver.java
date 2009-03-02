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
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.Providers;

import org.exoplatform.services.rest.provider.EntityProvider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ProviderResolver extends Providers {

  <T> void addEntityProvider(Class<EntityProvider<T>> provider);

  /**
   * Add new {@link EntityProvider}.
   * 
   * @param provider See {@link EntityProvider}
   */
  <T> void addEntityProviderInstance(EntityProvider<T> provider);
  
  
  <T extends Throwable> void addExceptionMapper(Class<ExceptionMapper<T>> providerClass);
  
  void addExceptionMapperInstance(ExceptionMapper<Throwable> provider);
  
  
  <T> void addContextResolver(Class<ContextResolver<T>> providerClass);
  
  <T> void addContextResolverInstance(ContextResolver<T> provider);
  
  
  <T> void addMessageBodyReader(Class<MessageBodyReader<T>> providerClass);

  <T> void addMessageBodyReaderInstance(MessageBodyReader<T> provider);
  

  <T> void addMessageBodyWriter(Class<MessageBodyWriter<T>> providerClass);

  <T> void addMessageBodywriterInstance(MessageBodyWriter<T> provider);
  

  void addProvider(Class<?> providerClass);
  
  void addProviderInstance(Object provider);
  
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
}
