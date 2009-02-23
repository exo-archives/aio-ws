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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class AnnotationUtils {

  /**
   * Utility class. Must not be created.
   */
  private AnnotationUtils() {
  }

  /**
   * Annotations allowed for method parameters.
   */
  public enum PARAMETER_ANNOTATIONS {
    /**
     * Cookie parameter.
     */
    COOKIE_PARAM,
    /**
     * Context parameter. With &#64;Context annotation in method can be passed
     * some application context and container context parameters.
     */
    CONTEXT,
    /**
     * Form parameters.
     */
    FORM_PARAM,
    /**
     * HTTP header parameters.
     */
    HEADER_PARAM,
    /**
     * Matrix parameters.
     */
    MATRIX_PARAM,
    /**
     * Path parameters.
     */
    PATH_PARAM,
    /**
     * Query parameter.
     */
    QUERY_PARAM,
    /**
     * Meta annotation for HTTP method annotations.
     */
    /**
     * Annotations which indicate must method parameter be encoded.
     */
    ENCODED,
    /**
     * Default value for method parameter.
     */
    DEFAULT_VALUE
  }

  /**
   * Annotations allowed for classes.
   */
  public enum TYPE_ANNOTATIONS {
    /**
     * Resource path.
     */
    PATH,
    /**
     * Media types which resource can consume.
     */
    CONSUMES,
    /**
     * Media types which resource can produce.
     */
    PRODUCES
  }

  /**
   * Annotations allowed for methods.
   */
  public enum METHOD_ANNOTATIONS {
    /**
     * Cookie parameter.
     */
    COOKIE_PARAM,
    /**
     * Context parameter. With &#64;Context annotation in method can be passed
     * some application context and container context parameters.
     */
    CONTEXT,
    /**
     * Form parameters.
     */
    FORM_PARAM,
    /**
     * HTTP header parameters.
     */
    HEADER_PARAM,
    /**
     * Matrix parameters.
     */
    MATRIX_PARAM,
    /**
     * Path parameters.
     */
    PATH_PARAM,
    /**
     * Query parameter.
     */
    QUERY_PARAM,
    /**
     * Meta annotation for HTTP method annotations.
     */
    HTTP_METHOD,
    /**
     * HTTP method GET.
     */
    GET,
    /**
     * HTTP method POST.
     */
    POST,
    /**
     * HTTP method PUT.
     */
    PUT,
    /**
     * HTTP method HEAD.
     */
    HEAD,
    /**
     * HTTP method DELETE.
     */
    DELETE,
    /**
     * Annotations which indicate must method parameter be encoded.
     */
    ENCODED,
    /**
     * Default value for method parameter.
     */
    DEFAULT_VALUE,
    /**
     * Resource path.
     */
    PATH,
    /**
     * Media types which resource can consume.
     */
    CONSUMES,
    /**
     * Media types which resource can produce.
     */
    PRODUCES
  }

}
