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

import java.lang.reflect.Constructor;
import java.util.Collections;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.BaseTest;
import org.exoplatform.services.rest.ConstructorInjector;
import org.exoplatform.services.rest.FieldInjector;
import org.exoplatform.services.rest.impl.ConstructorInjectorImpl;
import org.exoplatform.services.rest.impl.FieldInjectorImpl;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.method.MethodParameter;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceDescriptorTest extends BaseTest {

  public void testCreateAbstractResourceDescriptor() {
    AbstractResourceDescriptor resource = createResourceDescriptor(SampleResource.class);
    assertTrue(resource.isRootResource());
    assertEquals("/a/{b}/", resource.getPath().getPath());
    assertEquals(SampleResource.class, resource.getResourceClass());
    assertEquals(1, resource.getResourceMethodDescriptors().size());
    assertEquals(1, resource.getSubResourceMethodDescriptors().size());
    assertEquals(1, resource.getSubResourceLocatorDescriptors().size());
  }

  public void testResourceMethods() {
    // resource method SampleResource#get3()
    AbstractResourceDescriptor resource = createResourceDescriptor(SampleResource.class);
    ResourceMethodDescriptor methodDescriptor = resource.getResourceMethodDescriptors().get(0);
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
    AbstractResourceDescriptor resource = createResourceDescriptor(SampleResource.class);
    SubResourceMethodDescriptor subResourceMethodDescriptor = resource.getSubResourceMethodDescriptors()
                                                                      .get(0);
    assertEquals("POST", subResourceMethodDescriptor.getHttpMethod());
    assertEquals("{c}", subResourceMethodDescriptor.getPathValue().getPath());
    assertEquals(MediaType.valueOf("text/plain"), subResourceMethodDescriptor.consumes().get(0));
    assertEquals(MediaType.valueOf("text/xml"), subResourceMethodDescriptor.consumes().get(1));
    assertEquals(MediaType.valueOf("text/html"), subResourceMethodDescriptor.produces().get(0));
    assertEquals(SampleResource.class, subResourceMethodDescriptor.getParentResource()
                                                                  .getResourceClass());
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
    AbstractResourceDescriptor resource = createResourceDescriptor(SampleResource.class);
    SubResourceLocatorDescriptor subResourceLocatorDescriptor = resource.getSubResourceLocatorDescriptors()
                                                                        .get(0);
    assertEquals("{c}/d", subResourceLocatorDescriptor.getPathValue().getPath());
    assertEquals(SampleResource.class, subResourceLocatorDescriptor.getParentResource()
                                                                   .getResourceClass());
    assertEquals(1, subResourceLocatorDescriptor.getMethodParameters().size());
    MethodParameter methodParameter = subResourceLocatorDescriptor.getMethodParameters().get(0);
    assertTrue(methodParameter.isEncoded());
    assertEquals(null, methodParameter.getDefaultValue());
    assertEquals(String.class, methodParameter.getParameterClass());
    assertEquals(PathParam.class, methodParameter.getAnnotation().annotationType());
    assertEquals(2, methodParameter.getAnnotations().length);
    assertEquals(PathParam.class, methodParameter.getAnnotations()[0].annotationType());
  }

  public void testFields() {
    AbstractResourceDescriptor resource = createResourceDescriptor(SampleResource.class);
    List<FieldInjector> fields = resource.getFieldInjectors();
    assertEquals(1, fields.size());
    FieldInjector f = fields.get(0);
    assertEquals(String.class, f.getParameterClass());
    assertEquals(String.class, f.getGenericType());
    assertEquals("default", f.getDefaultValue());
    assertEquals(PathParam.class, f.getAnnotation().annotationType());
    assertEquals("b", ((PathParam) f.getAnnotation()).value());
    assertTrue(f.isEncoded());
  }

  public void testConstructors() {
    AbstractResourceDescriptor resource = createResourceDescriptor(SampleResource.class);
    assertEquals(3, resource.getConstructorInjectors().size());
    List<ConstructorInjector> c = resource.getConstructorInjectors();
    assertEquals(2, c.get(0).getParameters().size());
    assertEquals(1, c.get(1).getParameters().size());
    assertEquals(0, c.get(2).getParameters().size());

    assertFalse(c.get(0).getParameters().get(0).isEncoded());
    assertTrue(c.get(0).getParameters().get(1).isEncoded());
    assertEquals(QueryParam.class, c.get(0).getParameters().get(0).getAnnotation().annotationType());
    assertEquals(PathParam.class, c.get(0).getParameters().get(1).getAnnotation().annotationType());
    assertEquals("test", ((QueryParam) c.get(0).getParameters().get(0).getAnnotation()).value());
    assertEquals("b", ((PathParam) c.get(0).getParameters().get(1).getAnnotation()).value());

    assertFalse(c.get(1).getParameters().get(0).isEncoded());
    assertEquals(PathParam.class, c.get(1).getParameters().get(0).getAnnotation().annotationType());
    assertEquals("b", ((PathParam) c.get(1).getParameters().get(0).getAnnotation()).value());
  }

  @Path("/a/{b}/")
  public static class SampleResource {

    @DefaultValue("default")
    @PathParam("b")
    @Encoded
    private String field1;

    public SampleResource(@PathParam("b") String str) {
    }

    public SampleResource() {
    }

    public SampleResource(@QueryParam("test") int i, @Encoded @PathParam("b") String str) {
    }

    @POST
    @Path("{c}")
    @Consumes( { "text/plain", "text/xml" })
    @Produces( { "text/html" })
    public void get1(@PathParam("b") List<String> p) {
      // this is sub-resource method
    }

    @Path("{c}/d")
    public void get2(@PathParam("b") @Encoded String p) {
      // this is sub-resource locator
    }

    @GET
    @Produces( { "application/xml" })
    public void get3(@PathParam("b") @DefaultValue("hello") String p) {
      // this is resource method
    }
  }

  public void testNotPublicMethodAnnotated() {
    // TODO Mechanism for checking log messages. There is some sections in
    // JAX-RS specification that said 'should warn...'. Need control this
    // messages in some way.
    AbstractResourceDescriptor resource = createResourceDescriptor(BadResource.class);
  }

  @Path("/")
  private static class BadResource {
    @GET
    void get() {
      // not public method annotated
    }
  }

}
