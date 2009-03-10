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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RequestHandler {

  public static final String WS_RS_TMP_DIR               = "ws.rs.tmpdir";

  public static final String WS_RS_BUFFER_SIZE           = "ws.rs.buffersize";

  public static final String WS_RS_USE_BUILTIN_PROVIDERS = "ws.rs.provider.builtin";

  /**
   * Handle the HTTP request by dispatching request to appropriate resource. If
   * no one appropriate resource found then error response will be produced.
   * 
   * @param request HTTP request
   * @param response HTTP response
   * @throws Exception if any error occurs
   */
  void handleRequest(GenericContainerRequest request, GenericContainerResponse response) throws Exception;

}
