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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;
import org.exoplatform.services.rest.impl.header.HeaderHelper;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ContextParametersInjectionTest extends AbstractResourceTest {

  @Path("/a/b")
  public static class Resource1 {
    
    @GET
    @Path("0")
    public String m0(@Context UriInfo uriInfo) {
      return uriInfo.getRequestUri().toString();
    }
    
    @GET
    @Path("1")
    public String m1(@Context HttpHeaders headers) {
      List<String> l = headers.getRequestHeader("Accept");
      return HeaderHelper.convertToString(l);
    }

    @GET
    @Path("2")
    public String m2(@Context Request request) {
      return request.getMethod();
    }

    @GET
    @Path("3")
    public String m3(@Context SecurityContext securityContext) {
      return "securityContext";
    }
  }
  
  public void testContextInjection() throws Exception {
    Resource1 r1 = new Resource1();
    registry(r1);
    assertEquals("http://localhost/test/a/b/0", service("GET",
                                                        "http://localhost/test/a/b/0",
                                                        "http://localhost/test",
                                                        null,
                                                        null).getEntity());
    MultivaluedMap<String, String> h = new MultivaluedMapImpl();
    h.add("Accept", "text/xml");
    h.add("Accept", "text/plain;q=0.7");
    assertEquals("text/xml,text/plain;q=0.7", service("GET",
                                                      "http://localhost/test/a/b/1",
                                                      "http://localhost/test",
                                                      h,
                                                      null).getEntity());
    assertEquals("GET", service("GET",
                                "http://localhost/test/a/b/2",
                                "http://localhost/test",
                                null,
                                null).getEntity());
    assertEquals("securityContext", service("GET",
                                            "http://localhost/test/a/b/3",
                                            "http://localhost/test",
                                            null,
                                            null).getEntity());
    unregistry(r1);
  }

}
