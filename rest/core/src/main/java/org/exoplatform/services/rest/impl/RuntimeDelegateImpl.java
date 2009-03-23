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
import org.exoplatform.services.rest.FilterDescriptor;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.PerRequestObjectFactory;
import org.exoplatform.services.rest.ProviderResolver;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.SingletonObjectFactory;
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
import org.exoplatform.services.rest.impl.provider.ProviderDescriptorImpl;
import org.exoplatform.services.rest.impl.resource.ResourceDescriptorValidator;
import org.exoplatform.services.rest.impl.uri.UriBuilderImpl;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.provider.EntityProvider;
import org.exoplatform.services.rest.provider.ProviderDescriptor;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;
import org.exoplatform.services.rest.util.MediaTypeMultivaluedMap;
import org.exoplatform.services.rest.util.RawTypeUtil;
import org.exoplatform.services.rest.util.UriPatternMap;
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

  /**
   * @return RuntimeDelegateImpl instance if it was already preset
   * @see RuntimeDelegate#setInstance(RuntimeDelegate)
   */
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

  //

  /**
   * Read message body providers. Also see {@link MediaTypeMultivaluedMap}.
   */
  private final MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>>         writeProviders   = new MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>>();

  /**
   * Read message body providers. Also see {@link MediaTypeMultivaluedMap}.
   */
  private final MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>>         readProviders    = new MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>>();

  /**
   * Exception mappers, see {@link ExceptionMapper}.
   */
  private final Map<Class<? extends Throwable>, ObjectFactory<ProviderDescriptor>> exceptionMappers = new HashMap<Class<? extends Throwable>, ObjectFactory<ProviderDescriptor>>();

  /**
   * Context resolvers.
   */
  private final Map<Class<?>, MediaTypeMap<ObjectFactory<ProviderDescriptor>>>     contextResolvers = new HashMap<Class<?>, MediaTypeMap<ObjectFactory<ProviderDescriptor>>>();

  /**
   * Request filters, see {@link RequestFilter}.
   */
  private final UriPatternMap<ObjectFactory<FilterDescriptor>>                     requestFilters   = new UriPatternMap<ObjectFactory<FilterDescriptor>>();

  /**
   * Response filters, see {@link ResponseFilter}.
   */
  private final UriPatternMap<ObjectFactory<FilterDescriptor>>                     responseFilters  = new UriPatternMap<ObjectFactory<FilterDescriptor>>();

  /**
   * Method invoking filters.
   */
  private final UriPatternMap<ObjectFactory<FilterDescriptor>>                     invokerFilters   = new UriPatternMap<ObjectFactory<FilterDescriptor>>();

  /**
   * Validator.
   */
  private final ResourceDescriptorVisitor                                          rdv              = ResourceDescriptorValidator.getInstance();

  /**
   * Should be used only once for initialize.
   * 
   * @see RuntimeDelegate#setInstance(RuntimeDelegate)
   * @see RuntimeDelegate#getInstance()
   */
  public RuntimeDelegateImpl() {
  }

  // RuntimeDelegate

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

  // ProviderResolver

  /**
   * {@inheritDoc}
   */
  public <T> void addContextResolver(Class<? extends ContextResolver<T>> ctxResolverClass) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(ctxResolverClass);
      descriptor.accept(rdv);
      // Per-request provider
      ObjectFactory<ProviderDescriptor> f = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
      addContextResolver(f);
    } catch (Exception e) {
      LOG.error("Failed add ContextResolver " + ctxResolverClass.getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addContextResolverInstance(ContextResolver<T> ctxResolver) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(ctxResolver);
      descriptor.accept(rdv);
      // Singleton provider
      ObjectFactory<ProviderDescriptor> f = new SingletonObjectFactory<ProviderDescriptor>(descriptor,
                                                                                           ctxResolver);
      addContextResolver(f);
    } catch (Exception e) {
      LOG.error("Failed add ContextResolver " + ctxResolver.getClass().getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addEntityProvider(Class<? extends EntityProvider<T>> entProviderClass) {
    try {
      addMessageBodyReader(entProviderClass);
      addMessageBodyWriter(entProviderClass);
    } catch (Exception e) {
      LOG.error("Failed add EntityProvider " + entProviderClass.getName());
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addEntityProviderInstance(EntityProvider<T> entProvider) {
    try {
      addMessageBodyReaderInstance(entProvider);
      addMessageBodyWriterInstance(entProvider);
    } catch (Exception e) {
      LOG.error("Failed add EntityProvider " + entProvider.getClass().getName());
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T extends Throwable> void addExceptionMapper(Class<? extends ExceptionMapper<T>> excMapperClass) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(excMapperClass);
      descriptor.accept(rdv);
      // Per-request provider
      ObjectFactory<ProviderDescriptor> f = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
      addExceptionMapper(f);
    } catch (Exception e) {
      LOG.error("Failed add ExceptionMapper " + excMapperClass.getClass().getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void addExceptionMapperInstance(ExceptionMapper excMapper) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(excMapper);
      descriptor.accept(rdv);
      // Singleton provider
      ObjectFactory<ProviderDescriptor> f = new SingletonObjectFactory<ProviderDescriptor>(descriptor,
                                                                                           excMapper);
      addExceptionMapper(f);
    } catch (Exception e) {
      LOG.error("Failed add ExceptionMapper " + excMapper.getClass().getName(), e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addMessageBodyReader(Class<? extends MessageBodyReader<T>> readerClass) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(readerClass);
      descriptor.accept(rdv);
      // Per-request provider
      ObjectFactory<ProviderDescriptor> f = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
      // MessageBodyReader is smart component and can determine which type it
      // supports, see method MessageBodyReader.isReadable. So here does not
      // check is reader for the same Java and media type already exists.
      // Let it be under developer's control.
      for (MediaType mime : f.getObjectModel().consumes())
        readProviders.getList(mime).add(f);
    } catch (Exception e) {
      LOG.error("Failed add MessageBodyReader ", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addMessageBodyReaderInstance(MessageBodyReader<T> reader) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(reader);
      descriptor.accept(rdv);
      // Singleton provider
      ObjectFactory<ProviderDescriptor> f = new SingletonObjectFactory<ProviderDescriptor>(descriptor,
                                                                                           reader);
      // MessageBodyReader is smart component and can determine which type it
      // supports, see method MessageBodyReader.isReadable. So here does not
      // check is reader for the same Java and media type already exists.
      // Let it be under developer's control.
      for (MediaType mime : f.getObjectModel().consumes())
        readProviders.getList(mime).add(f);
    } catch (Exception e) {
      LOG.error("Failed add MessageBodyReader ", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addMessageBodyWriter(Class<? extends MessageBodyWriter<T>> writerClass) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(writerClass);
      descriptor.accept(rdv);
      // Per-request provider
      ObjectFactory<ProviderDescriptor> f = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
      // MessageBodyWriter is smart component and can determine which type it
      // supports, see method MessageBodyWriter.isWritable. So here does not
      // check is writer for the same Java and media type already exists.
      // Let it be under developer's control.
      for (MediaType mime : f.getObjectModel().produces())
        writeProviders.getList(mime).add(f);
    } catch (Exception e) {
      LOG.error("Failed add MessageBodyWriter ", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  public <T> void addMessageBodyWriterInstance(MessageBodyWriter<T> writer) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(writer);
      descriptor.accept(rdv);
      // Singleton provider
      ObjectFactory<ProviderDescriptor> f = new SingletonObjectFactory<ProviderDescriptor>(descriptor,
                                                                                           writer);
      // MessageBodyWriter is smart component and can determine which type it
      // supports, see method MessageBodyWriter.isWritable. So here does not
      // check is writer for the same Java and media type already exists.
      // Let it be under developer's control.
      for (MediaType mime : f.getObjectModel().produces())
        writeProviders.getList(mime).add(f);
    } catch (Exception e) {
      LOG.error("Failed add MessageBodyWriter ", e);
    }
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public List<MediaType> getAcceptableWriterMediaTypes(Class<?> type,
                                                       Type genericType,
                                                       Annotation[] annotations) {
    List<MediaType> l = new ArrayList<MediaType>();
    for (Map.Entry<MediaType, List<ObjectFactory<ProviderDescriptor>>> e : writeProviders.entrySet()) {
      MediaType mime = e.getKey();
      for (ObjectFactory pf : e.getValue()) {
        MessageBodyWriter writer = (MessageBodyWriter) pf.getInstance(ApplicationContextImpl.getCurrent());
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
    MediaTypeMap<ObjectFactory<ProviderDescriptor>> pm = contextResolvers.get(contextType);
    ContextResolver<T> resolver = null;
    if (pm != null) {
      if (mediaType == null)
        return _getContextResolver(pm, contextType, MediaTypeHelper.DEFAULT_TYPE);

      resolver = _getContextResolver(pm, contextType, mediaType);
      if (resolver == null)
        resolver = _getContextResolver(pm,
                                       contextType,
                                       new MediaType(mediaType.getType(), MediaType.MEDIA_TYPE_WILDCARD));
      if (resolver == null)
        resolver = _getContextResolver(pm, contextType, MediaTypeHelper.DEFAULT_TYPE);
    }
    return resolver;
  }

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public <T extends Throwable> ExceptionMapper<T> getExceptionMapper(Class<T> type) {
    ObjectFactory pf = exceptionMappers.get(type);
    if (pf != null)
      return (ExceptionMapper<T>) pf.getInstance(ApplicationContextImpl.getCurrent());
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
      return _getMessageBodyReader(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);

    MessageBodyReader<T> reader = _getMessageBodyReader(type, genericType, annotations, mediaType);
    if (reader == null)
      reader = _getMessageBodyReader(type,
                                     genericType,
                                     annotations,
                                     new MediaType(mediaType.getType(), MediaType.MEDIA_TYPE_WILDCARD));
    if (reader == null)
      reader = _getMessageBodyReader(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);

    return reader;
  }

  /**
   * {@inheritDoc}
   */
  public <T> MessageBodyWriter<T> getMessageBodyWriter(Class<T> type,
                                                       Type genericType,
                                                       Annotation[] annotations,
                                                       MediaType mediaType) {
    if (mediaType == null)
      return _getMessageBodyWriter(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);

    MessageBodyWriter<T> writer = _getMessageBodyWriter(type, genericType, annotations, mediaType);
    if (writer == null)
      writer = _getMessageBodyWriter(type,
                                     genericType,
                                     annotations,
                                     new MediaType(mediaType.getType(), MediaType.MEDIA_TYPE_WILDCARD));
    if (writer == null)
      writer = _getMessageBodyWriter(type, genericType, annotations, MediaTypeHelper.DEFAULT_TYPE);
    return writer;
  }

  /**
   * @param providerClass should be one of known provider classes
   */
  void addProvider(Class<?> providerClass) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(providerClass);
      descriptor.accept(rdv);
      ObjectFactory<ProviderDescriptor> f = new PerRequestObjectFactory<ProviderDescriptor>(descriptor);
      addProvider(f);
    } catch (Exception e) {
      LOG.error("Failed add Provider " + providerClass.getName(), e);
    }
  }

  /**
   * @param provider should be one of known provider instances
   */
  void addProviderInstance(Object provider) {
    try {
      ProviderDescriptor descriptor = new ProviderDescriptorImpl(provider.getClass());
      rdv.visitProviderDescriptor(descriptor);

      ObjectFactory<ProviderDescriptor> f = new SingletonObjectFactory<ProviderDescriptor>(descriptor,
                                                                                           provider);
      addProvider(f);
    } catch (Exception e) {
      LOG.error("Failed add Provider " + provider.getClass().getName(), e);
    }
  }

  //

  /**
   * Add {@link ProviderFactory}.
   * 
   * @see #addContextResolver(Class)
   * @see #addContextResolverInstance(ContextResolver)
   * @see #addEntityProvider(Class)
   * @see #addEntityProviderInstance(EntityProvider)
   * @see #addExceptionMapper(Class)
   * @see #addExceptionMapperInstance(ExceptionMapper)
   * @see #addMessageBodyReader(Class)
   * @see #addMessageBodyReaderInstance(MessageBodyReader)
   * @see #addMessageBodyWriter(Class)
   * @see #addMessageBodyWriterInstance(MessageBodyWriter)
   * @param factory ObjectFactory
   */
  void addProvider(ObjectFactory<ProviderDescriptor> factory) {
    boolean isKnownProviderType = false;
    try {
      Class<?> clazz = factory.getObjectModel().getObjectClass();
      if (MessageBodyReader.class.isAssignableFrom(clazz)) {
        isKnownProviderType = true;
        // MessageBodyReader is smart component and can determine which type it
        // supports, see method MessageBodyReader.isReadable. So here does not
        // check is reader for the same Java and media type already exists.
        // Let it be under developer's control.
        for (MediaType mime : factory.getObjectModel().consumes())
          readProviders.getList(mime).add(factory);
      }
      if (MessageBodyWriter.class.isAssignableFrom(clazz)) {
        isKnownProviderType = true;
        // MessageBodyWriter is smart component and can determine which type it
        // supports, see method MessageBodyWriter.isWritable. So here does not
        // check is writer for the same Java and media type already exists.
        // Let it be under developer's control.
        for (MediaType mime : factory.getObjectModel().produces())
          writeProviders.getList(mime).add(factory);
      }
      if (ExceptionMapper.class.isAssignableFrom(clazz)) {
        isKnownProviderType = true;
        addExceptionMapper(factory);
      }
      if (ContextResolver.class.isAssignableFrom(clazz)) {
        isKnownProviderType = true;
        addContextResolver(factory);
      }
    } catch (Exception e) {
      LOG.error("Failed add Provider " + factory.getClass().getName());
    }
    if (!isKnownProviderType)
      LOG.warn("Unsupported provider type: " + factory.getObjectModel().getObjectClass().getName());
  }

  //

  /**
   * @return all available ExceptionMappers
   */
  public Map<Class<? extends Throwable>, ObjectFactory<ProviderDescriptor>> getExceptionMappers() {
    return exceptionMappers;
  }

  /**
   * @return all available ContextResolvers
   */
  public Map<Class<?>, MediaTypeMap<ObjectFactory<ProviderDescriptor>>> getContextResolvers() {
    return contextResolvers;
  }

  /**
   * @return all available MessageBodyReaders
   */
  public MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>> getMessageBodyReaders() {
    return readProviders;
  }

  /**
   * @return all available MessageBodyWriters
   */
  public MediaTypeMultivaluedMap<ObjectFactory<ProviderDescriptor>> getMessageBodyWriters() {
    return writeProviders;
  }

  // ----- Filters

  /**
   * @param filterClass see {@link MethodInvokerFilter}
   */
  public void addMethodInvokerFilter(Class<? extends MethodInvokerFilter> filterClass) {
    try {
      FilterDescriptor descriptor = new FilterDescriptorImpl(filterClass);
      descriptor.accept(rdv);
      ObjectFactory<FilterDescriptor> factory = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
      invokerFilters.getList(descriptor.getUriPattern()).add(factory);
    } catch (Exception e) {
      LOG.error("Failed add MethodInvokerFilter " + filterClass.getName(), e);
    }
  }

  /**
   * @param filter see {@link MethodInvokerFilter}
   */
  public void addMethodInvokerFilterInstance(MethodInvokerFilter filter) {
    try {
      FilterDescriptor descriptor = new FilterDescriptorImpl(filter);
      descriptor.accept(rdv);
      ObjectFactory<FilterDescriptor> factory = new SingletonObjectFactory<FilterDescriptor>(descriptor,
                                                                                             filter);
      invokerFilters.getList(descriptor.getUriPattern()).add(factory);
    } catch (Exception e) {
      LOG.error("Failed add RequestFilter " + filter.getClass().getName(), e);
    }
  }

  /**
   * @param filterClass See {@link RequestFilter}
   */
  public void addRequestFilter(Class<? extends RequestFilter> filterClass) {
    try {
      FilterDescriptor descriptor = new FilterDescriptorImpl(filterClass);
      descriptor.accept(rdv);
      ObjectFactory<FilterDescriptor> factory = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
      requestFilters.getList(descriptor.getUriPattern()).add(factory);
    } catch (Exception e) {
      LOG.error("Failed add MethodInvokerFilter " + filterClass.getName(), e);
    }
  }

  /**
   * @param filter See {@link RequestFilter}
   */
  public void addRequestFilterInstance(RequestFilter filter) {
    try {
      FilterDescriptor descriptor = new FilterDescriptorImpl(filter);
      descriptor.accept(rdv);
      ObjectFactory<FilterDescriptor> factory = new SingletonObjectFactory<FilterDescriptor>(descriptor,
                                                                                             filter);
      requestFilters.getList(descriptor.getUriPattern()).add(factory);
    } catch (Exception e) {
      LOG.error("Failed add RequestFilter " + filter.getClass().getName(), e);
    }
  }

  /**
   * @param filterClass See {@link ResponseFilter}
   */
  public void addResponseFilter(Class<? extends ResponseFilter> filterClass) {
    try {
      FilterDescriptor descriptor = new FilterDescriptorImpl(filterClass);
      descriptor.accept(rdv);
      ObjectFactory<FilterDescriptor> of = new PerRequestObjectFactory<FilterDescriptor>(descriptor);
      responseFilters.getList(descriptor.getUriPattern()).add(of);
    } catch (Exception e) {
      LOG.error("Failed add ResponseFilter " + filterClass.getName(), e);
    }
  }

  /**
   * @param filter See {@link ResponseFilter}
   */
  public void addResponseFilterInstance(ResponseFilter filter) {
    try {
      FilterDescriptor descriptor = new FilterDescriptorImpl(filter);
      descriptor.accept(rdv);
      ObjectFactory<FilterDescriptor> of = new SingletonObjectFactory<FilterDescriptor>(descriptor,
                                                                                        filter);
      responseFilters.getList(descriptor.getUriPattern()).add(of);
    } catch (Exception e) {
      LOG.error("Failed add ResponseFilter " + filter.getClass().getName(), e);
    }
  }

  /**
   * @param filterClass one of know filter classes
   * @see #addFilterFactory(FilterFactory)
   */
  @SuppressWarnings("unchecked")
  void addFilter(Class<?> filterClass) {
    boolean isKnownFilterType = false;
    try {
      if (RequestFilter.class.isAssignableFrom(filterClass)) {
        isKnownFilterType = true;
        addRequestFilter((Class<? extends RequestFilter>) filterClass);
      }
      if (ResponseFilter.class.isAssignableFrom(filterClass)) {
        isKnownFilterType = true;
        addResponseFilter((Class<? extends ResponseFilter>) filterClass);

      }
      if (MethodInvokerFilter.class.isAssignableFrom(filterClass)) {
        isKnownFilterType = true;
        addMethodInvokerFilter((Class<? extends MethodInvokerFilter>) filterClass);
      }
    } catch (Exception e) {
      LOG.error("Unable add filter: " + filterClass.getName(), e);
    }
    if (!isKnownFilterType) {
      LOG.warn("Unsupported filter type: " + filterClass.getName());
    }
  }

  /**
   * @param filter one of known filter instances
   * @see #addFilterFactory(FilterFactory)
   */
  void addFilterInstance(Object filter) {
    boolean isKnownFilterType = false;
    try {
      if (RequestFilter.class.isAssignableFrom(filter.getClass())) {
        isKnownFilterType = true;
        addRequestFilterInstance((RequestFilter) filter);
      }
      if (ResponseFilter.class.isAssignableFrom(filter.getClass())) {
        isKnownFilterType = true;
        addResponseFilterInstance((ResponseFilter) filter);

      }
      if (MethodInvokerFilter.class.isAssignableFrom(filter.getClass())) {
        isKnownFilterType = true;
        addMethodInvokerFilterInstance((MethodInvokerFilter) filter);
      }
    } catch (Exception e) {
      LOG.error("Unable add filter: " + filter.getClass().getName(), e);
    }
    if (!isKnownFilterType) {
      LOG.warn("Unsupported filter type: " + filter.getClass().getName());
    }
  }

  /**
   * @return method invocation filters
   */
  public UriPatternMap<ObjectFactory<FilterDescriptor>> getMethodInvokerFilters() {
    return invokerFilters;
  }

  /**
   * @return request filters
   */
  public UriPatternMap<ObjectFactory<FilterDescriptor>> getRequestFilters() {
    return requestFilters;
  }

  /**
   * @return response filters
   */
  public UriPatternMap<ObjectFactory<FilterDescriptor>> getResponseFilters() {
    return responseFilters;
  }

  // ----------------------------------

  /**
   * @param <T> context resolver actual type argument
   * @param pm MediaTypeMap that contains ProviderFactories that may produce
   *          objects that are instance of T
   * @param contextType context type
   * @param mediaType media type that can be used to restrict context resolver
   *          choose
   * @return ContextResolver or null if nothing was found
   */
  @SuppressWarnings("unchecked")
  private <T> ContextResolver<T> _getContextResolver(MediaTypeMap<ObjectFactory<ProviderDescriptor>> pm,
                                                     Class<T> contextType,
                                                     MediaType mediaType) {
    for (Map.Entry<MediaType, ObjectFactory<ProviderDescriptor>> e : pm.entrySet()) {
      if (mediaType.isCompatible(e.getKey())) {
        return (ContextResolver<T>) e.getValue().getInstance(ApplicationContextImpl.getCurrent());
      }
    }
    return null;
  }

  /**
   * Looking for message body reader according to supplied entity class, entity
   * generic type, annotations and content type.
   * 
   * @param <T> message body reader actual type argument
   * @param type entity type
   * @param genericType entity generic type
   * @param annotations annotations
   * @param mediaType entity content type
   * @return message body reader or null if no one was found.
   */
  @SuppressWarnings("unchecked")
  private <T> MessageBodyReader<T> _getMessageBodyReader(Class<T> type,
                                                         Type genericType,
                                                         Annotation[] annotations,
                                                         MediaType mediaType) {
    for (ObjectFactory pf : readProviders.getList(mediaType)) {
      MessageBodyReader reader = (MessageBodyReader) pf.getInstance(ApplicationContextImpl.getCurrent());
      if (reader.isReadable(type, genericType, annotations, mediaType))
        return reader;
    }
    return null;
  }

  /**
   * Looking for message body writer according to supplied entity class, entity
   * generic type, annotations and content type.
   * 
   * @param <T> message body writer actual type argument
   * @param type entity type
   * @param genericType entity generic type
   * @param annotations annotations
   * @param mediaType content type in which entity should be represented
   * @return message body writer or null if no one was found.
   */
  @SuppressWarnings("unchecked")
  private <T> MessageBodyWriter<T> _getMessageBodyWriter(Class<T> type,
                                                         Type genericType,
                                                         Annotation[] annotations,
                                                         MediaType mediaType) {
    for (ObjectFactory pf : writeProviders.getList(mediaType)) {
      MessageBodyWriter writer = (MessageBodyWriter) pf.getInstance(ApplicationContextImpl.getCurrent());
      if (writer.isWriteable(type, genericType, annotations, mediaType))
        return writer;
    }
    return null;
  }

  private void addContextResolver(ObjectFactory<ProviderDescriptor> factory) {
    Class<?> clazz = factory.getObjectModel().getObjectClass();
    for (Type type : clazz.getGenericInterfaces()) {
      if (type instanceof ParameterizedType) {
        if (ContextResolver.class == RawTypeUtil.getRawType(type)) {
          Type[] atypes = RawTypeUtil.getActualTypes(type);
          if (atypes.length > 1)
            throw new RuntimeException("Unable strong determine actual type argument, more then one type found.");
          Class<?> aclazz = (Class<?>) atypes[0];
          MediaTypeMap<ObjectFactory<ProviderDescriptor>> pm = contextResolvers.get(aclazz);
          if (pm == null) {
            pm = new MediaTypeMap<ObjectFactory<ProviderDescriptor>>();
            contextResolvers.put(aclazz, pm);
          }
          for (MediaType mime : factory.getObjectModel().produces()) {
            if (pm.get(mime) != null) {
              String msg = "Failed add ContextResolver " + clazz.getName()
                  + ". ContextResolver for " + aclazz.getName() + " and media type " + mime
                  + " already registered.";
              throw new RuntimeException(msg);
            } else {
              pm.put(mime, factory);
            }
          }
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  private void addExceptionMapper(ObjectFactory<ProviderDescriptor> factory) {
    Class<?> clazz = factory.getObjectModel().getObjectClass();
    for (Type type : clazz.getGenericInterfaces()) {
      if (type instanceof ParameterizedType) {
        if (ExceptionMapper.class == RawTypeUtil.getRawType(type)) {
          Type[] atypes = RawTypeUtil.getActualTypes(type);
          if (atypes.length > 1)
            throw new RuntimeException("Unable strong determine actual type argument, more then one type found.");
          Class<? extends Throwable> exc = (Class<? extends Throwable>) atypes[0];
          exceptionMappers.put(exc, factory);
        }
      }
    }
  }

}
