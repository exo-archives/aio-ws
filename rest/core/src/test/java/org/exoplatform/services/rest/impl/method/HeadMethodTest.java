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
import javax.ws.rs.HEAD;
import javax.ws.rs.Path;

import org.exoplatform.services.rest.AbstractResourceTest;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date: 21 Jan 2009
 * 
 * @author <a href="mailto:dmitry.kataev@exoplatform.com.ua">Dmytro Katayev</a>
 * @version $Id: HeadMethodTest.java
 */
public class HeadMethodTest extends AbstractResourceTest {

  @Path("/a")
  public static class Resource1 {

    @GET
    public String m0() {
      return "get";
    }

    @HEAD
    public String m1() {
      return "head";
    }

  }

  @Path("/b")
  public static class Resource2 {

    @GET
    public String m0() {
      return "get";
    }

  }

  public void testHeadMethod() throws Exception {
    Resource1 resource1 = new Resource1();
    registry(resource1);

    assertEquals("get", service("GET", "/a", "", null, null).getEntity());
    assertEquals(200, service("HEAD", "/a", "", null, null).getStatus());

    unregistry(resource1);

    Resource2 resource2 = new Resource2();

    registry(resource2);

    assertEquals("get", service("GET", "/b", "", null, null).getEntity());
    assertEquals(200, service("HEAD", "/b", "", null, null).getStatus());
    assertNull(service("HEAD", "/b", "", null, null).getEntity());

    unregistry(resource2);
  }

}
