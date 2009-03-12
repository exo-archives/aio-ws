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

package org.exoplatform.services.rest.impl.provider;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import javax.ws.rs.ext.Provider;
import javax.ws.rs.ext.Providers;

import org.exoplatform.services.rest.AbstractResourceTest;
import org.exoplatform.services.rest.impl.ContainerResponse;
import org.exoplatform.services.rest.provider.EntityProvider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ProviderContextParameterInjectionTest extends AbstractResourceTest {

  public static class MockEntity {
    String entity;
  }

  @Provider
  public static class ContextInjectionChecker implements EntityProvider<MockEntity> {

    @Context
    private UriInfo            uriInfo;

    @Context
    private Request            request;

    @Context
    private HttpHeaders        httpHeaders;

    @Context
    private Providers          providers;

    @Context
    private HttpServletRequest httpRequest;

    // EntityProvider can be used for reading/writing ONLY if all fields above
    // initialized

    public boolean isReadable(Class<?> type,
                              Type genericType,
                              Annotation[] annotations,
                              MediaType mediaType) {
      return uriInfo != null && request != null && httpHeaders != null && providers != null
          && httpRequest != null;
    }

    public MockEntity readFrom(Class<MockEntity> type,
                               Type genericType,
                               Annotation[] annotations,
                               MediaType mediaType,
                               MultivaluedMap<String, String> httpHeaders,
                               InputStream entityStream) throws IOException,
                                                        WebApplicationException {
      MockEntity me = new MockEntity();
      me.entity = IOHelper.readString(entityStream, IOHelper.DEFAULT_CHARSET_NAME);
      return me;
    }

    public long getSize(MockEntity t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType) {
      return 0;
    }

    public boolean isWriteable(Class<?> type,
                               Type genericType,
                               Annotation[] annotations,
                               MediaType mediaType) {
      return uriInfo != null && request != null && httpHeaders != null && providers != null
          && httpRequest != null;
    }

    public void writeTo(MockEntity t,
                        Class<?> type,
                        Type genericType,
                        Annotation[] annotations,
                        MediaType mediaType,
                        MultivaluedMap<String, Object> httpHeaders,
                        OutputStream entityStream) throws IOException, WebApplicationException {
      IOHelper.writeString(t.entity, entityStream, IOHelper.DEFAULT_CHARSET_NAME);
    }

  }

  public void setUp() throws Exception {
    super.setUp();
    rd.addEntityProvider(ContextInjectionChecker.class);
  }

  @Path("a")
  public static class Resource1 {
    @GET
    @Path("1")
    public MockEntity m0(MockEntity me) {
      assertNotNull(me);
      assertEquals("to be or not to be", me.entity);
      me.entity = "to be";
      return me;
    }
  }

  public void test0() throws Exception {
    registry(new Resource1());
    ContainerResponse resp = service("GET", "/a/1", "", null, "to be or not to be".getBytes());
    assertEquals("to be", ((MockEntity) resp.getEntity()).entity);
    unregistry(Resource1.class);
  }

}
