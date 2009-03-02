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
package org.exoplatform.services.rest.impl.resource;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.AbstractResourceTest;

/**
 * Created by The eXo Platform SAS. <br/>
 * Date: 23 Jan 2009
 * 
 * @author <a href="mailto:dmitry.kataev@exoplatform.com.ua">Dmytro Katayev</a>
 * @version $Id: AnnotationInheritanceTest.java
 */
public class AnnotationInheritanceTest extends AbstractResourceTest {

  public interface ResourceInterface {
    @GET
    @Produces(MediaType.TEXT_XML)
    String m0(String type);
  }

  @Path("/a")
  public class Resource1 implements ResourceInterface {
    public String m0(String type) {
      return "m0";
    }
  }

  @Path("/a")
  public class Resource2 implements ResourceInterface {
    @Produces(MediaType.APPLICATION_ATOM_XML)
    public String m0(String type) {
      return "m0";
    }
  }
  

  public void testAnnotationsInheritance() throws Exception {
    Resource1 resource1 = new Resource1();
    Resource2 resource2 = new Resource2();

    registry(resource1);

    assertEquals(200, service("GET", "/a", "", null, null).getStatus());
    assertEquals("m0", service("GET", "/a", "", null, null).getEntity());
    assertEquals(MediaType.TEXT_XML_TYPE, service("GET", "/a", "", null, null).getContentType());

    unregistry(resource1);

    registry(resource2);
    assertEquals(200, service("GET", "/a", "", null, null).getStatus());
    assertEquals("m0", service("GET", "/a", "", null, null).getEntity());
    assertEquals(MediaType.APPLICATION_ATOM_XML_TYPE,
                 service("GET", "/a", "", null, null).getContentType());
    unregistry(resource2);

  }

}
