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

package org.exoplatform.services.ws.rest.ejb21_;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJBObject;

/**
 * Work with REST service through EJB.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RestEJBConnector extends EJBObject {

  /**
   * @param str - data String.
   * @param method - HTTP method.
   * @param url - URL.
   * @param headers - Map of HTTP headers.
   * @param queries - Map of query parameters.
   * @return - result String from REST service.
   */
  public String service(String str, String method, String url,
      HashMap < String, List < String > > headers,
      HashMap < String, List < String > > queries) throws RemoteException;

  /**
   * @param str - data string.
   * @param method - HTTP method.
   * @param url - URL
   * @return - result String from REST service.
   */
  public String service(String str,
      String method, String url) throws RemoteException ;

  /**
   * @param method - HTTP method.
   * @param url - URL
   * @return - result String from REST service.
   */
  public String service(String method, String url) throws RemoteException;
}
