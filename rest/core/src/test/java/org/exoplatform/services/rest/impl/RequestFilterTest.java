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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.Filter;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.RequestFilter;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.ResourceBinder;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RequestFilterTest extends AbstractResourceTest {

  @Filter
  public static class RequestFilter1 implements RequestFilter {

    @Context
    private UriInfo            uriInfo;

    @Context
    private HttpHeaders        httpHeaders;

    private Providers          providers;

    private HttpServletRequest httpRequest;

    private ResourceBinder     binder; // exo container component

    public RequestFilter1(@Context Providers providers,
                          @Context HttpServletRequest httpRequest,
                          ResourceBinder binder) {
      this.providers = providers;
      this.httpRequest = httpRequest;
      this.binder = binder;
    }

    public void doFilter(GenericContainerRequest request) {
      if (uriInfo != null && httpHeaders != null && providers != null && httpRequest != null
          && binder != null)
        request.setMethod("POST");
    }

  }

  @Path("a/b/c/{x:.*}")
  @Filter
  public static class RequestFilter2 implements RequestFilter {

    public void doFilter(GenericContainerRequest request) {
      request.setMethod("DELETE");
    }

  }
  
  @Path("a")
  public static class Resource1 {

    @POST
    public void m0() {
    }
    
    @DELETE
    @Path("b/c/d/e")
    public void m1() {
      
    }

    @PUT
    @Path("c/d/e")
    public void m2() {
      
    }
  }
  
  public void tearDown() throws Exception {
    super.tearDown();
    removeRequestFilter(RequestFilter1.class);
    removeRequestFilter(RequestFilter2.class);
  }

  public void testFilter() throws Exception {
    registry(Resource1.class);
    ContainerResponse resp = service("GET", "/a", "", null, null);
    assertEquals(405, resp.getStatus());
    assertEquals(1, resp.getHttpHeaders().get("allow").size());
    assertTrue(resp.getHttpHeaders().get("allow").get(0).toString().contains("POST"));

    // add filter that can change method
    rd.addRequestFilter(RequestFilter1.class);

    // not should get status 204
    resp = service("GET", "/a", "", null, null);
    assertEquals(204, resp.getStatus());

    unregistry(Resource1.class);
  }

  public void testFilter2() throws Exception {
    registry(Resource1.class);
    ContainerResponse resp = service("GET", "/a/b/c/d/e", "", null, null);
    assertEquals(405, resp.getStatus());
    assertEquals(1, resp.getHttpHeaders().get("allow").size());
    assertTrue(resp.getHttpHeaders().get("allow").get(0).toString().contains("DELETE"));

    // add filter that can change method
    rd.addRequestFilterInstance(new RequestFilter2());

    // not should get status 204
    resp = service("GET", "/a/b/c/d/e", "", null, null);
    assertEquals(204, resp.getStatus());

    unregistry(Resource1.class);
  }

}
