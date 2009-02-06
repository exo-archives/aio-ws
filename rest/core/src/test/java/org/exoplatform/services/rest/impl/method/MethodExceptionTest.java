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

import org.exoplatform.services.rest.AbstractResourceTest;

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

    assertEquals(500, service("GET", "/a/2", "", null, null).getStatus());

    unregistry(resource);
  }

}
