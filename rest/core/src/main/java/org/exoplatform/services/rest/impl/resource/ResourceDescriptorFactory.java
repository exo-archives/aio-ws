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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.GET;
import javax.ws.rs.HttpMethod;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.impl.header.MediaTypeHelper;
import org.exoplatform.services.rest.impl.method.DefaultMethodInvoker;
import org.exoplatform.services.rest.impl.method.MethodParameter;
import org.exoplatform.services.rest.impl.method.MethodParameterHelper;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;
import org.exoplatform.services.rest.resource.ResourceMethodDescriptor;
import org.exoplatform.services.rest.resource.SubResourceLocatorDescriptor;
import org.exoplatform.services.rest.resource.SubResourceMethodDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ResourceDescriptorFactory {
  
  /**
   * Constructor. 
   */
  private ResourceDescriptorFactory() {
  }

  /**
   * Create instance of {@link AbstractResourceDescriptor} from given class.
   * 
   * @param resourceClass class
   * @return newly created AbstractResourceDescriptor
   */
  public static AbstractResourceDescriptor createAbstractResourceDescriptor(Class<?> resourceClass) {
    AbstractResourceDescriptor resourceDescriptor = null;
    final Path pathAnnotation = resourceClass.getAnnotation(Path.class);

    if (pathAnnotation != null) {
      PathValue path = new PathValue(pathAnnotation.value());
      resourceDescriptor = new AbstractResourceDescriptorImpl(path, resourceClass);
    } else {
      resourceDescriptor = new AbstractResourceDescriptorImpl(resourceClass);
    }

    final boolean encoded = resourceClass.getAnnotation(Encoded.class) != null;
    final Consumes consumesAnnotation = resourceClass.getAnnotation(Consumes.class);
    final Produces producesAnnotation = resourceClass.getAnnotation(Produces.class);

    List<MediaType> consumes = MediaTypeHelper.createConsumesList(consumesAnnotation);
    List<MediaType> produces = MediaTypeHelper.createProducesList(producesAnnotation);

    addResourceMethodDescriptor(resourceDescriptor,
                                createMethodList(resourceClass.getMethods()),
                                consumes,
                                produces,
                                encoded);
    addSubResourceMethodDescriptor(resourceDescriptor,
                                   createMethodList(resourceClass.getMethods()),
                                   consumes,
                                   produces,
                                   encoded);
    // NOTE Not process @Produces and @Consumes annotations for sub-locators.
    // According to specification:
    // @Produces and @Consumes annotations MAY be applied to a resource method,
    // a resource class or entity provider. Resource method MUST be annotated with
    // request method designator. Sub-locators has not this annotation.
    addSubResourceLocatordDescriptor(resourceDescriptor,
                                     createMethodList(resourceClass.getMethods()),
                                     encoded);

    return resourceDescriptor;
  }

  /**
   * Add {@link ResourceMethodDescriptor} to {@link AbstractResourceDescriptor}.
   * 
   * @param resourceDescriptor parent resource (class) for method resource, see
   *          {@link AbstractResourceDescriptor}
   * @param methods See {@link Class#getMethods()}
   * @param consumesFromParent List of consumes media types for parent
   * @param producesFromParent List of produces media types for parent
   * @param encodedFromParent true if automatic decoding of parameter values
   *          must be disable false otherwise. See {@link Encoded}
   */
  private static void addResourceMethodDescriptor(AbstractResourceDescriptor resourceDescriptor,
                                                        List<Method> methods,
                                                        List<MediaType> consumesFromParent,
                                                        List<MediaType> producesFromParent,
                                                        boolean encodedFromParent) {

    // keep only method with annotation which has annotation @HttpMethod, e. g.
    // @GET, @POST, etc
    retainWithMetaAnnotation(methods, HttpMethod.class);
    // remove all method with @Path annotation
    removeWithAnnotaion(methods, Path.class);

    for (Method method : methods) {
      String httpMethod = getMetaAnnotations(method, HttpMethod.class).get(0).value();
      List<MediaType> consumes = resolveConsumesMediaType(method, consumesFromParent);
      List<MediaType> produces = resolveProducesMediaType(method, producesFromParent);
      List<MethodParameter> methodParameters = createParametersList(method, encodedFromParent);
      ResourceMethodDescriptor resMethodDescriptor = new ResourceMethodDescriptorImpl(method,
                                                                                      httpMethod,
                                                                                      methodParameters,
                                                                                      resourceDescriptor,
                                                                                      consumes,
                                                                                      produces,
                                                                                      new DefaultMethodInvoker());
      resourceDescriptor.getResourceMethodDescriptors().add(resMethodDescriptor);
    }
  }

  /**
   * Add {@link SubResourceMethodDescriptor} to
   * {@link AbstractResourceDescriptor}.
   * 
   * @param resourceDescriptor parent resource (class) for method sub-resource,
   *          see {@link AbstractResourceDescriptor}
   * @param methods See {@link Class#getMethods()}
   * @param consumesFromParent List of consumes media types for parent
   * @param producesFromParent List of produces media types for parent
   * @param encodedFromParent true if automatic decoding of parameter values
   *          must be disable false otherwise. See {@link Encoded}
   */
  private static void addSubResourceMethodDescriptor(AbstractResourceDescriptor resourceDescriptor,
                                                           List<Method> methods,
                                                           List<MediaType> consumesFromParent,
                                                           List<MediaType> producesFromParent,
                                                           boolean encodedFromParent) {

    // keep only method with annotation which has annotation @HttpMethod, e. g.
    // @GET, @POST, etc
    retainWithMetaAnnotation(methods, HttpMethod.class);
    // keep only method with @Path annotation
    retainWithAnnotation(methods, Path.class);

    for (Method method : methods) {
      String httpMethod = getMetaAnnotations(method, HttpMethod.class).get(0).value();
      List<MediaType> consumes = resolveConsumesMediaType(method, consumesFromParent);
      List<MediaType> produces = resolveProducesMediaType(method, producesFromParent);
      List<MethodParameter> methodParameters = createParametersList(method, encodedFromParent);
      Path p = method.getAnnotation(Path.class);
      PathValue pathValue = new PathValue(p.value());
      SubResourceMethodDescriptor subresMethodDescriptor = new SubResourceMethodDescriptorImpl(pathValue,
                                                                                               method,
                                                                                               httpMethod,
                                                                                               methodParameters,
                                                                                               resourceDescriptor,
                                                                                               consumes,
                                                                                               produces,
                                                                                               new DefaultMethodInvoker());
      resourceDescriptor.getSubResourceMethodDescriptors().add(subresMethodDescriptor);
    }
  }

  /**
   * Add {@link SubResourceLocatorDescriptor} to
   * {@link AbstractResourceDescriptor}.
   * 
   * @param resourceDescriptor parent resource (class) for method resource, see
   *          {@link AbstractResourceDescriptor}
   * @param methods See {@link Class#getMethods()}
   * @param encodedFromParent true if automatic decoding of parameter values
   *          must be disable false otherwise. See {@link Encoded}
   */
  private static void addSubResourceLocatordDescriptor(AbstractResourceDescriptor resourceDescriptor,
                                                             List<Method> methods,
                                                             boolean encodedFromParent) {

    // keep only method with @Path annotation
    retainWithAnnotation(methods, Path.class);
    // remove all method with annotation which has annotation @HttpMethod, e. g.
    // @GET, @POST, etc
    removeWithMetaAnnotation(methods, HttpMethod.class);

    for (Method method : methods) {
      List<MethodParameter> methodParameters = createParametersList(method, encodedFromParent);
      Path p = method.getAnnotation(Path.class);
      PathValue pathValue = new PathValue(p.value());
      SubResourceLocatorDescriptor subresLocatorDescriptor = new SubResourceLocatorDescriptorImpl(pathValue,
                                                                                                  method,
                                                                                                  methodParameters,
                                                                                                  resourceDescriptor,
                                                                                                  new DefaultMethodInvoker());
      resourceDescriptor.getSubResourceLocatorDescriptors().add(subresLocatorDescriptor);
    }
  }

  /**
   * Create list of methods.
   * 
   * @param methods array of {@link Method}
   * @return list of {@link Method}
   */
  private static List<Method> createMethodList(Method[] methods) {
    List<Method> l = new ArrayList<Method>(methods.length);
    for (Method m : methods)
      l.add(m);

    return l;
  }

  /**
   * Create list of {@link MethodParameter} .
   * 
   * @param m See {@link Method}
   * @param encodedFromParent true if automatic decoding of parameter values
   *          must e disable false otherwise. See {@link Encoded}
   * @return list of {@link MethodParameter}
   */
  @SuppressWarnings("unchecked")
  private static List<MethodParameter> createParametersList(Method m, boolean encodedFromParent) {
    List<MethodParameter> l = new ArrayList<MethodParameter>();
    Class[] parameterClasses = m.getParameterTypes();
    Type[] parameterGenTypes = m.getGenericParameterTypes();
    Annotation[][] annotations = m.getParameterAnnotations();

    for (int i = 0; i < parameterClasses.length; i++) {
      l.add(createMethodParameter(parameterClasses[i],
                                  parameterGenTypes[i],
                                  annotations[i],
                                  encodedFromParent || m.getAnnotation(Encoded.class) != null));
    }

    return l;
  }

  /**
   * Create method parameter, see {@link MethodParameter} .
   * 
   * @param parameterClass parameter's class
   * @param parameterGenType generic parameter type
   * @param annotations all annotation for this parameter, see
   *          {@link Method#getParameterAnnotations()}
   * @param encodedFromParent true if automatic decoding of parameter values
   *          must e disable false otherwise. See {@link Encoded}
   * @return newly created method parameter
   */
  @SuppressWarnings("unchecked")
  private static MethodParameter createMethodParameter(Class parameterClass,
                                                       Type parameterGenType,
                                                       Annotation[] annotations,
                                                       boolean encodedFromParent) {
    String defaultValue = null;
    Annotation annotation = null;
    boolean encoded = false;
    for (Annotation a : annotations) {
      // TODO check if few annotation at one parameter, e. g. PathParam and HeaderParam
      if (MethodParameterHelper.PARAMETER_ANNOTATIONS_MAP.containsKey(a.annotationType().getName())) {
        annotation = a;
      } else if (a.annotationType() == Encoded.class) {
        encoded = true;
      } else if (a.annotationType() == DefaultValue.class) {
        defaultValue = ((DefaultValue) a).value();
      }
    }

    return new MethodParameter(annotation,
                               annotations,
                               parameterClass,
                               parameterGenType,
                               defaultValue,
                               encoded || encodedFromParent);
  }

  /**
   * Check does method contains annotation {@link Produces}. If it has then
   * process it and create list of media types method can produce. If it has not
   * annotation then return media types from parent resource (from resource
   * class).
   * 
   * @param m See {@link Method}
   * @param producesFromParent media types from from resource class
   * @return media types actual method <i>m</i> which it can produce
   */
  private static List<MediaType> resolveProducesMediaType(Method m,
                                                          List<MediaType> producesFromParent) {
    Annotation a = m.getAnnotation(Produces.class);
    if (a != null)
      return MediaTypeHelper.createProducesList((Produces) a);

    return producesFromParent;
  }

  /**
   * Check does method contains annotation {@link Consumes}. If it has then
   * process it and create list of media types method can consume. If it has not
   * annotation then return media types from parent resource (from resource
   * class).
   * 
   * @param m See {@link Method}
   * @param consumesFromParent media types from from resource class
   * @return media types for method <i>m</i> which it can consume
   */
  private static List<MediaType> resolveConsumesMediaType(Method m,
                                                          List<MediaType> consumesFromParent) {
    Annotation a = m.getAnnotation(Consumes.class);
    if (a != null)
      return MediaTypeHelper.createConsumesList((Consumes) a);

    return consumesFromParent;
  }

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
  private static <T extends Annotation> List<T> getMetaAnnotations(Method m, Class<T> annotation) {
    Annotation[] as = m.getAnnotations();
    List<T> l = new ArrayList<T>();
    for (Annotation a : as)
      if (a.annotationType().getAnnotation(annotation) != null)
        l.add(a.annotationType().getAnnotation(annotation));

    return l;
  }

  /**
   * @param <T> annotation type
   * @param m See {@link Method}
   * @param annotation annotation class
   * @return true if method has annotation which has annotation
   *         <i>annotation</i>
   */
  private static <T extends Annotation> boolean hasMetaAnnotation(Method m, Class<T> annotation) {
    Annotation[] as = m.getAnnotations();
    for (Annotation a : as)
      if (a.annotationType().getAnnotation(annotation) != null)
        return true;

    return false;
  }

  /**
   * Remove all method with annotation <i>annotation</i>.
   * 
   * @param <T> annotation type
   * @param ms methods list
   * @param annotation annotation class
   */
  private static <T extends Annotation> void removeWithAnnotaion(List<Method> ms,
                                                                 Class<T> annotation) {
    Iterator<Method> i = ms.iterator();
    while (i.hasNext()) {
      Method m = i.next();
      if (m.getAnnotation(annotation) != null)
        i.remove();
    }
  }

  /**
   * Remove all method with at least one annotation which has annotation
   * <i>annotation</i>. It is useful for annotation {@link GET}, etc. All HTTP
   * method annotations has annotation {@link HttpMethod}.
   * 
   * @param <T> annotation type
   * @param ms methods list
   * @param annotation annotation class
   */
  private static <T extends Annotation> void removeWithMetaAnnotation(List<Method> ms,
                                                                      Class<T> annotation) {
    Iterator<Method> i = ms.iterator();
    while (i.hasNext()) {
      Method m = i.next();
      if (hasMetaAnnotation(m, annotation))
        i.remove();
    }
  }

  /**
   * Remove all method without annotation <i>annotation</i>.
   * 
   * @param <T> annotation type
   * @param ms methods list
   * @param annotation annotation class
   */
  private static <T extends Annotation> void retainWithAnnotation(List<Method> ms,
                                                                  Class<T> annotation) {
    Iterator<Method> i = ms.iterator();
    while (i.hasNext()) {
      Method m = i.next();
      if (m.getAnnotation(annotation) == null)
        i.remove();
    }
  }

  /**
   * Remove all method without any annotation which has annotation
   * <i>annotation</i>. It is useful for annotation {@link GET}, etc. All HTTP
   * method annotations has annotation {@link HttpMethod}.
   * 
   * @param <T> annotation type
   * @param ms methods list
   * @param annotation annotation class
   */
  private static <T extends Annotation> void retainWithMetaAnnotation(List<Method> ms,
                                                                      Class<T> annotation) {
    Iterator<Method> i = ms.iterator();
    while (i.hasNext()) {
      Method m = i.next();
      if (!hasMetaAnnotation(m, annotation))
        i.remove();
    }
  }

}
