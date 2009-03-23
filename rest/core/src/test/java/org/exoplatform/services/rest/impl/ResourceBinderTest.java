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

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.UriInfo;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.ObjectFactory;
import org.exoplatform.services.rest.impl.ResourceBinder;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceBinderTest extends BaseTest {

  private ResourceBinder binder;
  
  /**
   * {@inheritDoc}
   */
  @Override
  public void setUp() throws Exception {
    super.setUp();
    binder = (ResourceBinder) container.getComponentInstanceOfType(ResourceBinder.class);
    binder.clear();
  }
  
  public void testBind() {
    binder.bind(Resource.class);
    assertEquals(1, binder.getResourceFactories().size());
    ObjectFactory<AbstractResourceDescriptor> rc = binder.getResourceFactories().iterator().next();
    
    // two resource methods because method annotated 'GET' and 'HEAD' added automatically.
    assertEquals(3, rc.getObjectModel().getResourceMethods().size());
    
    assertEquals(1, rc.getObjectModel().getSubResourceMethods().size());
    // two resource methods because method annotated 'GET' and 'HEAD' added automatically.
    assertEquals(2, rc.getObjectModel().getSubResourceMethods().values().iterator().next().size());
    
    assertEquals(1, rc.getObjectModel().getSubResourceLocators().size());
  }
  
  public void testUnbind() {
    binder.bind(Resource.class);
    binder.unbind(Resource.class);
    assertEquals(0, binder.getResourceFactories().size());
  }
  
  @Path("/a/b/{c}")
  public static class Resource {

    @SuppressWarnings("unused")
    @PathParam("c")
    private String pathsegm;
    
    public Resource() {
    }
    
    public Resource(@Context UriInfo uriInfo) {
    }
    
    @GET
    @Produces("text/html")
    public void m1() {
    }
    
    @GET
    @Path("d")
    @Produces("text/html")
    public void m2() {
    }

    @Path("d")
    public void m3() {
    }
  }
  
  public void testSameResourceURI() {
    List<ObjectFactory<AbstractResourceDescriptor>> l = binder.getResourceFactories();
    assertTrue(binder.bind(SameURIResource1.class));
    assertEquals(1, l.size());
    assertFalse(binder.bind(SameURIResource2.class));
    assertEquals(1, l.size());
    binder.clear();
    assertTrue(binder.bind(SameURIResource2.class));
    assertEquals(1, l.size());
    assertFalse(binder.bind(SameURIResource1.class));
    assertEquals(1, l.size());
    binder.clear();
    assertTrue(binder.bind(new SameURIResource1()));
    assertEquals(1, l.size());
    assertFalse(binder.bind(new SameURIResource2()));
    assertEquals(1, l.size());
    binder.clear();
    assertTrue(binder.bind(new SameURIResource2()));
    assertEquals(1, l.size());
    assertFalse(binder.bind(new SameURIResource1()));
    assertEquals(1, l.size());
  }
  
  @Path("/a/b/c/{d}/e")
  public static class SameURIResource1 {
    @GET
    public void m0() {
    }
  }

  @Path("/a/b/c/{d}/e")
  public static class SameURIResource2 {
    @GET
    public void m0() {
    }
  }

}
