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

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.exoplatform.services.rest.method.MethodInvokerFilter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RequestHandler extends EntityProviderResolver {

  /**
   * Handle the HTTP request by dispatching request to appropriate resource. If
   * no one appropriate resource found then error response will be produced.
   * 
   * @param request HTTP request
   * @param response HTTP response
   * @throws IOException if any i/o error occurs
   */
  void handleRequest(GenericContainerRequest request, GenericContainerResponse response) throws IOException;

  /**
   * @return collection on method invoking filters
   */
  List<MethodInvokerFilter> getInvokerFilters();

  /**
   * @return mutable application attributes
   */
  Map<String, Object> getAttributes();

}
