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

package org.exoplatform.services.rest.impl.method;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;

import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.MessageBodyReader;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.impl.ApplicationException;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.method.MethodInvoker;
import org.exoplatform.services.rest.resource.GenericMethodResource;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class DefaultMethodInvoker implements MethodInvoker {

  private static final Log LOG = ExoLogger.getLogger(DefaultMethodInvoker.class.getName());

  /**
   * {@inheritDoc}
   */
  @SuppressWarnings("unchecked")
  public Object invokeMethod(Object resource,
                             GenericMethodResource methodResource,
                             ApplicationContext context) {

//TODO    for (MethodInvokerFilter f : context.getRequestHandler().getInvokerFilters())
//      f.accept(methodResource);

    Object[] p = new Object[methodResource.getMethodParameters().size()];
    int i = 0;
    for (org.exoplatform.services.rest.method.MethodParameter mp : methodResource.getMethodParameters()) {
      Annotation a = mp.getAnnotation();
      if (a != null) {
        ParameterResolver<?> pr = ParameterResolverFactory.createParameterResolver(a);
        try {
          p[i++] = pr.resolve(mp, context);
        } catch (Exception e) {
          
          if (LOG.isDebugEnabled())
            e.printStackTrace();
          
          Class<?> ac = a.annotationType();
          if (ac == MatrixParam.class || ac == QueryParam.class || ac == PathParam.class)
            throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());

          throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
          
        }
        
      } else {

        InputStream entityStream = context.getContainerRequest().getEntityStream();
        if (entityStream == null)
          p[i++] = null;
        else {
          MediaType contentType = context.getContainerRequest().getMediaType();
          MultivaluedMap<String, String> headers = context.getContainerRequest()
                                                          .getRequestHeaders();

          MessageBodyReader entityReader = RuntimeDelegateImpl.getInstance()
                                                              .getMessageBodyReader(mp.getParameterClass(),
                                                                                    mp.getGenericType(),
                                                                                    mp.getAnnotations(),
                                                                                    contentType);
          if (entityReader == null) {
            if (LOG.isDebugEnabled())
              LOG.warn("Unsupported media type. ");
            
            throw new WebApplicationException(Response.status(Response.Status.UNSUPPORTED_MEDIA_TYPE)
                                                      .build());
          }

          try {
            
            p[i++] = entityReader.readFrom(mp.getParameterClass(),
                                           mp.getGenericType(),
                                           mp.getAnnotations(),
                                           contentType,
                                           headers,
                                           entityStream);
          } catch (IOException e) {
            
            if (LOG.isDebugEnabled())
              e.printStackTrace();
            
            throw new WebApplicationException(e);
            
          }
        }
      }

    }
    try {
      return methodResource.getMethod().invoke(resource, p);
    } catch (IllegalArgumentException argExc) {
      // should not be thrown
      throw new ApplicationException(argExc.getCause());
    } catch (IllegalAccessException accessExc) {
      // should not be thrown
      throw new ApplicationException(accessExc.getCause());
    } catch (InvocationTargetException invExc) {
      if (LOG.isDebugEnabled())
        invExc.printStackTrace();
      // get cause of exception that method produces
      Throwable cause = invExc.getCause();
      // if WebApplicationException than it may contain response
      if (WebApplicationException.class == cause.getClass())
        throw (WebApplicationException) invExc.getCause();

      throw new ApplicationException(cause);
    }
  }

}
