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

package org.exoplatform.services.rest.impl.method;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import org.exoplatform.services.rest.impl.AnnotationUtils;
import org.exoplatform.services.rest.method.TypeProducer;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class MethodParameterHelper {

  /**
   * Mapping for JAX-RS annotations (that can be used as method parameter
   * annotations) class names to members of
   * {@link AnnotationUtils.PARAMETER_ANNOTATIONS}.
   */
  public static final Map<String, AnnotationUtils.PARAMETER_ANNOTATIONS> PARAMETER_ANNOTATIONS_MAP;

  /**
   * Mapping for JAX-RS annotations (that can be used as constructor parameter
   * annotations) class names to members of
   * {@link AnnotationUtils.PARAMETER_ANNOTATIONS}.
   */
  public static final Map<String, AnnotationUtils.PARAMETER_ANNOTATIONS> CONSTRUCTOR_PARAMETER_ANNOTATIONS_MAP;

  static {
    Map<String, AnnotationUtils.PARAMETER_ANNOTATIONS> m1 = new HashMap<String, AnnotationUtils.PARAMETER_ANNOTATIONS>(6);
    m1.put(javax.ws.rs.CookieParam.class.getName(), AnnotationUtils.PARAMETER_ANNOTATIONS.COOKIE_PARAM);
    m1.put(javax.ws.rs.core.Context.class.getName(), AnnotationUtils.PARAMETER_ANNOTATIONS.CONTEXT);
    m1.put(javax.ws.rs.HeaderParam.class.getName(), AnnotationUtils.PARAMETER_ANNOTATIONS.HEADER_PARAM);
    m1.put(javax.ws.rs.MatrixParam.class.getName(), AnnotationUtils.PARAMETER_ANNOTATIONS.MATRIX_PARAM);
    m1.put(javax.ws.rs.PathParam.class.getName(), AnnotationUtils.PARAMETER_ANNOTATIONS.PATH_PARAM);
    m1.put(javax.ws.rs.QueryParam.class.getName(), AnnotationUtils.PARAMETER_ANNOTATIONS.QUERY_PARAM);
    PARAMETER_ANNOTATIONS_MAP = Collections.unmodifiableMap(m1);
    
    Map<String, AnnotationUtils.PARAMETER_ANNOTATIONS> m2 = new HashMap<String, AnnotationUtils.PARAMETER_ANNOTATIONS>(m1);
    m2.put(javax.ws.rs.FormParam.class.getName(), AnnotationUtils.PARAMETER_ANNOTATIONS.FORM_PARAM);
    CONSTRUCTOR_PARAMETER_ANNOTATIONS_MAP = Collections.unmodifiableMap(m2);
  }

  /**
   * @param parameterClass method parameter class
   * @param parameterType method parameter type
   * @return TypeProducer
   * @see TypeProducer
   * @see Method#getParameterTypes()
   * @see Method#getGenericParameterTypes()
   */
  static TypeProducer createTypeProducer(Class<?> parameterClass, Type parameterType) {

    if (parameterClass == List.class || parameterClass == Set.class
        || parameterClass == SortedSet.class) {
      // parameter is collection

      Class<?> clazz = getGenericType(parameterType);
      Method methodValueOf = null;
      Constructor<?> constructor = null;

      // if not parameterized then by default collection of Strings.
      if (clazz == String.class || clazz == null) {
        // String

        return new CollectionStringProducer(parameterClass);

      } else if ((methodValueOf = getStringValueOfMethod(clazz)) != null) {
        // static method valueOf

        return new CollectionStringValueOfProducer(parameterClass, methodValueOf);

      } else if ((constructor = getStringConstructor(clazz)) != null) {
        // constructor with String

        return new CollectionStringConstructorProducer(parameterClass, constructor);

      }

    } else {
      // parameters is not collection
      Method methodValueOf = null;
      Constructor<?> constructor = null;

      if (parameterClass.isPrimitive()) {
        // primitive type

        return new PrimitiveTypeProducer(parameterClass);

      } else if (parameterClass == String.class) {
        // String

        return new StringProducer();

      } else if ((methodValueOf = getStringValueOfMethod(parameterClass)) != null) {
        // static valueOf method

        return new StringValueOfProducer(methodValueOf);

      } else if ((constructor = getStringConstructor(parameterClass)) != null) {
        // constructor with String

        return new StringConstructorProducer(constructor);

      }
    }

    return null;
  }

  /**
   * The type <code>T</code> of the annotated parameter, field or property must
   * either:
   * <ol>
   * <li>Be a primitive type</li>
   * <li>Have a constructor that accepts a single <code>String</code> argument</li>
   * <li>Have a static method named <code>valueOf</code> that accepts a single
   * <code>String</code> argument (see, for example,
   * {@link Integer#valueOf(String)})</li>
   * <li>Be <code>List&lt;T&gt;</code>, <code>Set&lt;T&gt;</code> or
   * <code>SortedSet&lt;T&gt;</code>, where <code>T</code> satisfies 2 or 3
   * above. The resulting collection is read-only.</li>
   * </ol>
   * 
   * @param parameterClass the parameter class
   * @param parameterType the parameter type
   * @param parameterAnnotation parameter annotation
   * @return true it parameter is valid, false otherwise
   * @see AnnotationUtils.PARAMETER_ANNOTATIONS
   */
  boolean isValidAnnotatedParameter(Class<?> parameterClass,
                                    Type parameterType,
                                    AnnotationUtils.PARAMETER_ANNOTATIONS parameterAnnotation) {
    if (parameterClass == List.class || parameterClass == Set.class
        || parameterClass == SortedSet.class) {

      // PathParam cann't be used on collection
      if (parameterAnnotation == AnnotationUtils.PARAMETER_ANNOTATIONS.PATH_PARAM)
        return false;

      Class<?> clazz = getGenericType(parameterType);

      if (clazz == null || clazz == String.class || getStringValueOfMethod(clazz) != null
          || getStringConstructor(clazz) != null) {

        // parameter is collection (List, Set or SortedSet)
        return true;

      } else {

        // if primitive type
        if (parameterClass.isPrimitive()
            && PrimitiveTypeProducer.PRIMITIVE_TYPES_MAP.get(parameterClass) != null)
          return true;

        if (parameterClass == String.class || getStringValueOfMethod(parameterClass) != null
            || getStringConstructor(parameterClass) != null)
          return true;

      }
    }

    // not valid parameter.
    return false;
  }

  /**
   * Get static {@link Method} with single string argument and name 'valueOf'
   * for supplied class.
   * 
   * @param clazz class for discovering to have public static method with name
   *          'valueOf' and single string argument
   * @return valueOf method or null if class has not it
   */
  static Method getStringValueOfMethod(Class<?> clazz) {
    try {
      Method method = clazz.getDeclaredMethod("valueOf", String.class);
      return Modifier.isStatic(method.getModifiers()) ? method : null;
    } catch (Exception e) {
      return null;
    }
  }

  /**
   * Get constructor with single string argument for supplied class.
   * 
   * @param clazz class for discovering to have constructor with single string
   *          argument
   * @return constructor or null if class has not constructor with single string
   *         argument
   */
  static Constructor<?> getStringConstructor(Class<?> clazz) {
    try {
      return clazz.getConstructor(String.class);
    } catch (Exception e) {
      return null;
    }

  }

  /**
   * Get generic type for supplied type.
   * 
   * @param type See {@link Type}
   * @return generic type if type is {@link ParameterizedType}, null otherwise
   */
  static Class<?> getGenericType(Type type) {
    if (type instanceof ParameterizedType) {
      ParameterizedType parameterizedType = (ParameterizedType) type;

      Type[] genericTypes = ((ParameterizedType) parameterizedType).getActualTypeArguments();
      if (genericTypes.length == 1) {
        try {

          // if can't be cast to java.lang.Class thrown Exception
          return (Class<?>) genericTypes[0];

        } catch (ClassCastException e) {
          throw new RuntimeException("Unsupported type");
        }
      }
    }

    // not parameterized type
    return null;
  }

}
