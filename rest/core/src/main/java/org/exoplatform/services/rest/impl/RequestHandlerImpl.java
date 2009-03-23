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

import java.io.File;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import javax.ws.rs.ext.ExceptionMapper;

import org.apache.commons.logging.Log;
import org.exoplatform.container.component.ComponentPlugin;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ContainerObjectFactory;
import org.exoplatform.services.rest.FilterDescriptor;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.ObjectFactory;
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
import org.exoplatform.services.rest.impl.provider.ProviderDescriptorImpl;
import org.exoplatform.services.rest.impl.provider.ReaderEntityProvider;
import org.exoplatform.services.rest.impl.provider.SAXSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.StreamOutputEntityProvider;
import org.exoplatform.services.rest.impl.provider.StreamSourceEntityProvider;
import org.exoplatform.services.rest.impl.provider.StringEntityProvider;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.provider.EntityProvider;
import org.exoplatform.services.rest.provider.ProviderDescriptor;
import org.exoplatform.services.rest.uri.UriPattern;
import org.picocontainer.Startable;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class RequestHandlerImpl implements RequestHandler, Startable {

  /**
   * Logger.
   */
  private static final Log          LOG                   = ExoLogger.getLogger(RequestHandlerImpl.class.getName());

  /**
   * See {@link RequestDispatcher}.
   */
  private final RequestDispatcher   dispatcher;

  /**
   * See {@link RuntimeDelegateImpl}, {@link javax.ws.rs.ext.RuntimeDelegate}.
   */
  private RuntimeDelegateImpl       rd;

  /**
   * Application properties.
   */
  private final Map<String, Object> applicationProperties = new HashMap<String, Object>();

  /**
   * Constructs new instance of {@link RequestHandler}.
   * 
   * @param dispatcher See {@link RequestDispatcher}
   * @param params init parameters
   */
  @SuppressWarnings("unchecked")
  public RequestHandlerImpl(RequestDispatcher dispatcher, InitParams params) {

    // NOTE!!! RuntimeDelegate should be already initialized by ResourceBinder
    rd = RuntimeDelegateImpl.getInstance();

    if (params != null) {
      for (Iterator<ValueParam> i = params.getValueParamIterator(); i.hasNext();) {
        ValueParam vp = i.next();
        String name = vp.getName();
        String value = vp.getValue();
        if (name.equals(WS_RS_BUFFER_SIZE))
          applicationProperties.put(name, Integer.parseInt(value));
        else if (name.equals(WS_RS_USE_BUILTIN_PROVIDERS))
          applicationProperties.put(name, Boolean.valueOf(value));
        else if (name.equals(WS_RS_TMP_DIR))
          applicationProperties.put(name, new File(value));
        else
          applicationProperties.put(name, value);
      }
    }

    this.dispatcher = dispatcher;

  }

  // RequestHandler

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public void handleRequest(GenericContainerRequest request, GenericContainerResponse response) throws Exception {
    try {

      ApplicationContextImpl context = new ApplicationContextImpl(request, response);
      context.getAttributes().putAll(applicationProperties);
      ApplicationContextImpl.setCurrent(context);

      String path = context.getPath();
      List<String> capturingValues = new ArrayList<String>();

      for (Entry<UriPattern, List<ObjectFactory<FilterDescriptor>>> e : rd.getRequestFilters()
                                                                          .entrySet()) {
        UriPattern uriPattern = e.getKey();
        if (uriPattern != null) {
          if (e.getKey().match(path, capturingValues)) {
            int len = capturingValues.size();
            if (capturingValues.get(len - 1) != null && !"/".equals(capturingValues.get(len - 1)))
              continue; // not matched
          } else {
            continue; // not matched
          }

        }

        // if matched or UriPattern is null
        for (ObjectFactory<FilterDescriptor> factory : e.getValue()) {
          RequestFilter f = (RequestFilter) factory.getInstance(context);
          f.doFilter(request);
        }

      }

      try {
        dispatcher.dispatch(request, response);
      } catch (Exception e) {
        if (e instanceof WebApplicationException) {

          Response errorResponse = ((WebApplicationException) e).getResponse();
          ExceptionMapper excmap = RuntimeDelegateImpl.getInstance()
                                                      .getExceptionMapper(WebApplicationException.class);

          // should be some of 4xx status
          if (errorResponse.getStatus() < 500) {
            if (errorResponse.getEntity() == null) {
              if (excmap != null) {
                errorResponse = excmap.toResponse(e);
              }
            }
            response.setResponse(errorResponse);
          } else {
            if (LOG.isDebugEnabled())
              e.printStackTrace();

            if (errorResponse.getEntity() == null) {
              if (excmap != null) {
                errorResponse = excmap.toResponse(e);
              } else {
                // add stack trace as message body
                errorResponse = Response.status(errorResponse.getStatus())
                                        .entity(new ErrorStreaming(e))
                                        .type(MediaType.TEXT_PLAIN)
                                        .build();
              }
            }
            response.setResponse(errorResponse);
          }
        } else if (e instanceof ApplicationException) {
          Class cause = e.getCause().getClass();
          ExceptionMapper excmap = RuntimeDelegateImpl.getInstance().getExceptionMapper(cause);
          while (cause != null && excmap == null) {
            excmap = RuntimeDelegateImpl.getInstance().getExceptionMapper(cause);
            if (excmap == null)
              cause = cause.getSuperclass();
          }
          if (excmap != null) {
            response.setResponse(excmap.toResponse(e.getCause()));
          } else {
            throw new UnhandledException(e.getCause());
          }
        } else {
          throw new UnhandledException(e);
        }
      }

      for (Entry<UriPattern, List<ObjectFactory<FilterDescriptor>>> e : rd.getResponseFilters()
                                                                          .entrySet()) {
        UriPattern uriPattern = e.getKey();
        if (uriPattern != null) {
          if (e.getKey().match(path, capturingValues)) {
            int len = capturingValues.size();
            if (capturingValues.get(len - 1) != null && !"/".equals(capturingValues.get(len - 1)))
              continue; // not matched
          } else {
            continue; // not matched
          }

        }

        // if matched or UriPattern is null
        for (ObjectFactory<FilterDescriptor> factory : e.getValue()) {
          ResponseFilter f = (ResponseFilter) factory.getInstance(context);
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
    // Directory for temporary files
    final File tmpDir;
    if (applicationProperties.containsKey(WS_RS_TMP_DIR))
      tmpDir = (File) applicationProperties.get(WS_RS_TMP_DIR);
    else {
      tmpDir = new File(System.getProperty("java.io.tmpdir") + File.separator + "ws_jaxrs");
      applicationProperties.put(WS_RS_TMP_DIR, tmpDir);
    }

    if (!tmpDir.exists())
      tmpDir.mkdirs();

    // Register Shutdown Hook for cleaning temporary files.
    Runtime.getRuntime().addShutdownHook(new Thread() {
      public void run() {
        File[] files = tmpDir.listFiles();
        for (File file : files) {
          if (file.exists())
            file.delete();
        }
      }
    });

    Integer bufferSize = (Integer) applicationProperties.get(WS_RS_BUFFER_SIZE);
    if (bufferSize == null) {
      bufferSize = 204800; // TODO move somewhere as const
      applicationProperties.put(WS_RS_BUFFER_SIZE, bufferSize);
    }

    Boolean builtin = (Boolean) applicationProperties.get(WS_RS_USE_BUILTIN_PROVIDERS);
    if (builtin == null) {
      builtin = true;
      applicationProperties.put(WS_RS_USE_BUILTIN_PROVIDERS, builtin);
    }

    if (builtin) {
      // add prepared entity providers
      rd.addEntityProviderInstance(new ByteEntityProvider());
      rd.addEntityProviderInstance(new DataSourceEntityProvider());
      rd.addEntityProviderInstance(new DOMSourceEntityProvider());
      rd.addEntityProviderInstance(new FileEntityProvider());
      rd.addEntityProviderInstance(new MultivaluedMapEntityProvider());
      rd.addEntityProviderInstance(new InputStreamEntityProvider());
      rd.addEntityProviderInstance(new ReaderEntityProvider());
      rd.addEntityProviderInstance(new SAXSourceEntityProvider());
      rd.addEntityProviderInstance(new StreamSourceEntityProvider());
      rd.addEntityProviderInstance(new StringEntityProvider());
      rd.addEntityProviderInstance(new StreamOutputEntityProvider());
      rd.addEntityProviderInstance(new JsonEntityProvider());

      // per-request mode , Providers should be injected
      rd.addProvider(JAXBElementEntityProvider.class);
      rd.addProvider(JAXBObjectEntityProvider.class);

      // per-request mode , HttpServletRequest should be injected in provider
      rd.addProvider(MultipartFormDataEntityProvider.class);

      // FIXME Remove this hard code by something more smart.
      rd.addProvider(new ContainerObjectFactory<ProviderDescriptor>(new ProviderDescriptorImpl(JAXBContextResolver.class)));
    }
  }

  /**
   * Processing {@link ComponentPlugin} for injection external components.
   * 
   * @param plugin See {@link ComponentPlugin}
   */
  @SuppressWarnings("unchecked")
  public void addPlugin(ComponentPlugin plugin) {
    if (MethodInvokerFilterComponentPlugin.class.isAssignableFrom(plugin.getClass())) {
      // add method invoker filter
      for (Class<? extends MethodInvokerFilter> filter : ((MethodInvokerFilterComponentPlugin) plugin).getFilters())
        rd.addMethodInvokerFilter(filter);
    } else if (EntityProviderComponentPlugin.class.isAssignableFrom(plugin.getClass())) {
      // add external entity providers
      Set<Class<? extends EntityProvider>> eps = ((EntityProviderComponentPlugin) plugin).getEntityProviders();
      for (Class<? extends EntityProvider> ep : eps)
        rd.addProvider(ep);
    } else if (RequestFilterComponentPlugin.class.isAssignableFrom(plugin.getClass())) {
      Set<Class<? extends RequestFilter>> filters = ((RequestFilterComponentPlugin) plugin).getFilters();
      for (Class<? extends RequestFilter> filter : filters)
        rd.addRequestFilter(filter);
    } else if (ResponseFilterComponentPlugin.class.isAssignableFrom(plugin.getClass())) {
      Set<Class<? extends ResponseFilter>> filters = ((ResponseFilterComponentPlugin) plugin).getFilters();
      for (Class<? extends ResponseFilter> filter : filters)
        rd.addResponseFilter(filter);
    }
  }

}
