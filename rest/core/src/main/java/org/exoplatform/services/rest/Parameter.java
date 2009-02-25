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

package org.exoplatform.services.rest;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

import javax.ws.rs.QueryParam;

/**
 * Abstraction of method's or constructor's parameter.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface Parameter {

  /**
   * @return addition annotation
   */
  Annotation[] getAnnotations();

  /**
   * @return <i>main</i> annotation. It mind this annotation describe which
   *         value will be used for initialize parameter, e. g.
   *         {@link PathParam}, {@link QueryParam}, etc.
   */
  Annotation getAnnotation();

  /**
   * @return true if parameter must not be decoded false otherwise
   */
  boolean isEncoded();

  /**
   * @return default value for parameter
   */
  String getDefaultValue();

  /**
   * @return generic parameter type, can be null if parameter is not generic.
   * @see Method#getGenericParameterTypes()
   */
  Type getParameterType();

  /**
   * @return parameter class.
   * @see Method#getParameterTypes()
   */
  Class<?> getParameterClass();

}
