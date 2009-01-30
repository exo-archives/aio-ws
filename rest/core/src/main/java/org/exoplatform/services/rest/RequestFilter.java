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
 * Process the original {@link GenericContainerRequest} before it dispatch by
 * {@link ResourceDispatcher}. NOTE this method must be not called directly, it
 * is part of REST framework, otherwise {@link ApplicationContext} may contains
 * wrong parameters.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RequestFilter {

  /**
   * Can modify original request.
   * 
   * @param request the request
   */
  void doFilter(GenericContainerRequest request);

}
