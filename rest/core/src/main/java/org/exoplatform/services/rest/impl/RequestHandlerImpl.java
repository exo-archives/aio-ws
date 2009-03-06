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

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.RuntimeDelegate;

import org.apache.commons.logging.Log;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.ResponseFilter;
import org.exoplatform.services.rest.impl.method.MethodInvokerFilterComponentPlugin;
import org.exoplatform.services.rest.impl.provider.ByteEntityProvider;
import org.exoplatform.services.rest.impl.provider.DOMSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.DataSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.EntityProviderComponentPlugin;
import org.exoplatform.services.rest.impl.provider.FileEntityProvider;
import org.exoplatform.services.rest.impl.provider.InputStreamEntityProvider;
import org.exoplatform.services.rest.impl.provider.JAXBContextResolver;
import org.exoplatform.services.rest.impl.provider.JAXBElementEntityProvider;
import org.exoplatform.services.rest.impl.provider.JAXBObjectEntityProvider;
import org.exoplatform.services.rest.impl.provider.JsonEntityProvider;
import org.exoplatform.services.rest.impl.provider.MultipartFormDataEntityProvider;
import org.exoplatform.services.rest.impl.provider.MultivaluedMapEntityProvider;
import org.exoplatform.services.rest.impl.provider.ReaderEntityProvider;
import org.exoplatform.services.rest.impl.provider.SAXSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.StreamOutputEntityProvider;
import org.exoplatform.services.rest.impl.provider.StreamSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.StringEntityProvider;
import org.exoplatform.services.rest.impl.uri.UriPattern;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.provider.EntityProvider;
import org.picocontainer.Startable;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class RequestHandlerImpl implements RequestHandler, Startable {

  /**
   * Logger.
   */
  private static final Log          LOG = ExoLogger.getLogger(RequestHandlerImpl.class.getName());

  /**
   * See {@link RequestDispatcher}.
   */
  private final RequestDispatcher   dispatcher;

  /**
   * See {@link JAXBContextResolver}.
   */
  private final JAXBContextResolver jaxbContexts;

  /**
   * Should be built-in providers be loaded.
   */
  private boolean                   loadBuiltinProviders;

  /**
   * See {@link RuntimeDelegateImpl}, {@link RuntimeDelegate}.
   */
  private RuntimeDelegateImpl       rd;

  /**
   * Constructs new instance of {@link RequestHandler}.
   * 
   * @param dispatcher See {@link RequestDispatcher}
   * @param jaxbContexts See {@link JAXBContextResolver}
   */
  public RequestHandlerImpl(RequestDispatcher dispatcher,
                            JAXBContextResolver jaxbContexts,
                            InitParams params) {

    rd = RuntimeDelegateImpl.getInstance();
    ValueParam builtinVp = null;
    if (params != null)
      builtinVp = params.getValueParam("ws.rs.entity.provider.builtin");
    loadBuiltinProviders = builtinVp == null || Boolean.valueOf(builtinVp.getValue().trim());

    this.dispatcher = dispatcher;
    this.jaxbContexts = jaxbContexts;

  }

  // RequestHandler

  /**
   * {@inheritDoc}
   */
  public void handleRequest(GenericContainerRequest request, GenericContainerResponse response) throws Exception {
    try {
      URI uri = UriBuilder.fromUri(request.getRequestUri())
                          .replaceQuery(null)
                          .fragment(null)
                          .build();
      String path = uri.getRawPath().substring(request.getBaseUri().getRawPath().length());
      List<String> t = new ArrayList<String>();

      for (Entry<UriPattern, List<RequestFilter>> e : rd.getRequestFilters().entrySet()) {
        if (e.getKey().match(path, t)) {
          for (RequestFilter f : e.getValue())
            f.doFilter(request);
        }
      }

      ApplicationContextImpl context = new ApplicationContextImpl(request, response);
      ApplicationContextImpl.setCurrent(context);
      try {
        dispatcher.dispatch(request, response);
      } catch (Exception e) {
        processError(e, response);
      }

      // NOTE error response can be processed by filter also
      for (Entry<UriPattern, List<ResponseFilter>> e : rd.getResponseFilters().entrySet()) {
        if (e.getKey().match(path, t)) {
          for (ResponseFilter f : e.getValue())
            f.doFilter(response);
        }
      }

      response.writeResponse();
    } finally {
      // reset application context
      ApplicationContextImpl.setCurrent(null);
    }
  }

  //

  /**
   * Process errors, create message about occurs errors if needs, etc.
   * 
   * @param e See exception
   * @param response See {@link GenericContainerResponse}
   */
  @SuppressWarnings("unchecked")
  private static void processError(Exception e, GenericContainerResponse response) {
    if (e instanceof WebApplicationException) {

      Response r = ((WebApplicationException) e).getResponse();
      if (r.getStatus() < 500) {
        // be silent, should be some of 4xx status
        response.setResponse(r);
      } else {
        if (LOG.isDebugEnabled())
          e.printStackTrace();

        if (r.getEntity() == null) // add stack trace as message body
          r = Response.status(r.getStatus())
                      .entity(new ErrorStreaming(e))
                      .type(MediaType.TEXT_PLAIN)
                      .build();

        response.setResponse(r);
      }
    } else if (e instanceof ApplicationException) {
      Class cause = e.getCause().getClass();
      ExceptionMapper excmap = RuntimeDelegateImpl.getInstance().getExceptionMapper(cause);
      while (cause != null && excmap == null) {
        excmap = RuntimeDelegateImpl.getInstance().getExceptionMapper(cause);
        if (excmap == null)
          cause = cause.getSuperclass();
      }
      if (excmap != null)
        response.setResponse(excmap.toResponse(e.getCause()));
      else
        throw new UnhandledException(e.getCause());
    } else {
      throw new UnhandledException(e);
    }
  }

  /**
   * For writing error message.
   */
  static class ErrorStreaming implements StreamingOutput {

    /**
     * Exception which should send to client.
     */
    private final Exception e;

    /**
     * @param e Exception for serialization
     */
    ErrorStreaming(Exception e) {
      this.e = e;
    }

    /**
     * {@inheritDoc}
     */
    public void write(OutputStream output) {
      PrintWriter pw = new PrintWriter(output);
      e.printStackTrace(pw);
      pw.flush();
    }

  }

  // Startable

  /**
   * {@inheritDoc}
   */
  public void start() {
    init();
  }

  /**
   * {@inheritDoc}
   */
  public void stop() {
  }

  //

  /**
   * Startup initialization.
   */
  protected void init() {
    if (loadBuiltinProviders) {
      // add prepared entity providers
      rd.addProviderInstance(new ByteEntityProvider());
      rd.addProviderInstance(new DataSourceEntityProvider());
      rd.addProviderInstance(new DOMSourceEntityProvider());
      rd.addProviderInstance(new FileEntityProvider());
      rd.addProviderInstance(new MultivaluedMapEntityProvider());
      rd.addProviderInstance(new MultipartFormDataEntityProvider());
      rd.addProviderInstance(new InputStreamEntityProvider());
      rd.addProviderInstance(new ReaderEntityProvider());
      rd.addProviderInstance(new SAXSourceEntityProvider());
      rd.addProviderInstance(new StreamSourceEntityProvider());
      rd.addProviderInstance(new StringEntityProvider());
      rd.addProviderInstance(new StreamOutputEntityProvider());
      rd.addProviderInstance(new JsonEntityProvider());
      JAXBElementEntityProvider jep = new JAXBElementEntityProvider();
      jep.setContexResolver(jaxbContexts);
      rd.addProviderInstance(jep);
      JAXBObjectEntityProvider jop = new JAXBObjectEntityProvider();
      jop.setContexResolver(jaxbContexts);
      rd.addProviderInstance(jop);
    }
  }

  /**
   * Processing {@link ComponentPlugin} for injection external components.
   * 
   * @param plugin See {@link ComponentPlugin}
   */
  public void addPlugin(ComponentPlugin plugin) {
    if (MethodInvokerFilterComponentPlugin.class.isAssignableFrom(plugin.getClass())) {
      // add method invoker filter
      for (MethodInvokerFilter filter : ((MethodInvokerFilterComponentPlugin) plugin).getFilters())
        rd.addMethodInvokerFilter(filter);
    } else if (EntityProviderComponentPlugin.class.isAssignableFrom(plugin.getClass())) {
      // add external entity providers
      List<EntityProvider<?>> eps = ((EntityProviderComponentPlugin) plugin).getEntityProviders();
      for (EntityProvider<?> ep : eps)
        rd.addProviderInstance(ep);
    } else if (RequestFilterComponentPlugin.class.isAssignableFrom(plugin.getClass())) {
      List<RequestFilter> filters = ((RequestFilterComponentPlugin) plugin).getFilters();
      for (RequestFilter filter : filters)
        rd.addRequestFilter(filter);
    } else if (ResponseFilterComponentPlugin.class.isAssignableFrom(plugin.getClass())) {
      List<ResponseFilter> filters = ((ResponseFilterComponentPlugin) plugin).getFilters();
      for (ResponseFilter filter : filters)
        rd.addResponseFilter(filter);
    }
  }

}
