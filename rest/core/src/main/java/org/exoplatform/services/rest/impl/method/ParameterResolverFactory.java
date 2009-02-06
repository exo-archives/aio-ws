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

import java.lang.annotation.Annotation;

import javax.ws.rs.CookieParam;
import javax.ws.rs.FormParam;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.MatrixParam;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class ParameterResolverFactory {

  /**
   * Constructor.
   */
  private ParameterResolverFactory() {
  }
  
  /**
   * Create parameter resolver for supplied annotation.
   * 
   * @param annotation JAX-RS annotation
   * @return ParameterResolver
   */
  @SuppressWarnings("unchecked")
  public static ParameterResolver createParameterResolver(Annotation annotation) {
    String className = annotation.annotationType().getName();
    switch (MethodParameterHelper.PARAMETER_ANNOTATIONS_MAP.get(className)) {
    case COOKIE_PARAM:
      return new CookieParameterResolver((CookieParam) annotation);
    case CONTEXT:
      return new ContextParameterResolver((Context) annotation);
    case FORM_PARAM:
      return new FormParameterResolver((FormParam) annotation);
    case HEADER_PARAM:
      return new HeaderParameterResolver((HeaderParam) annotation);
    case MATRIX_PARAM:
      return new MatrixParameterResolver((MatrixParam) annotation);
    case PATH_PARAM:
      return new PathParameterResolver((PathParam) annotation);
    case QUERY_PARAM:
      return new QueryParameterResolver((QueryParam) annotation);
    default:
      // nothing to do, null will be returned
      break;
    }

    return null;
  }

}
