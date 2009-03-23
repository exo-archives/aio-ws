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

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Providers;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.Filter;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.method.MethodInvokerFilter;
import org.exoplatform.services.rest.resource.GenericMethodResource;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MethodInvokerFilterTest extends AbstractResourceTest {

  @Filter
  public static class MethodInvokerFilter1 implements MethodInvokerFilter {

    private UriInfo            uriInfo;

    private HttpHeaders        httpHeaders;

    @Context
    private Providers          providers;

    @Context
    private HttpServletRequest httpRequest;

    private ResourceBinder     binder;     // exo container component

    public MethodInvokerFilter1(@Context UriInfo uriInfo,
                                @Context HttpHeaders httpHeaders,
                                ResourceBinder binder) {
      this.uriInfo = uriInfo;
      this.httpHeaders = httpHeaders;
      this.binder = binder;
    }

    public void accept(GenericMethodResource genericMethodResource) {
      if (uriInfo != null && httpHeaders != null && providers != null && httpRequest != null
          && binder != null) {
        if (genericMethodResource instanceof SubResourceMethodDescriptor)
          // not invoke sub-resource method
          throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
        else if (genericMethodResource instanceof ResourceMethodDescriptor)
          System.out.println("MethodInvokerFilter1: >>>>>>>>>>>> ResourceMethodDescriptor");
      } else {
        throw new WebApplicationException(Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                                                  .build());
      }
    }

  }

  @Filter
  @Path("b/c")
  public static class MethodInvokerFilter2 implements MethodInvokerFilter {

    public void accept(GenericMethodResource genericMethodResource) {
      System.out.println("MethodInvokerFilter2: >>>>>>>>>>>>");
      throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
    }

  }

  @Path("a")
  public static class Resource1 {
    @GET
    public void m0() {
    }

    @GET
    @Path("b")
    public void m1() {
    }
  }

  @Path("b")
  public static class Resource2 {
    @GET
    @Path("c")
    public void m0() {
    }

    @GET
    @Path("d")
    public void m1() {
    }
  }

  public void tearDown() throws Exception {
    super.tearDown();
    removeInvokerFilter(MethodInvokerFilter1.class);
    removeInvokerFilter(MethodInvokerFilter2.class);
  }

  public void testInvokerFilter() throws Exception {
    Resource1 r = new Resource1();
    registry(r);
    assertEquals(204, service("GET", "/a/b", "", null, null).getStatus());
    assertEquals(204, service("GET", "/a", "", null, null).getStatus());
    rd.addMethodInvokerFilter(MethodInvokerFilter1.class);
    assertEquals(400, service("GET", "/a/b", "", null, null).getStatus());
    assertEquals(204, service("GET", "/a", "", null, null).getStatus());
    unregistry(r);
  }

  public void testInvokerFilter2() throws Exception {
    Resource2 r = new Resource2();
    registry(r);
    assertEquals(204, service("GET", "/b/c", "", null, null).getStatus());
    assertEquals(204, service("GET", "/b/d", "", null, null).getStatus());
    rd.addMethodInvokerFilterInstance(new MethodInvokerFilter2());
    assertEquals(400, service("GET", "/b/c", "", null, null).getStatus());
    assertEquals(204, service("GET", "/b/d", "", null, null).getStatus());
    unregistry(r);
  }

}
