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
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Comparator;
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
import org.exoplatform.services.rest.impl.method.ConstructorDescriptorImpl;
import org.exoplatform.services.rest.impl.method.ConstructorParameterImpl;
import org.exoplatform.services.rest.impl.method.DefaultMethodInvoker;
import org.exoplatform.services.rest.impl.method.MethodParameterHelper;
import org.exoplatform.services.rest.impl.method.MethodParameterImpl;
import org.exoplatform.services.rest.method.ConstructorDescriptor;
import org.exoplatform.services.rest.method.ConstructorParameter;
import org.exoplatform.services.rest.method.MethodParameter;
import org.exoplatform.services.rest.resource.AbstractResourceDescriptor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ResourceDescriptorFactory {

  private static final Log LOG = ExoLogger.getLogger(ResourceDescriptorFactory.class.getName());

  /**
   * Constructor.
   */
  private ResourceDescriptorFactory() {
  }
  
  /**
   * Compare two ConstructorDescriptor in number parameters order. 
   */
  private static final Comparator<ConstructorDescriptor> CONSTRUCTOR_COMPARATOR =
    new Comparator<ConstructorDescriptor>() {
      public int compare(ConstructorDescriptor o1, ConstructorDescriptor o2) {
        int r = o2.getConstructorParameters().size() - o1.getConstructorParameters().size();
        if (r == 0)
          LOG.warn("Two constructors with the same number of parameter found "
                   + o1.getConstructor().getName() + " and "
                   + o2.getConstructor().getName());
        return r;
      }
  };
  
  /**
   * Create instance of {@link AbstractResourceDescriptor} from given class.
   * 
   * @param resourceClass class
   * @return newly created AbstractResourceDescriptor
   */
  public static AbstractResourceDescriptor createAbstractResourceDescriptor(Class<?> resourceClass) {

    for (Method method : resourceClass.getDeclaredMethods()) {
      for (Annotation a : method.getAnnotations()) {
        Class<?> ac = a.annotationType();
        if (!Modifier.isPublic(method.getModifiers())
            && (ac == CookieParam.class || ac == Consumes.class || ac == Context.class
                || ac == DefaultValue.class || ac == Encoded.class || ac == FormParam.class
                || ac == HeaderParam.class || ac == MatrixParam.class || ac == Path.class
                || ac == PathParam.class || ac == Produces.class || ac == QueryParam.class
                || ac.getAnnotation(HttpMethod.class) != null)) {

            LOG.warn("Non-public method is annotated with JAX-RS annotation: "
                + resourceClass.getName() + "#" + method.getName());
        }
      }
    }

    final Path pathAnnotation = getClassAnnotation(resourceClass, Path.class);
    AbstractResourceDescriptor resourceDescriptor = null;
    if (pathAnnotation != null)
      resourceDescriptor = new AbstractResourceDescriptorImpl(new PathValue(pathAnnotation.value()),
                                                              resourceClass);
    else
      resourceDescriptor = new AbstractResourceDescriptorImpl(resourceClass);

    final boolean encoded = resourceClass.getAnnotation(Encoded.class) != null;
    final Consumes consumesResource = resourceClass.getAnnotation(Consumes.class);
    final Produces producesResource = resourceClass.getAnnotation(Produces.class);
    
    Constructor<?>[] constructors = resourceClass.getConstructors();
    for (Constructor<?> constructor : constructors)
      resourceDescriptor.getConstructorDescriptor().add(createConstructorDescriptors(constructor,
                                                                                     encoded));

    if (resourceDescriptor.getConstructorDescriptor().size() > 1)
      java.util.Collections.sort(resourceDescriptor.getConstructorDescriptor(),
                                 CONSTRUCTOR_COMPARATOR);


    for (Method method : createMethodList(resourceClass)) {
      Path path = getMethodAnnotation(method, resourceClass, Path.class, false);
      HttpMethod httpMethod = getMethodAnnotation(method, resourceClass, HttpMethod.class, true);
      if (path != null || httpMethod != null) {
        List<MethodParameter> methodParameters = createMethodParametersList(resourceClass,
                                                                      method,
                                                                      encoded);
        if (httpMethod != null) {
          List<MediaType> consumes = resolveConsumesMediaType(resourceClass,
                                                              method,
                                                              MediaTypeHelper.createConsumesList(consumesResource));
          List<MediaType> produces = resolveProducesMediaType(resourceClass,
                                                              method,
                                                              MediaTypeHelper.createProducesList(producesResource));

          if (path == null) {
            // resource method
            resourceDescriptor.getResourceMethodDescriptors()
                              .add(new ResourceMethodDescriptorImpl(method,
                                                                    httpMethod.value(),
                                                                    methodParameters,
                                                                    resourceDescriptor,
                                                                    consumes,
                                                                    produces,
                                                                    new DefaultMethodInvoker()));
          } else {
            // sub-resource method
            resourceDescriptor.getSubResourceMethodDescriptors()
                              .add(new SubResourceMethodDescriptorImpl(new PathValue(path.value()),
                                                                       method,
                                                                       httpMethod.value(),
                                                                       methodParameters,
                                                                       resourceDescriptor,
                                                                       consumes,
                                                                       produces,
                                                                       new DefaultMethodInvoker()));
          }
        } else {
          if (path != null) {
            // sub-resource locator

            // NOTE Not process @Produces and @Consumes annotations for
            // sub-locators.
            // According to specification:
            // @Produces and @Consumes annotations MAY be applied to a resource
            // method, a resource class or entity provider. Resource method
            // MUST be annotated with request method designator.
            // Sub-locators has not this annotation.
            resourceDescriptor.getSubResourceLocatorDescriptors()
                              .add(new SubResourceLocatorDescriptorImpl(new PathValue(path.value()),
                                                                        method,
                                                                        methodParameters,
                                                                        resourceDescriptor,
                                                                        new DefaultMethodInvoker()));
          }
        }
      }
    }

    return resourceDescriptor;
  }

  /**
   * Create list of methods.
   * 
   * @param methods array of {@link Method}
   * @return list of {@link Method}
   */
  private static List<Method> createMethodList(Class<?> resourceClass) {
    Method[] tmp = resourceClass.getMethods();
    List<Method> l = new ArrayList<Method>(tmp.length);
    for (Method m : tmp)
      l.add(m);
    return l;
  }
  
  private static ConstructorDescriptor createConstructorDescriptors(Constructor<?> constructor,
                                                                    boolean encodedFromParent) {
    Class<?>[] parameterTypes = constructor.getParameterTypes();
    
    if (parameterTypes.length == 0)
      return new ConstructorDescriptorImpl(constructor);
    
    Type[] parameterGenericTypes = constructor.getGenericParameterTypes();
    Annotation[][] annotations = constructor.getParameterAnnotations();
    List<ConstructorParameter> parameters = new ArrayList<ConstructorParameter>(parameterTypes.length);
    for (int i = 0; i < parameterTypes.length; i++)
      parameters.add(createConstructorParameter(parameterTypes[i],
                                                parameterGenericTypes[i],
                                                annotations[i],
                                                encodedFromParent));
    
    return new ConstructorDescriptorImpl(constructor, parameters);
  }
    

  /**
   * Create list of {@link MethodParameter} . FIXME
   * 
   * @param m See {@link Method}
   * @param encodedFromParent true if automatic decoding of parameter values
   *          must e disable false otherwise. See {@link Encoded}
   * @return list of {@link MethodParameter}
   */
  private static List<MethodParameter> createMethodParametersList(Class<?> resourceClass,
                                                                  Method m,
                                                                  boolean encodedFromParent) {
    Class<?>[] parameterClasses = m.getParameterTypes();
    Type[] parameterGenTypes = m.getGenericParameterTypes();
    Annotation[][] annotations = m.getParameterAnnotations();

    List<MethodParameter> l = new ArrayList<MethodParameter>(parameterClasses.length);
    for (int i = 0; i < parameterClasses.length; i++) {
      l.add(createMethodParameter(parameterClasses[i],
                                  parameterGenTypes[i],
                                  annotations[i],
                                  encodedFromParent || m.getAnnotation(Encoded.class) != null));
    }

    return l;
  }

  private static ConstructorParameter createConstructorParameter(Class<?> clazz,
                                                                 Type type,
                                                                 Annotation[] annotations,
                                                                 boolean encodedFromParent) {
    String defaultValue = null;
    Annotation annotation = null;
    boolean encoded = false;
    for (Annotation a : annotations) {
      Class<?> ac = a.annotationType();
      if (MethodParameterHelper.CONSTRUCTOR_PARAMETER_ANNOTATIONS_MAP.containsKey(ac.getName())) {
        annotation = a;
      } else if (ac == Encoded.class) {
        encoded = true;
      } else if (ac == DefaultValue.class) {
        defaultValue = ((DefaultValue) a).value();
      }
    }
    return new ConstructorParameterImpl(annotation, annotations, clazz, type, defaultValue, encoded
        | encodedFromParent);
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
  private static MethodParameter createMethodParameter(Class<?> parameterClass,
                                                       Type parameterGenType,
                                                       Annotation[] annotations,
                                                       boolean encodedFromParent) {
    String defaultValue = null;
    Annotation annotation = null;
    boolean encoded = false;
    for (Annotation a : annotations) {
      // TODO check if few annotation at one parameter, e. g. PathParam and
      // HeaderParam
      Class<?> ac = a.annotationType();
      if (MethodParameterHelper.PARAMETER_ANNOTATIONS_MAP.containsKey(ac.getName())) {
        annotation = a;
      } else if (ac == Encoded.class) {
        encoded = true;
      } else if (ac == DefaultValue.class) {
        defaultValue = ((DefaultValue) a).value();
      }
    }

    return new MethodParameterImpl(annotation,
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
   * class). FIXME
   * 
   * @param m See {@link Method}
   * @param producesFromParent media types from from resource class
   * @return media types actual method <i>m</i> which it can produce
   */
  private static List<MediaType> resolveProducesMediaType(Class<?> resourceClass,
                                                          Method method,
                                                          List<MediaType> producesFromParent) {
    Produces p = getMethodAnnotation(method, resourceClass, Produces.class, false);
    if (p != null)
      return MediaTypeHelper.createProducesList(p);

    return producesFromParent;
  }

  /**
   * Check does method contains annotation {@link Consumes}. If it has then
   * process it and create list of media types method can consume. If it has not
   * annotation then return media types from parent resource (from resource
   * class). FIXME
   * 
   * @param m See {@link Method}
   * @param consumesFromParent media types from from resource class
   * @return media types for method <i>m</i> which it can consume
   */
  private static List<MediaType> resolveConsumesMediaType(Class<?> resourceClass,
                                                          Method method,
                                                          List<MediaType> consumesFromParent) {
    Consumes c = getMethodAnnotation(method, resourceClass, Consumes.class, false);
    if (c != null)
      return MediaTypeHelper.createConsumesList(c);

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
  private static <T extends Annotation> T getMetaAnnotation(Method m, Class<T> annotation) {
    for (Annotation a : m.getAnnotations()) {
      T endPoint = null;
      if ((endPoint = a.annotationType().getAnnotation(annotation)) != null)
        return endPoint;
    }
    return null;
  }

  /**
   * FIXME Tries to get JAX-RS annotated method from the root resource class's
   * superclass or implemented interfaces.
   * 
   * @param method
   * @param resourceClass
   * @return
   */
  private static <T extends Annotation> T getMethodAnnotation(Method method,
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
            if (inhMethod == null)
              inhMethod = tmp;
            else
              throw new RuntimeException("JAX-RS annotation on method " + resourceClass.getName()
                  + "#" + inhMethod.getName() + " is equivocality.");
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
  
  private static <T extends Annotation> T getClassAnnotation(Class<?> resourceClass,
                                                             Class<T> annotationClass) {
    T annotation = resourceClass.getAnnotation(annotationClass);
    if (annotation == null) {
      annotation = resourceClass.getSuperclass().getAnnotation(annotationClass);
      if (annotation == null) {
        for (Class<?> intf : resourceClass.getInterfaces()) {
          T tmp = intf.getAnnotation(annotationClass);
          if (annotation == null)
            annotation = tmp;
          else
            throw new RuntimeException("JAX-RS annotation on class " + resourceClass.getName()
                + " is equivocality.");
        }
      }
    }
    return annotation;
  }

}
