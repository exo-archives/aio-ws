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
 * Provide access to providers.
 * 
 * @see javax.ws.rs.ext.Provider
 * @see javax.ws.rs.ext.Providers
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface ProviderResolver extends Providers {

  /**
   * Add per-request EntityProvider.
   * 
   * @param <T> entity provider actual type argument often it is Java type which
   *          provider can serve (read/write)
   * @param provider EntityProvider class
   * @see #addMessageBodyReader(Class)
   * @see #addMessageBodyWriter(Class)
   */
  <T> void addEntityProvider(Class<? extends EntityProvider<T>> provider);

  /**
   * Add singleton EntityProvider.
   * 
   * @param <T> entity provider actual type argument often it is Java type which
   *          provider can serve (read/write)
   * @param provider EntityProvider instance
   * @see #addMessageBodyReaderInstance(MessageBodyReader)
   * @see #addMessageBodyWriterInstance(MessageBodyWriter)
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
   * @param <T> context resolver actual type argument often it is Java type
   *          which resolver can serve
   * @param providerClass ContextResolver class
   */
  <T> void addContextResolver(Class<? extends ContextResolver<T>> providerClass);

  /**
   * Add singleton ContextResolver.
   * 
   * @param <T> context resolver actual type argument often it is Java type
   *          which resolver can serve
   * @param provider ContextResolver instance
   */
  <T> void addContextResolverInstance(ContextResolver<T> provider);

  /**
   * Add per-request MessageBodyReader.
   * 
   * @param <T> message body reader actual type argument often it is Java type
   *          which reader can deserialize from stream
   * @param providerClass MessageBodyReader class
   */
  <T> void addMessageBodyReader(Class<? extends MessageBodyReader<T>> providerClass);

  /**
   * Add singleton MessageBodyReader.
   * 
   * @param <T> message body reader actual type argument often it is Java type
   *          which reader can deserialize from stream
   * @param provider MessageBodyReader instance
   */
  <T> void addMessageBodyReaderInstance(MessageBodyReader<T> provider);

  /**
   * Add per-request MessageBodyWriter.
   * 
   * @param <T> message body writer actual type argument often it is Java type
   *          which writer can serialize to stream
   * @param providerClass MessageBodyWriter class
   */
  <T> void addMessageBodyWriter(Class<? extends MessageBodyWriter<T>> providerClass);

  /**
   * Add singleton MessageBodyWriter.
   * 
   * @param <T> message body writer actual type argument often it is Java type
   *          which writer can serialize to stream
   * @param provider MessageBodyWriter instance
   */
  <T> void addMessageBodyWriterInstance(MessageBodyWriter<T> provider);

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
