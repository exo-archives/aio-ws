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

import java.util.HashMap;
import java.util.Map;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
final class AnnotationUtils {
  
  /**
   * Utility class. Must not be created.
   */
  private AnnotationUtils() {
  }
  
  /**
   * Known annotation types.
   */
  public enum Anno {
    // method's annotations
    
    /**
     * HTTP method, @see {@link HTTPMethod} .
     */
    HTTP_METHOD,
    
    /**
     * URI template, see {@link URITemplate} .
     */
    URI_TEMPLATE,

    /**
     * Query template, see {@link QueryTemplate} .
     */
    QUERY_TEMPLATE,

    /**
     * see {@link ConsumedMimeTypes} .
     */
    CONSUMED_MIMETYPES,
    
    /**
     * see {@link ProducedMimeTypes} .
     */
    PRODUCED_MIMETYPES, 
    
    /**
     * see {@link InputTransformer} .
     */
    INPUT_TRANSFORMER,
    
    /**
     * see {@link OutputTransformer} .
     */
    OUTPUT_TRANSFORMER,
    
    // method's parameters annotations
    
    /**
     * @see {@link URIParam} .
     */
    URI_PARAM,
    
    /**
     * see {@link QueryParam} .
     */
    QUERY_PARAM,
    
    /**
     * see {@link HeaderParam} .
     */
    HEADER_PARAM,
    
    /**
     * @see {@link CookieParam} .
     */
    COOKIE_PARAM,
    
    /**
     * @see {@link ContextParam} .
     */
    CONTEXT_PARAM
  }
  
  /**
   * Mapping annotations class name to enum.
   */
  private static final Map<String, Anno> KNOWN_ANNO = new HashMap<String, Anno>(); 
  
  static {
    KNOWN_ANNO.put(ConsumedMimeTypes.class.getName(), Anno.CONSUMED_MIMETYPES);
    KNOWN_ANNO.put(ContextParam.class.getName(), Anno.CONTEXT_PARAM);
    KNOWN_ANNO.put(CookieParam.class.getName(), Anno.COOKIE_PARAM);
    KNOWN_ANNO.put(HeaderParam.class.getName(), Anno.HEADER_PARAM);
    KNOWN_ANNO.put(HTTPMethod.class.getName(), Anno.HTTP_METHOD);
    KNOWN_ANNO.put(InputTransformer.class.getName(), Anno.INPUT_TRANSFORMER);
    KNOWN_ANNO.put(OutputTransformer.class.getName(), Anno.OUTPUT_TRANSFORMER);
    KNOWN_ANNO.put(ProducedMimeTypes.class.getName(), Anno.PRODUCED_MIMETYPES);
    KNOWN_ANNO.put(QueryParam.class.getName(), Anno.QUERY_PARAM);
    KNOWN_ANNO.put(QueryTemplate.class.getName(), Anno.QUERY_TEMPLATE);
    KNOWN_ANNO.put(URIParam.class.getName(), Anno.URI_PARAM);
    KNOWN_ANNO.put(URITemplate.class.getName(), Anno.URI_TEMPLATE);
  }
  
  /**
   * Check is annotation known.
   * @param clazz the annotation class.
   * @return true if annotation is known, false otherwise.
   */
  public static boolean isKnownType(Class<?> clazz) {
    return KNOWN_ANNO.get(clazz.getName()) != null;
  }
  
  /**
   * Get enum type which is corresponded to aonnotation class.
   * @param clazz the annotation class.
   * @return enum type.
   */
  public static Anno getType(Class<?> clazz) {
    return KNOWN_ANNO.get(clazz.getName());
  }

}

