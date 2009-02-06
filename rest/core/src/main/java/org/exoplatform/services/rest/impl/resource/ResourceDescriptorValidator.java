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

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.FormParam;
import javax.ws.rs.core.MultivaluedMap;

import org.exoplatform.services.rest.impl.method.MethodParameter;
import org.exoplatform.services.rest.resource.ResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * Validate ResourceDescriptors. @see
 * {@link ResourceDescriptor#accept(ResourceDescriptorVisitor)}.
 * <p>
 * Validation Goals:
 * <li>check number of method parameters without annotation, should be not more
 * then one at resource method or sub-resource method and no one at sub-resource
 * locator</li>
 * <li>if one of parameters at resource method or sub-resource method has
 * {@link FormParam} annotation then entity type can be only
 * MultivalueMap&lt;String, String&gt; and nothing other</li>
 * <li> {@link PathValue#getPath()} can't return empty string, it minds for root
 * resource classes, sub-resource methods and sub-resource locators can't have
 * annotation &#64;Path("")</li>
 * <li>Resource class must contains at least one resource method, sub-resource
 * method or sub-resource locator</li>
 * <p>
 * Non-Goals:
 * <li>Check does any two resource methods has the same consume and produce
 * media type. This will be done later in binding cycle</li>
 * <li>Check does any two sub-resource methods has the same consume and produce
 * media type and HTTP request method designation. This will be done later in binding cycle</li>
 * <li>Check does two sub-resource locators has the same UriPattern</li>
 * <p>
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ResourceDescriptorValidator implements ResourceDescriptorVisitor {

  /**
   * Validate AbstractResourceDescriptor. AbstractResourceDescriptor is a class
   * which annotated with path annotation then it is root resource, or not
   * annotated with path then it is sub-resource. Can have also consumes and
   * produces annotation. Path annotation is required for root resource.
   * {@inheritDoc}
   */
  public void visitAbstractResourceDescriptor(AbstractResourceDescriptor ard) {
    if (ard.getResourceMethodDescriptors().size() == 0
        && ard.getSubResourceMethodDescriptors().size() == 0
        && ard.getSubResourceLocatorDescriptors().size() == 0)
      throw new IllegalArgumentException("Not found any resource methods, sub-resource methods"
          + " or sub-resource locators in " + ard.getResourceClass().getName());

    if (ard.isRootResource() && ard.getPath().getPath().length() == 0)
      throw new IllegalArgumentException("Resource class " + ard.getResourceClass()
          + " is root resource but path value is null or empty," + " see javax.ws.rs.Path#value()");

  }

  /**
   * Validate ResourceMethodDescriptor. ResourceMethodDescriptor is method in
   * Resource class which has not path annotation. This method MUST have at
   * least one annotation (HTTP method, e.g. GET). {@inheritDoc}
   */
  public void visitResourceMethodDescriptor(ResourceMethodDescriptor rmd) {
    checkMethodParameters(rmd);
  }

  /**
   * Validate SubResourceLocatorDescriptor. SubResourceLocatorDescriptor is a
   * method which annotated with path annotation and has not HTTP method
   * annotation. This method can not directly process the request but it can
   * produces object that will handle the request. {@inheritDoc}
   */
  public void visitSubResourceLocatorDescriptor(SubResourceLocatorDescriptor srld) {
    if (srld.getPathValue().getPath() == null || srld.getPathValue().getPath().length() == 0)
      throw new IllegalArgumentException("Path value is null or empty for method "
          + srld.getMethod().getName() + " in resource class "
          + srld.getParentResource().getResourceClass() + ", see javax.ws.rs.Path#value()");
    checkMethodParameters(srld);
  }

  /**
   * Validate SubResourceMethodDescriptor. SubResourceMethodDescriptor is a
   * method which annotated with path annotation and has HTTP method annotation.
   * This method can process the request directly. {@inheritDoc}
   */
  public void visitSubResourceMethodDescriptor(SubResourceMethodDescriptor srmd) {
    if (srmd.getPathValue().getPath().length() == 0)
      throw new IllegalArgumentException("Path value is null or empty for method "
          + srmd.getMethod().getName() + " in resource class "
          + srmd.getParentResource().getResourceClass() + ", see javax.ws.rs.Path#value()");
    checkMethodParameters(srmd);
  }

  /**
   * Check method parameter for valid annotations. NOTE If a any method
   * parameter is annotated with {@link FormParam} then type of entity parameter
   * must be MultivalueMap&lt;String, String&gt;.
   * 
   * @param rmd See {@link ResourceMethodDescriptor}
   */
  private static void checkMethodParameters(ResourceMethodDescriptor rmd) {
    List<MethodParameter> l = rmd.getMethodParameters();
    boolean entity = false;
    boolean form = false;
    for (int i = 0; i < l.size(); i++) {
      // Must be only: MatrixParam, QueryParam, PathParam, HeaderParam,
      // FormParam, CookieParam, Context and only one of it at each parameter
      MethodParameter mp = l.get(i);
      if (mp.getAnnotation() == null) {
        if (!entity) {
          entity = true;
          if (form) // form already met then check type of entity
            checkFormParam(mp.getParameterClass(), mp.getParameterType());
        } else 
          throw new IllegalArgumentException("Wrong or absent annotation at parameter with index " + i
              + " at " + rmd.getParentResource().getResourceClass() + "#"
              + rmd.getMethod().getName());
        
      } else {
        if (mp.getAnnotation().annotationType() == FormParam.class) {
          form = true;
          if (entity) // entity already met then check type of entity
            checkFormParam(mp.getParameterClass(), mp.getParameterType());
        }
      }
    }
  }

  /**
   * Check does sub-resource locator has required annotation at method
   * parameters. Sub-resource locator can't has not annotated parameter (entity
   * parameter).
   * 
   * @param srld SubResourceLocatorDescriptor
   */
  private static void checkMethodParameters(SubResourceLocatorDescriptor srld) {
    List<MethodParameter> l = srld.getMethodParameters();
    for (int i = 0; i < l.size(); i++) {
      // Must be only: MatrixParam, QueryParam, PathParam, HeaderParam,
      // FormParam, CookieParam, Context and only one of it at each parameter
      MethodParameter mp = l.get(i);
      if (mp.getAnnotation() == null) {
        // not allowed to have not annotated parameters in resource locator
        throw new IllegalArgumentException("Wrong or absent annotation at parameter with index "
            + i + " at " + srld.getParentResource().getResourceClass() + "#"
            + srld.getMethod().getName());
      }
    }
  }

  /**
   * Check is supplied class MultivaluedMap&lt;String, String&gt;.
   * 
   * @param clazz class to be checked
   * @param type generic type
   * @see #checkGenericType(Type)
   */
  @SuppressWarnings("unchecked")
  private static void checkFormParam(Class clazz, Type type) {
    if (MultivaluedMap.class != clazz || !checkGenericType(type))
      throw new IllegalArgumentException("If a any method parameter is annotated with FormParam then type"
          + " of entity parameter MUST be MultivalueMap<String, String> or FormEntity");          
  }

  /**
   * Check is supplied class parameterized as &lt;String, String&gt;.
   * 
   * @param type generic type
   * @return true if type is {@link ParameterizedType} and parameterized
   *         &lt;String, String&gt;, false otherwise
   */
  private static boolean checkGenericType(Type type) {
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;

      Type[] genericTypes = ((ParameterizedType) parameterizedType).getActualTypeArguments();
      if (genericTypes.length == 2) {
        try {

          return (String.class == (Class<?>) genericTypes[0])
              && (String.class == (Class<?>) genericTypes[1]);

        } catch (ClassCastException e) {
          throw new RuntimeException("Unsupported type");
        }
      }
    }

    // not parameterized type
    // TODO must be tolerant for not parameterized type and use string as
    // default ?
    return false;
  }

}
