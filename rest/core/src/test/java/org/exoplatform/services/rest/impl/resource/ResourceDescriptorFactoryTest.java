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

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.impl.method.MethodParameter;
import org.exoplatform.services.rest.impl.resource.ResourceDescriptorFactory;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceDescriptorFactoryTest extends BaseTest {

  public void testCreateAbstractResourceDescriptor() {
    AbstractResourceDescriptor resourceDescriptor = ResourceDescriptorFactory
        .createAbstractResourceDescriptor(SampleResource.class);
    assertTrue(resourceDescriptor.isRootResource());
    assertEquals("/a/{b}/",  resourceDescriptor.getPath().getPath());
    assertEquals(SampleResource.class, resourceDescriptor.getResourceClass());
    assertEquals(1, resourceDescriptor.getResourceMethodDescriptors().size());
    assertEquals(1, resourceDescriptor.getSubResourceMethodDescriptors().size());
    assertEquals(1, resourceDescriptor.getSubResourceLocatorDescriptors().size());
  }
  
  public void testResourceMethods() {
    // resource method SampleResource#get3()
    AbstractResourceDescriptor resourceDescriptor = ResourceDescriptorFactory
        .createAbstractResourceDescriptor(SampleResource.class);
    ResourceMethodDescriptor methodDescriptor = resourceDescriptor.getResourceMethodDescriptors().get(0);
    assertEquals("GET", methodDescriptor.getHttpMethod());
    assertEquals(MediaTypeHelper.DEFAULT_TYPE, methodDescriptor.consumes().get(0));
    assertEquals(MediaType.valueOf("application/xml"), methodDescriptor.produces().get(0));
    assertEquals(SampleResource.class, methodDescriptor.getParentResource().getResourceClass());
    assertEquals(1, methodDescriptor.getMethodParameters().size());
    MethodParameter methodParameter = methodDescriptor.getMethodParameters().get(0);
    assertEquals("hello", methodParameter.getDefaultValue());
    assertEquals(String.class, methodParameter.getParameterClass());
    assertEquals(PathParam.class, methodParameter.getAnnotation().annotationType());
    assertEquals(2, methodParameter.getAnnotations().length);
    assertEquals(PathParam.class, methodParameter.getAnnotations()[0].annotationType());
    assertEquals(DefaultValue.class, methodParameter.getAnnotations()[1].annotationType());
  }
  
  public void testSubResourceMethods() {
    // sub-resource method SampleResource#get1()
    AbstractResourceDescriptor resourceDescriptor = ResourceDescriptorFactory
        .createAbstractResourceDescriptor(SampleResource.class);
    SubResourceMethodDescriptor subResourceMethodDescriptor = resourceDescriptor.getSubResourceMethodDescriptors().get(0);
    assertEquals("POST", subResourceMethodDescriptor.getHttpMethod());
    assertEquals("{c}", subResourceMethodDescriptor.getPathValue().getPath());
    assertEquals(MediaType.valueOf("text/plain"), subResourceMethodDescriptor.consumes().get(0));
    assertEquals(MediaType.valueOf("text/xml"), subResourceMethodDescriptor.consumes().get(1));
    assertEquals(MediaType.valueOf("text/html"), subResourceMethodDescriptor.produces().get(0));
    assertEquals(SampleResource.class, subResourceMethodDescriptor.getParentResource().getResourceClass());
    assertEquals(1, subResourceMethodDescriptor.getMethodParameters().size());
    MethodParameter methodParameter = subResourceMethodDescriptor.getMethodParameters().get(0);
    assertEquals(null, methodParameter.getDefaultValue());
    assertEquals(List.class, methodParameter.getParameterClass());
    assertEquals(PathParam.class, methodParameter.getAnnotation().annotationType());
    assertEquals(1, methodParameter.getAnnotations().length);
    assertEquals(PathParam.class, methodParameter.getAnnotations()[0].annotationType());
  }

  public void testSubResourceLocators() {
    // sub-resource method SampleResource#get2()
    AbstractResourceDescriptor resourceDescriptor = ResourceDescriptorFactory
        .createAbstractResourceDescriptor(SampleResource.class);
    SubResourceLocatorDescriptor subResourceLocatorDescriptor = resourceDescriptor.getSubResourceLocatorDescriptors().get(0);
    assertEquals("{c}/d", subResourceLocatorDescriptor.getPathValue().getPath());
    assertEquals(SampleResource.class, subResourceLocatorDescriptor.getParentResource().getResourceClass());
    assertEquals(1, subResourceLocatorDescriptor.getMethodParameters().size());
    MethodParameter methodParameter = subResourceLocatorDescriptor.getMethodParameters().get(0);
    assertTrue(methodParameter.isEncoded());
    assertEquals(null, methodParameter.getDefaultValue());
    assertEquals(String.class, methodParameter.getParameterClass());
    assertEquals(PathParam.class, methodParameter.getAnnotation().annotationType());
    assertEquals(2, methodParameter.getAnnotations().length);
    assertEquals(PathParam.class, methodParameter.getAnnotations()[0].annotationType());
  }

  @Path("/a/{b}/")
  private static class SampleResource {

    @POST
    @Path("{c}")
    @Consumes({"text/plain", "text/xml"})
    @Produces({"text/html"})
    public void get1(@PathParam("b") List<String> p) {
      // this is sub-resource method
    }

    @Path("{c}/d")
    public void get2(@PathParam("b") @Encoded String p) {
      // this is sub-resource locator
    }

    @GET
    @Produces({"application/xml"})
    public void get3(@PathParam("b") @DefaultValue("hello") String p) {
      // this is resource method
    }
  }
  
  public void testNotPublicMethodAnnotated() {
    System.setProperty("org.exoplatform.ws.develop", "true");
    try {
      ResourceDescriptorFactory.createAbstractResourceDescriptor(BadResource.class);
      fail("Exception should be thrown");
    } catch (RuntimeException e) {
    } finally {
      System.setProperty("org.exoplatform.ws.develop", "false");
    }
  }
  
  private static class BadResource {
    @GET
    void get() {
      // not public method annotated
    }
  }

}
