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

import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.uri.UriPattern;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceClassTest extends BaseTest {

  public void testResourceMethod() {
    AbstractResourceDescriptor resourceDescriptor =
      ResourceDescriptorFactory.createAbstractResourceDescriptor(Resource1.class);
    ResourceClass resourceClass = new ResourceClass(resourceDescriptor, new Resource1());
    assertNotNull(resourceClass.getUriPattern());
    
    assertEquals(0, resourceClass.getSubResourceMethods().size());
    assertEquals(0, resourceClass.getSubResourceLocators().size());
    // one more method will be created for HEAD request.
    // For each GET method one HEAD method created.  
    assertEquals(5, resourceClass.getResourceMethods().size());
    
    assertEquals(2, resourceClass.getResourceMethods().get("POST").size());
    assertEquals(2, resourceClass.getResourceMethods().get("GET").size());
    assertEquals(1, resourceClass.getResourceMethods().get("PUT").size());
    // created automatically
    assertEquals(2, resourceClass.getResourceMethods().get("HEAD").size());

    List<ResourceMethodDescriptor> rmd = resourceClass.getResourceMethods().get("POST");
    Iterator<ResourceMethodDescriptor> i = rmd.iterator();
    ResourceMethodDescriptor rm = i.next();
    assertEquals("m5", rm.getMethod().getName());
    assertEquals("POST", rm.getHttpMethod());
    rm = i.next();
    assertEquals("m4", rm.getMethod().getName());
    assertEquals("POST", rm.getHttpMethod());

    rmd = resourceClass.getResourceMethods().get("GET");
    i = rmd.iterator();
    rm = i.next();
    assertEquals("m2", rm.getMethod().getName());
    assertEquals("GET", rm.getHttpMethod());
    rm = i.next();
    assertEquals("m1", rm.getMethod().getName());
    assertEquals("GET", rm.getHttpMethod());

    rmd = resourceClass.getResourceMethods().get("PUT");
    i = rmd.iterator();
    rm = i.next();
    assertEquals("m3", rm.getMethod().getName());
    assertEquals("PUT", rm.getHttpMethod());
  }

  @Path("/a/{b}/")
  private static class Resource1 {

    // -------------------
    // one of this should be ignored
    @GET
    @Produces( { "text/*" })
    public void m1() {
    }
//    // fix 
//    @GET
//    @Produces( { "text/*" })
//    public void m0() {
//    }
    // -------------------

    @GET
    @Produces( { "text/html", "text/xml" })
    public void m2() {
    }

    @PUT
    @Consumes( { "text/xml", "application/xml" })
    @Produces( { "text/html" })
    public void m3() {
    }

    @POST
    public void m4() {
    }

    @POST
    @Consumes( { "text/*", "text/xml" })
    public void m5() {
    }
  }
  
  public void testSubResourceMethod() {
    AbstractResourceDescriptor resourceDescriptor =
      ResourceDescriptorFactory.createAbstractResourceDescriptor(Resource2.class);
    ResourceClass resourceClass = new ResourceClass(resourceDescriptor, new Resource2());
    assertNotNull(resourceClass.getUriPattern());
    assertEquals(1, resourceClass.getResourceMethods().size());
    assertEquals(0, resourceClass.getSubResourceLocators().size());
    assertEquals(3, resourceClass.getSubResourceMethods().size());
    
    UriPattern u = new UriPattern("c");
    ResourceMethodMap rmm = resourceClass.getSubResourceMethods().get(u);
    assertEquals(3, rmm.size());
    assertEquals(1, rmm.get("PUT").size());
    assertEquals(1, rmm.get("GET").size());
    // created automatically
    assertEquals(1, rmm.get("HEAD").size());

    u = new UriPattern("c/e");
    rmm = resourceClass.getSubResourceMethods().get(u);
    assertEquals(1, rmm.size());
    assertEquals(2, rmm.get("POST").size());

    u = new UriPattern("d/");
    rmm = resourceClass.getSubResourceMethods().get(u);
    assertEquals(2, rmm.size());
    assertEquals(1, rmm.get("GET").size());
    // created automatically
    assertEquals(1, rmm.get("HEAD").size());
  }

  @Path("/a/{b}/")
  private static class Resource2 {

    @GET
    @Path("c")
    @Produces( { "text/*" })
    public void m1() {
    }

    @GET
    @Path("d/")
    @Produces( { "text/html", "text/xml" })
    public void m2() {
    }

    @PUT
    @Path("c")
    @Consumes( { "text/xml", "application/xml" })
    @Produces( { "text/html" })
    public void m3() {
    }

    @POST
    @Path("c/e")
    public void m4() {
    }

    @POST
    @Path("c/e")
    @Consumes( { "text/*", "text/xml" })
    public void m5() {
    }
  }

  public void testSubResourceLocator() {
    AbstractResourceDescriptor resourceDescriptor =
      ResourceDescriptorFactory.createAbstractResourceDescriptor(Resource3.class);
    ResourceClass resourceClass = new ResourceClass(resourceDescriptor, new Resource3());
    assertNotNull(resourceClass.getUriPattern());
    assertEquals(1, resourceClass.getResourceMethods().size());
    assertEquals(0, resourceClass.getSubResourceMethods().size());
    assertEquals(3, resourceClass.getSubResourceLocators().size());
    
    UriPattern u = new UriPattern("c");
    SubResourceLocatorDescriptor rld = resourceClass.getSubResourceLocators().get(u);
    assertNotNull(rld);

    u = new UriPattern("c/e");
    rld = resourceClass.getSubResourceLocators().get(u);
    assertNotNull(rld);

    u = new UriPattern("d/");
    rld = resourceClass.getSubResourceLocators().get(u);
    assertNotNull(rld);
  }

  @Path("/a/{b}/")
  private static class Resource3 {

    @Path("d/")
    public void m2() {
    }

    // -------------------
    // one of this should be ignored
    @Path("c")
    public void m3() {
    }
    @Path("c")
    public void m0() {
    }
    //---------------------

    @Path("c/e")
    public void m4() {
    }

  }

}
