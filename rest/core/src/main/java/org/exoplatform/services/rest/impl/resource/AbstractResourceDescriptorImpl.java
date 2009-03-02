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

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ConstructorInjector;
import org.exoplatform.services.rest.FieldInjector;
import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.impl.method.DefaultMethodInvoker;
import org.exoplatform.services.rest.impl.method.MethodParameterImpl;
import org.exoplatform.services.rest.impl.method.ParameterHelper;
import org.exoplatform.services.rest.impl.uri.UriPattern;
import org.exoplatform.services.rest.method.MethodParameter;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AbstractResourceDescriptorImpl implements AbstractResourceDescriptor {
  
  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(AbstractResourceDescriptorImpl.class.getName());

  /**
   * @see {@link PathValue}.
   */
  private final PathValue                          path;

  /**
   * See {@link UriPattern}.
   */
  private final UriPattern                         uriPattern;

  /**
   * Resource class.
   */
  private final Class<?>                           resourceClazz;
  
  /**
   * Sub-resource methods. Sub-resource method has path annotation.
   * 
   * @see {@link SubResourceMethodDescriptor}
   */
  private final List<SubResourceMethodDescriptor>  subResourceMethods;

  /**
   * Sub-resource locators. Sub-resource locator has path annotation.
   * 
   * @see {@link SubResourceLocatorDescriptor}
   */
  private final List<SubResourceLocatorDescriptor> subResourceLocators;

  /**
   * Resource methods. Resource method has not own path annotation.
   * 
   * @see {@link ResourceMethodDescriptor}
   */
  private final List<ResourceMethodDescriptor>     resourceMethods;

  /**
   * Resource class constructors.
   * 
   * @see {@link ConstructorInjector}
   */
  private final List<ConstructorInjector>          constructorDescriptors;

  /**
   * Resource class fields.
   */
  private final List<FieldInjector>                fields;

  /**
   * Constructs new instance of AbstractResourceDescriptor with path (root
   * resource).
   * 
   * @param path the path value.
   * @param resourceClazz resource class
   */
  public AbstractResourceDescriptorImpl(PathValue path, Class<?> resourceClazz) {
    this.path = path;
    if (path != null)
      uriPattern = new UriPattern(path.getPath());
    else
      uriPattern = null;
    this.resourceClazz = resourceClazz;
    this.constructorDescriptors = new ArrayList<ConstructorInjector>();
    this.fields = new ArrayList<FieldInjector>();
    this.subResourceMethods = new ArrayList<SubResourceMethodDescriptor>();
    this.subResourceLocators = new ArrayList<SubResourceLocatorDescriptor>();
    this.resourceMethods = new ArrayList<ResourceMethodDescriptor>();
    
    processMethod();
  }

  /**
   * Constructs new instance of AbstractResourceDescriptor without path
   * (sub-resource).
   * 
   * @param resourceClazz resource class
   */
  public AbstractResourceDescriptorImpl(Class<?> resourceClazz) {
    this(null, resourceClazz);
  }

  /**
   * {@inheritDoc}
   */
  public void accept(ResourceDescriptorVisitor visitor) {
    visitor.visitAbstractResourceDescriptor(this);
  }

  /**
   * {@inheritDoc}
   */
  public PathValue getPath() {
    return path;
  }

  /**
   * {@inheritDoc}
   */
  public UriPattern getUriPattern() {
    return uriPattern;
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isRootResource() {
    return path != null;
  }

  /**
   * {@inheritDoc}
   */
  public Class<?> getResourceClass() {
    return resourceClazz;
  }

  /**
   * {@inheritDoc}
   */
  public List<ConstructorInjector> getConstructorInjectors() {
    return constructorDescriptors;
  }

  /**
   * {@inheritDoc}
   */
  public List<FieldInjector> getFieldInjectors() {
    return fields;
  }

  /**
   * {@inheritDoc}
   */
  public List<ResourceMethodDescriptor> getResourceMethodDescriptors() {
    return resourceMethods;
  }

  /**
   * {@inheritDoc}
   */
  public List<SubResourceLocatorDescriptor> getSubResourceLocatorDescriptors() {
    return subResourceLocators;
  }

  /**
   * {@inheritDoc}
   */
  public List<SubResourceMethodDescriptor> getSubResourceMethodDescriptors() {
    return subResourceMethods;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("[ AbstractResourceDescriptor: ");
    sb.append("path: " + getPath())
      .append("; isRootResource: " + isRootResource())
      .append("; class: " + getResourceClass())
      .append(" ]");
    ;
    return sb.toString();
  }

  /**
   * Process method of resource and separate them to three types Resource
   * Methods, Sub-Resource Methods and Sub-Resource Locators.
   */
  protected void processMethod() {
    Class<?> resourceClass = getResourceClass();

    for (Method method : resourceClass.getDeclaredMethods()) {
      for (Annotation a : method.getAnnotations()) {
        Class<?> ac = a.annotationType();
        if (!Modifier.isPublic(method.getModifiers())
            && (ac == CookieParam.class || ac == Consumes.class || ac == Context.class
                || ac == DefaultValue.class || ac == Encoded.class || ac == FormParam.class
                || ac == HeaderParam.class || ac == MatrixParam.class || ac == Path.class
                || ac == PathParam.class || ac == Produces.class || ac == QueryParam.class
                || ac.getAnnotation(HttpMethod.class) != null)) {

          LOG.warn("Non-public method at resource " + toString()
              + " annotated with JAX-RS annotation: " + a);
        }
      }
    }

    for (Method method : resourceClass.getMethods()) {
      Path subPath = getMethodAnnotation(method, resourceClass, Path.class, false);
      HttpMethod httpMethod = getMethodAnnotation(method, resourceClass, HttpMethod.class, true);

      if (subPath != null || httpMethod != null) {
        List<MethodParameter> params = createMethodParametersList(resourceClass, method);
        if (httpMethod != null) {

          Produces p = getMethodAnnotation(method, resourceClass, Produces.class, false);
          if (p == null)
            p = resourceClass.getAnnotation(Produces.class);
          List<MediaType> produces = MediaTypeHelper.createProducesList(p);

          Consumes c = getMethodAnnotation(method, resourceClass, Consumes.class, false);
          if (c == null)
            c = resourceClass.getAnnotation(Consumes.class);
          List<MediaType> consumes = MediaTypeHelper.createConsumesList(c);

          if (subPath == null) {
            // resource method
            ResourceMethodDescriptor res = new ResourceMethodDescriptorImpl(method,
                                                                            httpMethod.value(),
                                                                            params,
                                                                            this,
                                                                            consumes,
                                                                            produces,
                                                                            new DefaultMethodInvoker());
            getResourceMethodDescriptors().add(res);
          } else {
            // sub-resource method
            SubResourceMethodDescriptor subRes = new SubResourceMethodDescriptorImpl(new PathValue(subPath.value()),
                                                                                     method,
                                                                                     httpMethod.value(),
                                                                                     params,
                                                                                     this,
                                                                                     consumes,
                                                                                     produces,
                                                                                     new DefaultMethodInvoker());
            getSubResourceMethodDescriptors().add(subRes);
          }
        } else {
          if (subPath != null) {
            // sub-resource locator
            SubResourceLocatorDescriptor loc = new SubResourceLocatorDescriptorImpl(new PathValue(subPath.value()),
                                                                                    method,
                                                                                    params,
                                                                                    this,
                                                                                    new DefaultMethodInvoker());

            getSubResourceLocatorDescriptors().add(loc);
          }
        }
      }
    }
  }

  /**
   * Create list of {@link MethodParameter} .
   * 
   * @param resourceClass class
   * @param method See {@link Method}
   * @return list of {@link MethodParameter}
   */
  protected List<MethodParameter> createMethodParametersList(Class<?> resourceClass, Method method) {
    Class<?>[] parameterClasses = method.getParameterTypes();
    if (parameterClasses.length == 0)
      return java.util.Collections.emptyList();

    Type[] parameterGenTypes = method.getGenericParameterTypes();
    Annotation[][] annotations = method.getParameterAnnotations();

    List<MethodParameter> params = new ArrayList<MethodParameter>(parameterClasses.length);
    for (int i = 0; i < parameterClasses.length; i++) {
      String defaultValue = null;
      Annotation annotation = null;
      boolean encoded = false;

      List<String> allowedAnnotation = ParameterHelper.RESOURCE_METHOD_PARAMETER_ANNOTATIONS;

      for (Annotation a : annotations[i]) {
        Class<?> ac = a.annotationType();
        if (allowedAnnotation.contains(ac.getName())) {

          if (annotation == null) {
            annotation = a;
          } else {
            String msg = "JAX-RS annotations on one of method parameters of resource " + toString()
                + "are equivocality. " + "Annotations: " + annotation + " and " + a
                + " can't be applied to one parameter.";
            throw new RuntimeException(msg);
          }

        } else if (ac == Encoded.class) {
          encoded = true;
        } else if (ac == DefaultValue.class) {
          defaultValue = ((DefaultValue) a).value();
        } else {
          LOG.warn("Method parameter contains unknown or not valid JAX-RS annotation "
              + a.toString() + ". It will be ignored.");
        }
      }

      encoded = encoded || resourceClass.getAnnotation(Encoded.class) != null;

      MethodParameter mp = new MethodParameterImpl(annotation,
                                                   annotations[i],
                                                   parameterClasses[i],
                                                   parameterGenTypes[i],
                                                   defaultValue,
                                                   encoded);
      params.add(mp);
    }

    return params;
  }

  //

  /**
   * Get all method with at least one annotation which has annotation
   * <i>annotation</i>. It is useful for annotation {@link GET}, etc. All HTTP
   * method annotations has annotation {@link HttpMethod}.
   * 
   * @param <T> annotation type
   * @param m method
   * @param annotation annotation class
   * @return list of annotation
   */
  protected <T extends Annotation> T getMetaAnnotation(Method m, Class<T> annotation) {
    for (Annotation a : m.getAnnotations()) {
      T endPoint = null;
      if ((endPoint = a.annotationType().getAnnotation(annotation)) != null)
        return endPoint;
    }
    return null;
  }

  /**
   * Tries to get JAX-RS annotation on method from the root resource class's
   * superclass or implemented interfaces.
   * 
   * @param <T> annotation type
   * @param method method for discovering
   * @param resourceClass class that contains discovered method
   * @param annotationClass annotation type what we are looking for
   * @param metaAnnotation false if annotation should be on method and true in
   *          method should contain annotations that has supplied annotation
   * @return annotation from class or its ancestor or null if nothing found
   */
  protected <T extends Annotation> T getMethodAnnotation(Method method,
                                                         Class<?> resourceClass,
                                                         Class<T> annotationClass,
                                                         boolean metaAnnotation) {

    T annotation = null;
    if (metaAnnotation)
      annotation = getMetaAnnotation(method, annotationClass);
    else
      annotation = method.getAnnotation(annotationClass);

    if (annotation == null) {

      Method inhMethod = null;

      try {
        inhMethod = resourceClass.getSuperclass().getMethod(method.getName(),
                                                            method.getParameterTypes());
      } catch (NoSuchMethodException e) {
        for (Class<?> intf : resourceClass.getInterfaces()) {
          try {

            Method tmp = intf.getMethod(method.getName(), method.getParameterTypes());
            if (inhMethod == null) {
              inhMethod = tmp;
            } else {
              String msg = "JAX-RS annotation on method " + inhMethod.getName() + " of resource "
                  + toString() + " is equivocality.";
              throw new RuntimeException(msg);
            }
          } catch (NoSuchMethodException exc) {
          }
        }
      }

      if (inhMethod != null) {
        if (metaAnnotation)
          annotation = getMetaAnnotation(inhMethod, annotationClass);
        else
          annotation = inhMethod.getAnnotation(annotationClass);
      }
    }

    return annotation;
  }
  
}
