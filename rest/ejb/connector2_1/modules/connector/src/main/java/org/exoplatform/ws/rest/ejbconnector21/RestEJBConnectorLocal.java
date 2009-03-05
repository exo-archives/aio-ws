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

package org.exoplatform.ws.rest.ejbconnector21;

import java.io.IOException;

import javax.ejb.EJBLocalObject;

import org.exoplatform.common.transport.SerialRequest;
import org.exoplatform.common.transport.SerialResponse;

/**
 * Work with REST service through EJB.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RestEJBConnectorLocal extends EJBLocalObject {

  /**
   * Local interface.
   * @param request SerialRequest. This is special wrapper around REST request, which can be transfer via RMI.
   * @return SerialResponse. This is special wrapper for REST response, which can be transfer via RMI.
   * @throws IOException if i/o error occurs.
   */
  SerialResponse service(SerialRequest request) throws IOException;
  
}
