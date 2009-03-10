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

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HEAD;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MultivaluedMap;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceDescriptorValidatorTest extends BaseTest {

  public void testAbstractResourceDescriptorValidator() {
    AbstractResourceDescriptor resource = createResourceDescriptor(Resource1.class);
    try {
      resource.accept(new ResourceDescriptorValidator());
      fail("Exception should be here");
    } catch (RuntimeException e) {
    }
    resource = createResourceDescriptor(Resource2.class);
    try {
      resource.accept(new ResourceDescriptorValidator());
      fail("Exception should be here");
    } catch (RuntimeException e) {
    }
  }
  
  
  public void testResourceMethodDescriptorValidator() {
    AbstractResourceDescriptor resource = createResourceDescriptor(Resource3.class);
    List<ResourceMethodDescriptor> l = resource.getResourceMethodDescriptors();
    ResourceDescriptorValidator validator = new ResourceDescriptorValidator();
    for (ResourceMethodDescriptor rmd : l) {
      String mn = rmd.getMethod().getName();
      if ("m1".equals(mn)) 
        rmd.accept(validator);
      else {
        try {
          rmd.accept(validator);
          fail("Exception should be here");
        } catch (RuntimeException e) {
        }
      }
    }
  }
  
  public void testSubResourceMethodDescriptorValidator() {
    AbstractResourceDescriptor resource = createResourceDescriptor(Resource4.class);
    List<SubResourceMethodDescriptor> l = resource.getSubResourceMethodDescriptors();
    
    ResourceDescriptorValidator validator = new ResourceDescriptorValidator();
    for (SubResourceMethodDescriptor srmd : l) {
      String mn = srmd.getMethod().getName();
      if ("m1".equals(mn) || "m3".equals(mn)) 
        srmd.accept(validator);
      else {
        try {
          srmd.accept(validator);
          fail("Exception should be here");
        } catch (RuntimeException e) {
        }
      }
    }
  }

  public void testSubResourceLocatorDescriptorValidator() {
    AbstractResourceDescriptor resource = createResourceDescriptor(Resource5.class);
    List<SubResourceLocatorDescriptor> l = resource.getSubResourceLocatorDescriptors();
    ResourceDescriptorValidator validator = new ResourceDescriptorValidator();
    for (SubResourceLocatorDescriptor rmd : l) {
      String mn = rmd.getMethod().getName();
      if ("m1".equals(mn)) 
        rmd.accept(validator);
      else {
        try {
          rmd.accept(validator);
          fail("Exception should be here");
        } catch (RuntimeException e) {
        }
      }
    }
  }
  
  //
  
  @Path("/a/b") // OK
  private static class Resource1 {
    public void m1() {
    }
  }

  @Path("") // wrong
  private static class Resource2 {
    @GET
    public void m1() {
    }
  }

  @Path("/a/b")
  private static class Resource3 {
    @GET
    public void m1(@FormParam("a") String t, MultivaluedMap<String, String> entity) {
      // OK
    }
    @SuppressWarnings("unchecked")
    @POST
    public void m2(@FormParam("a") String t, MultivaluedMap entity) {
      // wrong ?
    }
    @PUT
    public void m3(@FormParam("a") String t, String entity) {
      // wrong
    }
    @HEAD
    public void m4(String entity1, String entity2) {
      // wrong
    }
  }
  
  @Path("/a/b")
  private static class Resource4 {
    @GET
    @Path("c")
    public void m1() {
      // OK
    }
    @GET
    @Path("") // wrong
    public void m2() {
    }
    @GET
    @Path("c/d")
    public void m3(@FormParam("a") String t, MultivaluedMap<String, String> entity) {
      // OK
    }
    @POST
    @Path("c/d/e")
    public void m4(@FormParam("a") String t, MultivaluedMap entity) {
      // wrong ?
    }
    @PUT
    @Path("c/d/e/f")
    public void m5(@FormParam("a") String t, String entity) {
      // wrong
    }
    @GET
    @Path("c/d/e/f/g")
    public void m6(String entity1, String entity2) {
      // wrong
    }
  }

  @Path("/a/b")
  private static class Resource5 {
    @Path("c")
    public void m1() {
      // OK
    }
    @Path("") // wrong
    public void m2() {
    }
    @Path("c/d")
    public void m3(@PathParam("a") String t, String entity) {
      // wrong
    }
  }

}
