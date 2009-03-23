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

package org.exoplatform.services.rest.impl.resource;

import java.util.HashSet;
import java.util.Set;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;
import javax.ws.rs.ext.Provider;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.impl.ContainerResponse;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ApplicationTest extends AbstractResourceTest {

  public static class Application1 extends javax.ws.rs.core.Application {

    private final Set<Class<?>> perreq     = new HashSet<Class<?>>();

    private final Set<Object>   singletons = new HashSet<Object>();

    public Application1() {
      perreq.add(Resource1.class);
      perreq.add(Resource2.class);
      perreq.add(ExceptionMapper1.class);
      
      singletons.add(new Resource3());
      singletons.add(new Resource4());
      singletons.add(new ExceptionMapper2());
    }

    @Override
    public Set<Class<?>> getClasses() {
      return perreq;
    }

    public Set<Object> getSingletons() {
      return singletons;
    }

  }

  // will be per-request resource
  @Path("a")
  public static class Resource1 {

    @GET
    public String m0() {
      return hashCode() + "";
    }

  }

  // will be per-request resource
  @Path("b")
  public static class Resource2 {

    @GET
    public void m0() {
      throw new RuntimeException("test Runtime Exception");
    }

  }

  // will be singleton resource
  @Path("c")
  public static class Resource3 {

    @GET
    public String m0() {
      return hashCode() + "";
    }

  }

  // will be per-request resource
  @Path("d")
  public static class Resource4 {

    @GET
    public void m0() {
      throw new IllegalStateException("test Illegal State Exception");
    }

  }

  @Provider
  public static class ExceptionMapper1 implements ExceptionMapper<RuntimeException> {

    public Response toResponse(RuntimeException exception) {
      return Response.status(200).entity(exception.getMessage()).build();
    }

  }

  @Provider
  public static class ExceptionMapper2 implements ExceptionMapper<IllegalStateException> {

    public Response toResponse(IllegalStateException exception) {
      return Response.status(200).entity(exception.getMessage()).build();
    }

  }
  
  public void tearDown() throws Exception {
    super.tearDown();
    removeExceptionMapper(ExceptionMapper1.class);
    removeExceptionMapper(ExceptionMapper2.class);
  }

  public void testRegistry() {
    binder.addApplication(new Application1());
    assertEquals(4, binder.getResourceFactories().size());
    assertNotNull(rd.getExceptionMapper(RuntimeException.class));
  }

  public void testAsResources() throws Exception {
    binder.addApplication(new Application1());
    // per-request
    ContainerResponse resp = service("GET", "/a", "", null, null);
    assertEquals(200, resp.getStatus());
    String hash10 = (String) resp.getEntity();
    resp = service("GET", "/a", "", null, null);
    String hash11 = (String) resp.getEntity();
    // new instance of resource for each request
    assertFalse(hash10.equals(hash11));

    // singleton
    resp = service("GET", "/c", "", null, null);
    assertEquals(200, resp.getStatus());
    String hash20 = (String) resp.getEntity();
    resp = service("GET", "/c", "", null, null);
    String hash21 = (String) resp.getEntity();
    // singleton resource
    assertTrue(hash20.equals(hash21));
    
    // check per-request ExceptionMapper as example of provider
    resp = service("GET", "/b", "", null, null);
    // should be 200 status instead 500 if ExceptionMapper works correct
    assertEquals(200, resp.getStatus());
    assertEquals("test Runtime Exception", resp.getEntity());

    // check singleton ExceptionMapper as example of provider
    resp = service("GET", "/d", "", null, null);
    // should be 200 status instead 500 if ExceptionMapper works correct
    assertEquals(200, resp.getStatus());
    assertEquals("test Illegal State Exception", resp.getEntity());
  }

}
