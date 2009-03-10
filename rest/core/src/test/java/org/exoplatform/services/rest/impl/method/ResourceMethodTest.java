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

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;

import org.exoplatform.services.rest.AbstractResourceTest;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date: 19 Jan 2009
 * 
 * @author <a href="mailto:dmitry.kataev@exoplatform.com.ua">Dmytro Katayev</a>
 * @version $Id: ExceptionsTest.java
 */
public class ResourceMethodTest extends AbstractResourceTest {

  @Target(ElementType.METHOD)
  @Retention(RetentionPolicy.RUNTIME)
  @HttpMethod("CUSTOM")
  public @interface CUSTOM {

  }

  @Path("/a")
  public static class Resource1 {

    @GET
    public String get() {
      return "get";
    }

    @POST
    public String post() {
      return "post";
    }

    @PUT
    public String put() {
      return "put";
    }

    @HEAD
    public String head() {
      return "head";
    }

    @DELETE
    public String delete() {
      return "delete";
    }

    @CUSTOM
    public String custom() {
      return "custom";
    }

  }

  public void testMethodAnnotations() throws Exception {

    Resource1 resource = new Resource1();
    registry(resource);

    assertEquals("get", service("GET", "/a", "", null, null).getEntity());
    assertEquals("post", service("POST", "/a", "", null, null).getEntity());
    assertEquals("put", service("PUT", "/a", "", null, null).getEntity());
    assertEquals("delete", service("DELETE", "/a", "", null, null).getEntity());

    assertEquals(200, service("HEAD", "/a", "", null, null).getStatus());

    assertEquals("custom", service("CUSTOM", "/a", "", null, null).getEntity());

    unregistry(resource);

  }

  @Path("/d")
  public static class Resource2 {
    @POST
    public String post(String body1, String body2) {
      return "post";
    }
  }

  public void testMultyEntity() throws Exception {
    Resource2 resource = new Resource2();
    try {
      registry(resource);
      fail();
    } catch (RuntimeException e){
    }
    // [jsr-311 3.3.2.1] Resource methods MUST NOT have more than one parameter
    // that is not annotated with one of the above-listed annotations.
    assertEquals(404, service("POST", "/d", "", null, null).getStatus());
  }
  
}
