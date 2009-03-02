/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.ExceptionMapper;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.impl.RuntimeDelegateImpl;
import org.exoplatform.services.rest.impl.UnhandledException;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date: 21 Jan 2009
 * 
 * @author <a href="mailto:dmitry.kataev@exoplatform.com.ua">Dmytro Katayev</a>
 * @version $Id: TestMethodException.java
 */
public class MethodExceptionTest extends AbstractResourceTest {

  @SuppressWarnings("serial")
  public static class UncheckedException extends Exception {

    public UncheckedException() {
      super();
    }

    public UncheckedException(String msg) {
      super(msg);
    }

  }

  @Path("/a")
  public static class Resource1 {

    @GET
    @Path("/0")
    public void m0() throws WebApplicationException {
      throw new WebApplicationException();
    }

    @GET
    @Path("/1")
    public Response m1() throws WebApplicationException {
      return new WebApplicationException().getResponse();
    }

    @GET
    @Path("/2")
    public void m2() throws Exception {
      throw new UncheckedException("Unchecked exception");
    }

  }

  public void testExceptionProcessing() throws Exception {
    Resource1 resource = new Resource1();
    registry(resource);

    assertEquals(500, service("GET", "/a/0", "", null, null).getStatus());
    assertEquals(500, service("GET", "/a/1", "", null, null).getStatus());
    try {
      assertEquals(500, service("GET", "/a/2", "", null, null).getStatus());
      fail();
    } catch (UnhandledException e) {
    }
    unregistry(resource);
  }

  public static class DummyExceptionMapper implements ExceptionMapper<IllegalArgumentException> {
    public Response toResponse(IllegalArgumentException exception) {
      return Response.status(400).entity("IllegalArgumentException was thrown").build();
    }
  }

  @Path("/")
  public static class Resource2 {
    @GET
    @Path("a")
    public void m0() {
      throw new IllegalArgumentException();
    }

    @GET
    @Path("b")
    public void m1() {
      throw new WebApplicationException();
    }
  }

  public void testExceptionMapper() throws Exception {
    RuntimeDelegateImpl.getInstance().addExceptionMapperInstance(new DummyExceptionMapper());
    Resource2 resource = new Resource2();
    try {
      registry(resource);
      ContainerResponse response = service("GET", "/a", "", null, null);
      assertEquals(400 ,response.getStatus());
      assertEquals("IllegalArgumentException was thrown" ,response.getEntity());
      response = service("GET", "/b", "", null, null);
      assertEquals(500 ,response.getStatus());
    } finally {
      unregistry(resource);
    }
  }

}
