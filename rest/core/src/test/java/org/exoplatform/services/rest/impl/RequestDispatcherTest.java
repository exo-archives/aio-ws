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

import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.GenericContainerResponse;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RequestDispatcherTest extends AbstractResourceTest {

  @Path("/a")
  public static class Resource1 {
    @POST
    public String m0() {
      return "m0";
    }
    
    @POST
    @Path("/b")
    public String m1() {
      return "m1";
    }
    
    @Path("b/c")
    public SubResource1 m2() {
      return new SubResource1();
    }
  }
  
  public static class SubResource1 {
    @POST
    public String m0() {
      return "m2.0";
    }
    @POST
    @Path("d")
    public String m1() {
      return "m2.1";
    }
    @Path("d/e")
    public SubResource2 m2() {
      return new SubResource2();
    }
  }
  
  public static class SubResource2 {
    @POST
    public String m0() {
      return "m3.0";
    }
    @POST
    @Path("f")
    public String m1() {
      return "m3.1";
    }
  }
  
  
  public void testResource1() throws Exception {
    Resource1 r1 = new Resource1();
    registry(r1);
    assertEquals("m0", service("POST", "/a", "", null, null).getEntity());
    assertEquals("m1", service("POST", "/a/b", "", null, null).getEntity());
    assertEquals("m2.0", service("POST", "/a/b/c", "", null, null).getEntity());
    assertEquals("m2.1", service("POST", "/a/b/c/d", "", null, null).getEntity());
    assertEquals("m3.0", service("POST", "/a/b/c/d/e", "", null, null).getEntity());
    assertEquals("m3.1", service("POST", "/a/b/c/d/e/f", "", null, null).getEntity());
    unregistry(r1);
  }
  
  @Path("/")
  public static class Resource2 {
    @POST
    public String m0() {
      return "m0";
    }

    @POST
    @Path("a")
    public String m1() {
      return "m1";
    }

    @POST
    @Path("1/a/b /c/{d}")
    public String m2(@PathParam("d")String d) {
      return d;
    }

    @POST
    @Path("2/a/b /c/{d}")
    public String m3(@Encoded @PathParam("d")String d) {
      return d;
    }

  }

  public void testResource2() throws Exception {
    Resource2 r2 = new Resource2();
    registry(r2);
    assertEquals("m0", service("POST", "/", "", null, null).getEntity());
    assertEquals("m1", service("POST", "/a", "", null, null).getEntity());
    assertEquals("#x y", service("POST", "/1/a/b%20/c/%23x%20y", "", null, null).getEntity());
    assertEquals("%23x%20y", service("POST", "/2/a/b%20/c/%23x%20y", "", null, null).getEntity());
    unregistry(r2);
  }
  
  @Path("/a/b/{c}/{d}")
  public static class Resource3 {
    
    @Context
    private UriInfo uriInfo;
    private String c;
    private String d;
    
    public Resource3(@PathParam("c") String c) {
      this.c = c;
    }

    public Resource3(@PathParam("c") String c, @PathParam("d") String d) {
      this.c = c;
      this.d = d;
    }
    
    @GET
    @Path("m0")
    public String m0() {
      return uriInfo.getRequestUri().toString();
    }

    @GET
    @Path("m1")
    public String m1() {
      return c;
    }

    @GET
    @Path("m2")
    public String m2() {
      return d;
    }
  }

  public void testResourceConstructorAndFields() throws Exception {
    registry(Resource3.class);
    assertEquals("/a/b/c/d/m0", service("GET", "/a/b/c/d/m0", "", null, null).getEntity());
    assertEquals("c", service("GET", "/a/b/c/d/m1", "", null, null).getEntity());
    assertEquals("d", service("GET", "/a/b/c/d/m2", "", null, null).getEntity());
    unregistry(Resource3.class);
  }
  
  public static class Failure {
    // not member of exo-container
  }

  @Path("/_a/b/{c}/{d}")
  public static class ResourceFail {
    
    public ResourceFail(Failure failure, @PathParam("c") String c, @PathParam("d") String d) {
    }
    
    @GET
    @Path("m0")
    public String m0() {
      return "m0";
    }
  }  
  
  public void testResourceConstructorFail() throws Exception {
    registry(ResourceFail.class);
    GenericContainerResponse resp = service("GET", "/_a/b/c/d/m0", "", null, null);
    String entity = (String) resp.getEntity();
    assertTrue(entity.startsWith("Can't instantiate resource "));
    assertEquals(javax.ws.rs.core.Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), resp.getStatus());
    unregistry(ResourceFail.class);
  }
  
}
