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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.MessageBodyReader;
import javax.ws.rs.ext.MessageBodyWriter;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.provider.ProviderDescriptor;
import org.exoplatform.services.rest.uri.UriPattern;
import org.exoplatform.services.rest.util.MediaTypeMap;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class BaseTest extends TestCase {

  protected StandaloneContainer container;
  
  protected RuntimeDelegateImpl rd;

  protected ResourceBinder     binder;

  protected RequestHandlerImpl requestHandler;

  public void setUp() throws Exception {
    StandaloneContainer.setConfigurationPath("src/test/java/conf/standalone/test-configuration.xml");
    container = StandaloneContainer.getInstance();
    rd = RuntimeDelegateImpl.getInstance();
    // **********************************************************
    // FIXME
    // NOTE Not possible to stop container in JUnit test in fact.
    // This may cause problem in test if providers in
    // RuntimeDelegateImpl are not override before each test.
    // Some providers (e.g. ExceptionMapper may remain from
    // previous test). Need to find smart solution to fix this.
    // Ideal solution is to find way for stop container and
    // restart it in each test.
    // **********************************************************
    binder = (ResourceBinder) container.getComponentInstanceOfType(ResourceBinder.class);
    requestHandler = (RequestHandlerImpl) container.getComponentInstanceOfType(RequestHandlerImpl.class);
    binder.clear();
  }

  public boolean registry(Object resource) throws Exception {
    container.registerComponentInstance(resource);
    return binder.bind(resource);
  }

  public boolean registry(Class<?> resourceClass) throws Exception {
    container.registerComponentImplementation(resourceClass.getName(), resourceClass);
    return binder.bind(resourceClass);
  }

  // Remove providers. If at runtime new providers was added they should be
  // removed after test. Only default provider should be left.
  
  @SuppressWarnings("unchecked")
  protected void removeMessageBodyReader(Class<? extends MessageBodyReader> clazz) {
    for (MediaType m : MediaTypeHelper.createConsumesList(clazz.getAnnotation(Consumes.class))) {
      List<ObjectFactory<ProviderDescriptor>> r = rd.getMessageBodyReaders().getList(m);
      for (Iterator<ObjectFactory<ProviderDescriptor>> i = r.iterator(); i.hasNext();) {
        ObjectFactory f = i.next();
        if (f.getObjectModel().getObjectClass() == clazz) {
          i.remove();
        }
      }
    }
  }

  @SuppressWarnings("unchecked")
  protected void removeMessageBodyWriter(Class<? extends MessageBodyWriter> clazz) {
    for (MediaType m : MediaTypeHelper.createProducesList(clazz.getAnnotation(Produces.class))) {
      List<ObjectFactory<ProviderDescriptor>> r = rd.getMessageBodyWriters().getList(m);
      for (Iterator<ObjectFactory<ProviderDescriptor>> i = r.iterator(); i.hasNext();) {
        ObjectFactory f = i.next();
        if (f.getObjectModel().getObjectClass() == clazz) {
          i.remove();
        }
      }
    }
  }
  
  @SuppressWarnings("unchecked")
  protected void removeExceptionMapper(Class<? extends ExceptionMapper> clazz) {
    Type t = clazz.getGenericInterfaces()[0];
    ParameterizedType pt = (ParameterizedType) t;
    rd.getExceptionMappers().remove(pt.getActualTypeArguments()[0]);
  }
  
  @SuppressWarnings("unchecked")
  protected void removeContextResolver(Class<? extends ContextResolver> clazz) {
    Type t = clazz.getGenericInterfaces()[0];
    ParameterizedType pt = (ParameterizedType) t;
    MediaTypeMap<ObjectFactory<ProviderDescriptor>> f = rd.getContextResolvers().get(pt.getActualTypeArguments()[0]);
    if (f == null)
      return;
    for (MediaType m : MediaTypeHelper.createProducesList(clazz.getAnnotation(Produces.class))) {
      f.remove(m);
    }
  }
  
  protected void removeRequestFilter(Class<? extends RequestFilter> clazz) {
    for (Map.Entry<UriPattern, List<ObjectFactory<FilterDescriptor>>> e : rd.getRequestFilters().entrySet()) {
      for (Iterator<ObjectFactory<FilterDescriptor>> i = e.getValue().iterator(); i.hasNext();) {
        ObjectFactory<FilterDescriptor> f = i.next();
        if (f.getObjectModel().getObjectClass() == clazz) {
          i.remove();
        }
      }
    }
  }

  protected void removeResponseFilter(Class<? extends ResponseFilter> clazz) {
    for (Map.Entry<UriPattern, List<ObjectFactory<FilterDescriptor>>> e : rd.getResponseFilters().entrySet()) {
      for (Iterator<ObjectFactory<FilterDescriptor>> i = e.getValue().iterator(); i.hasNext();) {
        ObjectFactory<FilterDescriptor> f = i.next();
        if (f.getObjectModel().getObjectClass() == clazz) {
          i.remove();
        }
      }
    }
  }

  protected void removeInvokerFilter(Class<? extends MethodInvokerFilter> clazz) {
    for (Map.Entry<UriPattern, List<ObjectFactory<FilterDescriptor>>> e : rd.getMethodInvokerFilters().entrySet()) {
      for (Iterator<ObjectFactory<FilterDescriptor>> i = e.getValue().iterator(); i.hasNext();) {
        ObjectFactory<FilterDescriptor> f = i.next();
        if (f.getObjectModel().getObjectClass() == clazz) {
          i.remove();
        }
      }
    }
  }

}
