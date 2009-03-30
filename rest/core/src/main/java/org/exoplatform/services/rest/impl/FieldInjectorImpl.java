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

package org.exoplatform.services.rest.impl;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.Encoded;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.ApplicationContext;
import org.exoplatform.services.rest.FieldInjector;
import org.exoplatform.services.rest.impl.method.ParameterHelper;
import org.exoplatform.services.rest.impl.method.ParameterResolver;
import org.exoplatform.services.rest.impl.method.ParameterResolverFactory;
import org.exoplatform.services.rest.resource.ResourceDescriptorVisitor;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FieldInjectorImpl implements FieldInjector {

  /**
   * Logger.
   */
  private static final Log   LOG = ExoLogger.getLogger(FieldInjectorImpl.class.getName());

  /**
   * All annotations including JAX-RS annotation.
   */
  private final Annotation[] annotations;

  /**
   * JAX-RS annotation.
   */
  private final Annotation   annotation;

  /**
   * Parameter type. See {@link Constructor#getGenericParameterTypes()} .
   */
  private final Type         type;

  /**
   * Parameter class. See {@link Constructor#getParameterTypes()}
   */
  private final Class<?>     clazz;

  /**
   * Default value for this parameter, default value can be used if there is not
   * found required parameter in request. See {@link javax.ws.rs.DefaultValue}.
   */
  private final String       defaultValue;

  /**
   * See {@link javax.ws.rs.Encoded}.
   */
  private final boolean      encoded;

  /**
   * Field name, see {@link java.lang.reflect.Field#getName()}.
   */
  private final String       name;

  /**
   * @param resourceClass class that contains field <tt>jfield</tt>
   * @param jfield java.lang.reflect.Field
   */
  public FieldInjectorImpl(Class<?> resourceClass, java.lang.reflect.Field jfield) {

    this.name = jfield.getName();
    this.annotations = jfield.getDeclaredAnnotations();
    this.clazz = jfield.getType();
    this.type = jfield.getGenericType();

    Annotation annotation = null;
    String defaultValue = null;
    boolean encoded = false;

    // is resource provider
    boolean provider = resourceClass.getAnnotation(Provider.class) != null;
    List<String> allowedAnnotation;
    if (provider)
      allowedAnnotation = ParameterHelper.PROVIDER_FIELDS_ANNOTATIONS;
    else
      allowedAnnotation = ParameterHelper.RESOURCE_FIELDS_ANNOTATIONS;

    for (Annotation a : annotations) {
      Class<?> ac = a.annotationType();

      if (allowedAnnotation.contains(ac.getName())) {
        if (annotation == null) {
          annotation = a;
        } else {
          String msg = "JAX-RS annotations on one of fields are equivocality. Annotations: "
              + annotation.toString() + " and " + a.toString() + " can't be applied to one field.";
          throw new RuntimeException(msg);
        }

        // @Encoded has not sense for Provider. Provider may use only @Context
        // annotation for fields
      } else if (ac == Encoded.class && !provider) {
        encoded = true;
        // @Default has not sense for Provider. Provider may use only @Context
        // annotation for fields
      } else if (ac == DefaultValue.class && !provider) {
        defaultValue = ((DefaultValue) a).value();
      } else {
        LOG.warn("Field " + jfield.toString()
            + " contains unknown or not allowed JAX-RS annotation " + a.toString()
            + ". It will be ignored.");
      }
    }

    this.defaultValue = defaultValue;
    this.annotation = annotation;
    this.encoded = encoded || resourceClass.getAnnotation(Encoded.class) != null;

  }

  /**
   * {@inheritDoc}
   */
  public Annotation getAnnotation() {
    return annotation;
  }

  /**
   * {@inheritDoc}
   */
  public Annotation[] getAnnotations() {
    return annotations;
  }

  /**
   * {@inheritDoc}
   */
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * {@inheritDoc}
   */
  public Class<?> getParameterClass() {
    return clazz;
  }

  /**
   * {@inheritDoc}
   */
  public Type getGenericType() {
    return type;
  }

  /**
   * {@inheritDoc}
   */
  public boolean isEncoded() {
    return encoded;
  }

  /**
   * {@inheritDoc}
   */
  public String getName() {
    return name;
  }

  /**
   * {@inheritDoc}
   */
  public void inject(Object resource, ApplicationContext context) {
    if (annotation != null) {
      ParameterResolver<?> pr = ParameterResolverFactory.createParameterResolver(annotation);
      try {
        java.lang.reflect.Field jfield = resource.getClass().getDeclaredField(getName());

        if (!Modifier.isPublic(jfield.getModifiers()))
          jfield.setAccessible(true);

        jfield.set(resource, pr.resolve(this, context));
      } catch (Throwable e) {
        
//        if (LOG.isDebugEnabled())
          e.printStackTrace();

        Class<?> ac = annotation.annotationType();
        if (ac == MatrixParam.class || ac == QueryParam.class || ac == PathParam.class)
          throw new WebApplicationException(Response.status(Response.Status.NOT_FOUND).build());

        throw new WebApplicationException(Response.status(Response.Status.BAD_REQUEST).build());
      }
    }

  }
  
  /**
   * {@inheritDoc}
   */
  public void accept(ResourceDescriptorVisitor visitor) {
    visitor.visitFieldInjector(this);
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer("[ FieldInjectorImpl: ");
    sb.append("annotation: " + getAnnotation())
      .append("; type: " + getParameterClass())
      .append("; generic-type : " + getGenericType())
      .append("; default-value: " + getDefaultValue())
      .append("; encoded: " + isEncoded())
      .append(" ]");
    return sb.toString();
  }

}
