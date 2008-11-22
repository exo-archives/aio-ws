/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
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
package org.exoplatform.services.rest.data;

import org.exoplatform.services.rest.MultivaluedMetadata;

/**
 * Utils for process query parameters.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class QueryUtils {
  
  /**
   * Must not be created.
   */
  private QueryUtils() {
  }

  /**
   * Transform query string to {@link MultivaluedMetadata} .
   * @param queryString source string.
   * @return MultivaluedMetadata.
   */
  public static MultivaluedMetadata parseQueryString(String queryString) {
    String[] p = queryString.split("&");
    MultivaluedMetadata queryParams = new MultivaluedMetadata();
    for (String s : p) {
      String[] t = s.split("=");
      queryParams.putSingle(t[0], t[1]);
    }
    return queryParams;
  }

}
