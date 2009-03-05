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

package org.exoplatform.services.rest.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.core.CacheControl;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.EntityTag;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.Response.ResponseBuilder;
import javax.ws.rs.core.Variant.VariantListBuilder;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ProviderResolver;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.impl.header.AcceptLanguage;
import org.exoplatform.services.rest.impl.header.AcceptLanguageHeaderDelegate;
import org.exoplatform.services.rest.impl.header.AcceptMediaType;
import org.exoplatform.services.rest.impl.header.AcceptMediaTypeHeaderDelegate;
import org.exoplatform.services.rest.impl.header.CacheControlHeaderDelegate;
import org.exoplatform.services.rest.impl.header.CookieHeaderDelegate;
import org.exoplatform.services.rest.impl.header.DateHeaderDelegate;
import org.exoplatform.services.rest.impl.header.EntityTagHeaderDelegate;
import org.exoplatform.services.rest.impl.header.LocaleHeaderDelegate;
import org.exoplatform.services.rest.impl.header.MediaTypeHeaderDelegate;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.impl.header.NewCookieHeaderDelegate;
import org.exoplatform.services.rest.impl.header.StringHeaderDelegate;
import org.exoplatform.services.rest.impl.header.URIHeaderDelegate;
import org.exoplatform.services.rest.impl.provider.PerRequestProviderFactory;
import org.exoplatform.services.rest.impl.provider.ProviderDescriptorImpl;
import org.exoplatform.services.rest.impl.provider.ProviderFactory;
import org.exoplatform.services.rest.impl.provider.SingletonProviderFactory;
import org.exoplatform.services.rest.impl.uri.UriBuilderImpl;
import org.exoplatform.services.rest.impl.uri.UriPattern;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.provider.EntityProvider;
import org.exoplatform.services.rest.provider.ProviderDescriptor;
import org.exoplatform.services.rest.util.EntityProviderMap;
import org.exoplatform.services.rest.util.FilterMap;
import org.exoplatform.services.rest.util.MediaTypeMap;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class RuntimeDelegateImpl extends RuntimeDelegate implements ProviderResolver {

  /**
   * Logger.
   */
  private static final Log                           LOG = ExoLogger.getLogger(RuntimeDelegateImpl.class.getName());

  /**
   * HeaderDelegate cache.
   */
  @SuppressWarnings("unchecked")
  private static final Map<Class<?>, HeaderDelegate> HDS = new HashMap<Class<?>, HeaderDelegate>();

  public static RuntimeDelegateImpl getInstance() {
    return (RuntimeDelegateImpl) RuntimeDelegate.getInstance();
  }

  static {
    // add prepared HeaderDelegate according to JSR-311 and some external
    HDS.put(MediaType.class, new MediaTypeHeaderDelegate());
    HDS.put(CacheControl.class, new CacheControlHeaderDelegate());
    HDS.put(Cookie.class, new CookieHeaderDelegate());
    HDS.put(NewCookie.class, new NewCookieHeaderDelegate());
    HDS.put(EntityTag.class, new EntityTagHeaderDelegate());
    HDS.put(Date.class, new DateHeaderDelegate());
    // addition
    HDS.put(AcceptLanguage.class, new AcceptLanguageHeaderDelegate());
    HDS.put(AcceptMediaType.class, new AcceptMediaTypeHeaderDelegate());
    HDS.put(String.class, new StringHeaderDelegate());
    HDS.put(URI.class, new URIHeaderDelegate());
    HDS.put(Locale.class, new LocaleHeaderDelegate());
  }

  /**
   * Read message body providers. Also see {@link EntityProviderMap}.
   */
  private final EntityProviderMap                                writeProviders   = new EntityProviderMap();

  /**
   * Read message body providers. Also see {@link EntityProviderMap}.
   */
  private final EntityProviderMap                                readProviders    = new EntityProviderMap();

  /**
   * Exception mappers, see {@link ExceptionMapper}.
   */
  private final Map<Class<? extends Throwable>, ProviderFactory> exceptionMappers = new HashMap<Class<? extends Throwable>, ProviderFactory>();

  /**
   * Context resolvers.
   */
  private final Map<Class<?>, MediaTypeMap<ProviderFactory>>     contextResolvers = new HashMap<Class<?>, MediaTypeMap<ProviderFactory>>();

  /**
   * Request filters, see {@link RequestFilter}.
   */
  private final FilterMap<RequestFilter>                         requestFilters   = new FilterMap<RequestFilter>();

  /**
   * Response filters, see {@link ResponseFilter}.
   */
  private final FilterMap<ResponseFilter>                        responseFilters  = new FilterMap<ResponseFilter>();

  /**
   * Method invoking filters.
   */
  private final List<MethodInvokerFilter>                        invokerFilters   = new ArrayList<MethodInvokerFilter>();

  /**
   * Should be used only once for initialize.
   * 
   * @see RuntimeDelegate#setInstance(RuntimeDelegate)
   * @see RuntimeDelegate#getInstance()
   */
  public RuntimeDelegateImpl() {
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addContextResolver(Class<ContextResolver<T>> providerClass) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(providerClass);
    ProviderFactory pf = new PerRequestProviderFactory(descriptor);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add ContextResolver " + providerClass.getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addContextResolverInstance(ContextResolver<T> provider) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(provider.getClass());
    ProviderFactory pf = new SingletonProviderFactory(descriptor, provider);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add ContextResolver " + provider.getClass().getName(), e);
    }
  }

  public <T> void addEntityProvider(Class<EntityProvider<T>> providerClass) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(providerClass);
    ProviderFactory pf = new PerRequestProviderFactory(descriptor);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add EntityProvider " + providerClass.getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addEntityProviderInstance(EntityProvider<T> provider) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(provider.getClass());
    ProviderFactory pf = new SingletonProviderFactory(descriptor, provider);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add EntityProvider " + provider.getClass().getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T extends Throwable> void addExceptionMapper(Class<ExceptionMapper<T>> providerClass) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(providerClass);
    ProviderFactory pf = new PerRequestProviderFactory(descriptor);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add ExceptionMapper " + providerClass.getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void addExceptionMapperInstance(ExceptionMapper provider) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(provider.getClass());
    ProviderFactory pf = new SingletonProviderFactory(descriptor, provider);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add ExceptionMapper " + provider.getClass().getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addMessageBodyReader(Class<MessageBodyReader<T>> providerClass) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(providerClass);
    ProviderFactory pf = new PerRequestProviderFactory(descriptor);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add MessageBodyReader " + providerClass.getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addMessageBodyReaderInstance(MessageBodyReader<T> provider) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(provider.getClass());
    ProviderFactory pf = new SingletonProviderFactory(descriptor, provider);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add MessageBodyReader " + provider.getClass().getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addMessageBodyWriter(Class<MessageBodyWriter<T>> providerClass) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(providerClass);
    ProviderFactory pf = new PerRequestProviderFactory(descriptor);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add MessageBodyWriter " + providerClass.getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addMessageBodywriterInstance(MessageBodyWriter<T> provider) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(provider.getClass());
    ProviderFactory pf = new SingletonProviderFactory(descriptor, provider);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add MessageBodyWriter " + provider.getClass().getName(), e);
    }
  }

  /**
   * @param filter see {@link MethodInvokerFilter}
   */
  public void addMethodInvokerFilter(MethodInvokerFilter filter) {
    invokerFilters.add(filter);
  }

  /**
   * {@inheritDoc}
   */
  public void addProvider(Class<?> providerClass) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(providerClass);
    ProviderFactory pf = new PerRequestProviderFactory(descriptor);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add Provider " + providerClass.getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void addProviderInstance(Object provider) {
    ProviderDescriptor descriptor = new ProviderDescriptorImpl(provider.getClass());
    ProviderFactory pf = new SingletonProviderFactory(descriptor, provider);
    try {
      addProvider(pf);
    } catch (Exception e) {
      LOG.error("Unable add Provider " + provider.getClass().getName(), e);
    }
  }

  /**
   * Add {@link ProviderFactory}.
   * 
   * @param factory ProviderFactory
   */
  @SuppressWarnings("unchecked")
  private void addProvider(ProviderFactory factory) {
    if (MessageBodyReader.class.isAssignableFrom(factory.getProviderClass())) {
      for (MediaType mime : factory.consumes())
        readProviders.getList(mime).add(factory);
    }
    if (MessageBodyWriter.class.isAssignableFrom(factory.getProviderClass())) {
      for (MediaType mime : factory.produces())
        writeProviders.getList(mime).add(factory);
    }
    if (ExceptionMapper.class.isAssignableFrom(factory.getProviderClass())) {
      for (Type t : factory.getProviderClass().getGenericInterfaces()) {
        if (t instanceof ParameterizedType) {
          ParameterizedType p = (ParameterizedType) t;
          if (ExceptionMapper.class == p.getRawType()) {
            Type[] ta = p.getActualTypeArguments();
            try {
              Class<? extends Throwable> exc = (Class<? extends Throwable>) ta[0];
              exceptionMappers.put(exc, factory);
            } catch (ClassCastException e) {
              throw new RuntimeException("ExceptionMapper parameterized by incorrect type " + ta[0]);
            }
          }
        }
      }
    }
    if (ContextResolver.class.isAssignableFrom(factory.getProviderClass())) {
      for (Type t : factory.getProviderClass().getGenericInterfaces()) {
        if (t instanceof ParameterizedType) {
          ParameterizedType p = (ParameterizedType) t;
          if (ContextResolver.class == p.getRawType()) {
            Type[] ta = p.getActualTypeArguments();
            try {
              Class<?> clazz = (Class<?>) ta[0];
              MediaTypeMap<ProviderFactory> pm = contextResolvers.get(clazz);
              if (pm == null) {
                pm = new MediaTypeMap<ProviderFactory>();
                contextResolvers.put(clazz, pm);
              }
              for (MediaType mime : factory.produces())
                pm.put(mime, factory);
            } catch (ClassCastException e) {
              throw new RuntimeException();
            }
          }
        }
      }
    }
  }

  /**
   * @param filter See {@link ResponseFilter}
   */
  public void addResponseFilter(ResponseFilter filter) {
    final Path pathAnnotation = filter.getClass().getAnnotation(Path.class);
    String path = pathAnnotation == null ? "/" : pathAnnotation.value();
    UriPattern uriPattern = new UriPattern(path);
    responseFilters.getList(uriPattern).add(filter);

  }

  /**
   * @param filter See {@link RequestFilter}
   */
  public void addRequestFilter(RequestFilter filter) {
    final Path pathAnnotation = filter.getClass().getAnnotation(Path.class);
    String path = pathAnnotation == null ? "/" : pathAnnotation.value();
    UriPattern uriPattern = new UriPattern(path);
    requestFilters.getList(uriPattern).add(filter);
  }

  /**
   * End Points is not supported. {@inheritDoc}
   */
  @Override
  public <T> T createEndpoint(Application applicationConfig, Class<T> type) {
    throw new UnsupportedOperationException("End Points is not supported");
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  @Override
  public <T> HeaderDelegate<T> createHeaderDelegate(Class<T> type) {
    // TODO mechanism for use external HeaderDelegate
    return (HeaderDelegate<T>) HDS.get(type);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public ResponseBuilder createResponseBuilder() {
    return new ResponseImpl.ResponseBuilderImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public UriBuilder createUriBuilder() {
    return new UriBuilderImpl();
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public VariantListBuilder createVariantListBuilder() {
    return new VariantListBuilderImpl();
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public List<MediaType> getAcceptableWriterMediaTypes(Class<?> type,
                                                       Type genericType,
                                                       Annotation[] annotations) {
    List<MediaType> l = new ArrayList<MediaType>();
    for (Map.Entry<MediaType, List<ProviderFactory>> e : writeProviders.entrySet()) {
      MediaType mime = e.getKey();
      for (ProviderFactory pf : e.getValue()) {
        MessageBodyWriter writer = (MessageBodyWriter) pf.getProvider(ApplicationContextImpl.getCurrent());
        if (writer.isWriteable(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE))
          l.add(mime);
      }
    }

    Collections.sort(l, MediaTypeHelper.MEDIA_TYPE_COMPARATOR);
    return l;
  }

  /**
   * {@inheritDoc}
   */
  public <T> ContextResolver<T> getContextResolver(Class<T> contextType, MediaType mediaType) {
    MediaTypeMap<ProviderFactory> pm = contextResolvers.get(contextType);
    ContextResolver<T> resolver = null;
    if (pm != null) {
      if (mediaType == null)
        return getContextResolver(pm, contextType, MediaTypeHelper.DEFAULT_TYPE);

      resolver = getContextResolver(pm, contextType, mediaType);
      if (resolver == null)
        resolver = getContextResolver(pm, contextType, new MediaType(mediaType.getType(),
                                                                     MediaType.MEDIA_TYPE_WILDCARD));
      if (resolver == null)
        resolver = getContextResolver(pm, contextType, MediaTypeHelper.DEFAULT_TYPE);
    }
    return resolver;
  }

  @SuppressWarnings("unchecked")
  private <T> ContextResolver<T> getContextResolver(MediaTypeMap<ProviderFactory> pm,
                                                    Class<T> contextType,
                                                    MediaType mediaType) {
    for (Map.Entry<MediaType, ProviderFactory> e : pm.entrySet()) {
      if (mediaType.isCompatible(e.getKey())) {
        return (ContextResolver<T>) e.getValue().getProvider(ApplicationContextImpl.getCurrent());
      }
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type) {
    ProviderFactory pf = exceptionMappers.get(type);
    if (pf != null)
      return (ExceptionMapper<T>) pf.getProvider(ApplicationContextImpl.getCurrent());
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public <T> MessageBodyReader<T> getMessageBodyReader(Class<T> type,
                                                       Type genericType,
                                                       Annotation[] annotations,
                                                       MediaType mediaType) {
    if (mediaType == null)
      return getMessageBodyReader0(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);

    MessageBodyReader<T> reader = getMessageBodyReader0(type, genericType, annotations, mediaType);
    if (reader == null)
      reader = getMessageBodyReader0(type,
                                     genericType,
                                     annotations,
                                     new MediaType(mediaType.getType(),
                                                   MediaType.MEDIA_TYPE_WILDCARD));
    if (reader == null)
      reader = getMessageBodyReader0(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);

    return reader;
  }

  /**
   * Looking for message body reader according to supplied entity class, entity
   * generic type, annotations and content type.
   * 
   * @param type entity type
   * @param genericType entity generic type
   * @param annotations annotations
   * @param mediaType entity content type
   * @return message body reader or null if no one was found.
   */
  @SuppressWarnings("unchecked")
  private <T> MessageBodyReader<T> getMessageBodyReader0(Class<T> type,
                                                         Type genericType,
                                                         Annotation[] annotations,
                                                         MediaType mediaType) {
    for (ProviderFactory pf : readProviders.getList(mediaType)) {
      MessageBodyReader reader = (MessageBodyReader) pf.getProvider(ApplicationContextImpl.getCurrent());
      if (reader.isReadable(type, genericType, annotations, mediaType))
        return reader;
    }
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type,
                                                       Type genericType,
                                                       Annotation[] annotations,
                                                       MediaType mediaType) {
    if (mediaType == null)
      return getMessageBodyWriter0(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);

    MessageBodyWriter<T> writer = getMessageBodyWriter0(type, genericType, annotations, mediaType);
    if (writer == null)
      writer = getMessageBodyWriter0(type,
                                     genericType,
                                     annotations,
                                     new MediaType(mediaType.getType(),
                                                   MediaType.MEDIA_TYPE_WILDCARD));
    if (writer == null)
      writer = getMessageBodyWriter0(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);
    return writer;
  }

  /**
   * Looking for message body writer according to supplied entity class, entity
   * generic type, annotations and content type.
   * 
   * @param type entity type
   * @param genericType entity generic type
   * @param annotations annotations
   * @param mediaType content type in which entity should be represented
   * @return message body writer or null if no one was found.
   */
  @SuppressWarnings("unchecked")
  private <T> MessageBodyWriter<T> getMessageBodyWriter0(Class<T> type,
                                                         Type genericType,
                                                         Annotation[] annotations,
                                                         MediaType mediaType) {
    for (ProviderFactory pf : writeProviders.getList(mediaType)) {
      MessageBodyWriter writer = (MessageBodyWriter) pf.getProvider(ApplicationContextImpl.getCurrent());
      if (writer.isWriteable(type, genericType, annotations, mediaType))
        return writer;
    }
    return null;
  }

  /**
   * @return method invocation filters
   */
  public List<MethodInvokerFilter> getMethodInvokerFilters() {
    return invokerFilters;
  }

  /**
   * @return request filters
   */
  public FilterMap<RequestFilter> getRequestFilters() {
    return requestFilters;
  }

  /**
   * @return response filters
   */
  public FilterMap<ResponseFilter> getResponseFilters() {
    return responseFilters;
  }

}
