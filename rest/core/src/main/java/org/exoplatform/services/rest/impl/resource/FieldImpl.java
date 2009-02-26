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
import java.lang.reflect.Type;

import javax.ws.rs.DefaultValue;

import org.exoplatform.services.rest.resource.Field;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class FieldImpl implements Field {

  /**
   * External annotations for parameter, external it mind some other then
   * contains in {@link ParameterHelper#FIELDS_ANNOTATIONS_MAP}.
   */
  private final Annotation[] additional;

  /**
   * One of annotations from {@link ParameterHelper#FIELDS_ANNOTATIONS_MAP}.
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
   * @param annotation see {@link #annotation}
   * @param additional see {@link #additional}
   * @param clazz field class
   * @param type generic field type
   * @param defaultValue default value for field. See {@link DefaultValue}.
   * @param encoded true if field must not be decoded false otherwise
   */
  public FieldImpl(Annotation annotation,
                   Annotation[] additional,
                   Class<?> clazz,
                   Type type,
                   String defaultValue,
                   boolean encoded) {
    this.annotation = annotation;
    this.additional = additional;
    this.clazz = clazz;
    this.type = type;
    this.defaultValue = defaultValue;
    this.encoded = encoded;
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
    return additional;
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

}
