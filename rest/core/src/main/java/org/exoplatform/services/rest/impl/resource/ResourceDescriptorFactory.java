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
import java.util.Iterator;
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

  private static final Log LOG = ExoLogger.getLogger(ResourceDescriptorFactory.class);

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

    Method[] ms = resourceClass.getDeclaredMethods();
    for (Method method : ms) {
      if (!getJaxRSAnnotations(method).isEmpty() && !Modifier.isPublic(method.getModifiers())) {
        LOG.warn("Non-public method annotated with JAX-RS annotation: " + resourceClass.getName()
            + "." + method.getName());
      }
    }

    if (pathAnnotation != null) {
      PathValue path = new PathValue(pathAnnotation.value());
      resourceDescriptor = new AbstractResourceDescriptorImpl(path, resourceClass);
    } else {
      resourceDescriptor = new AbstractResourceDescriptorImpl(resourceClass);
    }

    boolean inheritAnnotations = needsAnnotationsInheritance(resourceClass);

    final boolean encoded = !inheritAnnotations ? resourceClass.getAnnotation(Encoded.class) != null
                                               : getInheritedAnnotation(Encoded.class,
                                                                        resourceClass) != null;

    final Consumes consumesAnnotation = !inheritAnnotations ? resourceClass.getAnnotation(Consumes.class)
                                                           : (Consumes) getInheritedAnnotation(Consumes.class,
                                                                                               resourceClass);

    final Produces producesAnnotation = !inheritAnnotations ? resourceClass.getAnnotation(Produces.class)
                                                           : (Produces) getInheritedAnnotation(Produces.class,
                                                                                               resourceClass);

    List<MediaType> consumes = MediaTypeHelper.createConsumesList(consumesAnnotation);
    List<MediaType> produces = MediaTypeHelper.createProducesList(producesAnnotation);

    addResourceMethodDescriptor(resourceDescriptor,
                                createMethodList(resourceClass, inheritAnnotations),
                                consumes,
                                produces,
                                encoded,
                                inheritAnnotations);
    addSubResourceMethodDescriptor(resourceDescriptor,
                                   createMethodList(resourceClass, inheritAnnotations),
                                   consumes,
                                   produces,
                                   encoded,
                                   inheritAnnotations);
    // NOTE Not process @Produces and @Consumes annotations for sub-locators.
    // According to specification:
    // @Produces and @Consumes annotations MAY be applied to a resource method,
    // a resource class or entity provider. Resource method MUST be annotated
    // with
    // request method designator. Sub-locators has not this annotation.
    addSubResourceLocatordDescriptor(resourceDescriptor,
                                     createMethodList(resourceClass, inheritAnnotations),
                                     encoded,
                                     inheritAnnotations);

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
                                                  boolean encodedFromParent,
                                                  boolean inheritAnnotations) {

    // keep only method with annotation which has annotation @HttpMethod, e. g.
    // @GET, @POST, etc
    retainWithMetaAnnotation(methods, HttpMethod.class);
    // remove all method with @Path annotation
    removeWithAnnotaion(methods, Path.class);

    for (Method method : methods) {

      String httpMethod = getMetaAnnotations(method, HttpMethod.class).get(0).value();
      List<MediaType> consumes = resolveConsumesMediaType(method, consumesFromParent);
      List<MediaType> produces = resolveProducesMediaType(method, producesFromParent);
      List<MethodParameter> methodParameters = createParametersList(method,
                                                                    encodedFromParent);
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
                                                     boolean encodedFromParent,
                                                     boolean inheritAnnotations) {

    // keep only method with annotation which has annotation @HttpMethod, e. g.
    // @GET, @POST, etc
    retainWithMetaAnnotation(methods, HttpMethod.class);
    // keep only method with @Path annotation
    retainWithAnnotation(methods, Path.class);

    for (Method method : methods) {

      String httpMethod = getMetaAnnotations(method, HttpMethod.class).get(0).value();
      List<MediaType> consumes = resolveConsumesMediaType(method, consumesFromParent);
      List<MediaType> produces = resolveProducesMediaType(method, producesFromParent);
      List<MethodParameter> methodParameters = createParametersList(method,
                                                                    encodedFromParent);
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
                                                       boolean encodedFromParent,
                                                       boolean inheritAnnotations) {

    // keep only method with @Path annotation
    retainWithAnnotation(methods, Path.class);
    // remove all method with annotation which has annotation @HttpMethod, e. g.
    // @GET, @POST, etc
    removeWithMetaAnnotation(methods, HttpMethod.class);

    for (Method method : methods) {

      List<MethodParameter> methodParameters = createParametersList(method,
                                                                    encodedFromParent);
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
  private static List<Method> createMethodList(Class<?> resourceClass, boolean inheritAnnotanions) {
    List<Method> l = new ArrayList<Method>(resourceClass.getMethods().length);
    for (Method m : resourceClass.getMethods())
      if (inheritAnnotanions && getJaxRSAnnotations(m).isEmpty()
          && (getAnnotatedMethod(m, resourceClass) != null)) {
        l.add(getAnnotatedMethod(m, resourceClass));
      } else {
        l.add(m);
      }

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
      // TODO check if few annotation at one parameter, e. g. PathParam and
      // HeaderParam
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

  /**
   * Check if the resource class is annotated with any JAX-RS annotation except
   * 
   * @Path. If retunrs false then resource can inherit JAX-RS annotations from
   *        the superclass or an implemented interfaces.
   * @param resourceClass
   * @return
   */
  private static boolean needsAnnotationsInheritance(Class<?> resourceClass) {
    if ((resourceClass.getDeclaredAnnotations().length == 0)
        || (resourceClass.getDeclaredAnnotations().length == 1)
        && (resourceClass.isAnnotationPresent(Path.class))) {
      return true;
    } else {
      return false;
    }
  }

  /**
   * Returns ArrayList including all JAX-RS annotations present on the method.
   * 
   * @param method
   * @return
   */
  private static ArrayList<Annotation> getJaxRSAnnotations(Method method) {
    ArrayList<Annotation> annotationsList = new ArrayList<Annotation>();

    annotationsList.addAll(getMetaAnnotations(method, HttpMethod.class));

    if (method.getAnnotation(Consumes.class) != null) {
      annotationsList.add(method.getAnnotation(Consumes.class));
    }

    if (method.getAnnotation(Produces.class) != null) {
      annotationsList.add(method.getAnnotation(Produces.class));
    }

    if (method.getAnnotation(PathParam.class) != null) {
      annotationsList.add(method.getAnnotation(PathParam.class));
    }

    if (method.getAnnotation(QueryParam.class) != null) {
      annotationsList.add(method.getAnnotation(QueryParam.class));
    }

    if (method.getAnnotation(FormParam.class) != null) {
      annotationsList.add(method.getAnnotation(FormParam.class));
    }

    if (method.getAnnotation(MatrixParam.class) != null) {
      annotationsList.add(method.getAnnotation(MatrixParam.class));
    }

    if (method.getAnnotation(CookieParam.class) != null) {
      annotationsList.add(method.getAnnotation(CookieParam.class));
    }

    if (method.getAnnotation(HeaderParam.class) != null) {
      annotationsList.add(method.getAnnotation(HeaderParam.class));
    }

    if (method.getAnnotation(Encoded.class) != null) {
      annotationsList.add(method.getAnnotation(Encoded.class));
    }

    if (method.getAnnotation(DefaultValue.class) != null) {
      annotationsList.add(method.getAnnotation(DefaultValue.class));
    }

    if (method.getAnnotation(Context.class) != null) {
      annotationsList.add(method.getAnnotation(Context.class));
    }
    return annotationsList;
  }

  /**
   * Returns specified inherited annotation from the superclass or an
   * implemented interface.
   * 
   * @param annotation
   * @param resourceClass
   * @return
   */
  private static <T extends Annotation> Annotation getInheritedAnnotation(Class<T> annotation,
                                                                          Class<?> resourceClass) {
    Annotation anno = null;
    anno = resourceClass.getSuperclass().getAnnotation(annotation);

    if (anno == null) {
      for (Class<?> interfase : resourceClass.getInterfaces()) {
        anno = interfase.getAnnotation(annotation);
        if (anno != null) {
          return anno;
        }
      }
    }
    return anno;
  }

  /**
   * Tries to get JAX-RS annotated method from the root resourse class's
   * superclass or implemented interfaces.
   * 
   * @param method
   * @param resourceClass
   * @return
   */
  private static Method getAnnotatedMethod(Method method, Class<?> resourceClass) {
    Method annotatedMethod = null;
    Class<?> superClazz = resourceClass.getSuperclass();

    try {
      annotatedMethod = superClazz.getMethod(method.getName(), method.getParameterTypes());
    } catch (NoSuchMethodException e) {
      for (Class<?> interfase : resourceClass.getInterfaces()) {
        try {

          annotatedMethod = interfase.getDeclaredMethod(method.getName(),
                                                        method.getParameterTypes());

          Annotation[] ann = annotatedMethod.getAnnotations();
          
          return annotatedMethod;
        } catch (NoSuchMethodException exc) {
          return null;
        }
      }
    }
    return annotatedMethod;
  }

}
