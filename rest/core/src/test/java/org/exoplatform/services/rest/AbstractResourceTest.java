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

import java.io.ByteArrayInputStream;
import java.net.URI;

import javax.ws.rs.core.MultivaluedMap;

import org.exoplatform.services.rest.impl.ContainerRequest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.InputHeadersMap;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.RequestHandlerImpl;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.tools.DummyContainerResponseWriter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class AbstractResourceTest extends BaseTest {

  private ResourceBinder     binder;

  private RequestHandlerImpl requestHandler;

  public void setUp() throws Exception {
    super.setUp();
    binder = (ResourceBinder) container.getComponentInstanceOfType(ResourceBinder.class);
    requestHandler = (RequestHandlerImpl) container.getComponentInstanceOfType(RequestHandlerImpl.class);
    binder.clear();
  }

  public void registry(Object resource) throws Exception {
    container.registerComponentInstance(resource);
    binder.bind(resource);

  }

  public ContainerResponse service(String method,
                                   String requestURI,
                                   String baseURI,
                                   MultivaluedMap<String, String> headers,
                                   byte[] data,
                                   ContainerResponseWriter writer) throws Exception {

    if (headers == null)
      headers = new MultivaluedMapImpl();

    ByteArrayInputStream in = null;
    if (data != null)
      in = new ByteArrayInputStream(data);

    ContainerRequest request = new ContainerRequest(method,
                                                    new URI(requestURI),
                                                    new URI(baseURI),
                                                    in,
                                                    new InputHeadersMap(headers));
    ContainerResponse response = new ContainerResponse(writer);
    requestHandler.handleRequest(request, response);
    return response;
  }

  public ContainerResponse service(String method,
                                   String requestURI,
                                   String baseURI,
                                   MultivaluedMap<String, String> headers,
                                   byte[] data) throws Exception {
    return service(method, requestURI, baseURI, headers, data, new DummyContainerResponseWriter());

  }

  public void unregistry(Object resource) {
    binder.unbind(resource.getClass());
    container.unregisterComponentByInstance(resource);
  }

}
