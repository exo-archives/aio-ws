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
class AnnotationUtils {
  
  public enum Anno {
    // method's annotations
    HTTP_METHOD,
    URI_TEMPLATE,
    QUERY_TEMPLATE,
    CONSUMED_MIMETYPES,
    PRODUCED_MIMETYPES, 
    INPUT_TRANSFORMER,
    OUTPUT_TRANSFORMER,
    
    // method's parameters annotations
    URI_PARAM,
    QUERY_PARAM,
    HEADER_PARAM,
    COOKIE_PARAM,
    CONTEXT_PARAM
  }
  
  private static final Map<String, Anno> knownAnno =
    new HashMap<String, Anno>(); 
  
  static {
    knownAnno.put(ConsumedMimeTypes.class.getName(), Anno.CONSUMED_MIMETYPES);
    knownAnno.put(ContextParam.class.getName(), Anno.CONTEXT_PARAM);
    knownAnno.put(CookieParam.class.getName(), Anno.COOKIE_PARAM);
    knownAnno.put(HeaderParam.class.getName(), Anno.HEADER_PARAM);
    knownAnno.put(HTTPMethod.class.getName(), Anno.HTTP_METHOD);
    knownAnno.put(InputTransformer.class.getName(), Anno.INPUT_TRANSFORMER);
    knownAnno.put(OutputTransformer.class.getName(), Anno.OUTPUT_TRANSFORMER);
    knownAnno.put(ProducedMimeTypes.class.getName(), Anno.PRODUCED_MIMETYPES);
    knownAnno.put(QueryParam.class.getName(), Anno.QUERY_PARAM);
    knownAnno.put(QueryTemplate.class.getName(), Anno.QUERY_TEMPLATE);
    knownAnno.put(URIParam.class.getName(), Anno.URI_PARAM);
    knownAnno.put(URITemplate.class.getName(), Anno.URI_TEMPLATE);
  }
  
  public static boolean isKnownType(Class<?> clazz) {
    return knownAnno.get(clazz.getName()) != null;
  }
  
  public static Anno getType(Class<?> clazz) {
    return knownAnno.get(clazz.getName());
  }

}

