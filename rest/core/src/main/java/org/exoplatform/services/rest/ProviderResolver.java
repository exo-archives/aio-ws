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

import org.exoplatform.services.rest.impl.provider.ProviderFactory;
import org.exoplatform.services.rest.provider.EntityProvider;

/**
 * Provide access to providers.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ProviderResolver extends Providers {

  /**
   * Add per-request EntityProvider.
   * 
   * @param provider EntityProvider class
   */
  <T> void addEntityProvider(Class<? extends EntityProvider<T>> provider);

  /**
   * Add singleton EntityProvider.
   * 
   * @param provider EntityProvider instance
   */
  <T> void addEntityProviderInstance(EntityProvider<T> provider);

  /**
   * Add per-request ExceptionMapper.
   * 
   * @param <T> mapped exception type
   * @param providerClass ExceptionMapper class
   * @see ExceptionMapper
   */
  <T extends Throwable> void addExceptionMapper(Class<? extends ExceptionMapper<T>> providerClass);

  /**
   * Add singleton ExceptionMapper.
   * 
   * @param provider ExceptionMapper instance
   */
  void addExceptionMapperInstance(ExceptionMapper<Throwable> provider);

  /**
   * Add per-request ContextResolver.
   * 
   * @param providerClass ContextResolver class
   */
  <T> void addContextResolver(Class<? extends ContextResolver<T>> providerClass);

  /**
   * Add singleton ContextResolver.
   * 
   * @param provider ContextResolver instance
   */
  <T> void addContextResolverInstance(ContextResolver<T> provider);

  /**
   * Add per-request MessageBodyReader.
   * 
   * @param providerClass MessageBodyReader class
   */
  <T> void addMessageBodyReader(Class<? extends MessageBodyReader<T>> providerClass);

  /**
   * Add singleton MessageBodyReader.
   * 
   * @param provider MessageBodyReader instance
   */
  <T> void addMessageBodyReaderInstance(MessageBodyReader<T> provider);

  /**
   * Add per-request MessageBodyWriter.
   * 
   * @param providerClass MessageBodyWriter class
   */
  <T> void addMessageBodyWriter(Class<? extends MessageBodyWriter<T>> providerClass);

  /**
   * Add singleton MessageBodyWriter.
   * 
   * @param provider MessageBodyWriter instance
   */
  <T> void addMessageBodywriterInstance(MessageBodyWriter<T> provider);

  /**
   * Add per-request provider.
   * 
   * @param providerClass provider class
   * @see EntityProvider
   * @see MessageBodyReader
   * @see MessageBodyReader
   * @see ContextResolver
   * @see ExceptionMapper
   * @see ProviderFactory
   */
  void addProvider(Class<?> providerClass);

  /**
   * Add singleton provider.
   * 
   * @param provider provider instance
   * @see EntityProvider
   * @see MessageBodyReader
   * @see MessageBodyReader
   * @see ContextResolver
   * @see ExceptionMapper
   * @see ProviderFactory
   */
  void addProviderInstance(Object provider);

  /**
   * Get all media type which can be reflected to Java types.
   * 
   * @param type the entity type
   * @param genericType the generic entity type
   * @param annotations class annotations
   * @return list of {@link MediaType} which is sorted in order: x/y &lt; x/*
   *         &lt; *\\/*
   */
  List<MediaType> getAcceptableWriterMediaTypes(Class<?> type,
                                                Type genericType,
                                                Annotation[] annotations);
}
