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

package org.exoplatform.ws.rest.ejbconnector30;

import java.io.InputStream;
import java.net.URI;
import java.security.Principal;

import javax.ejb.SessionContext;
import javax.ws.rs.core.MultivaluedMap;

import org.exoplatform.services.rest.impl.ContainerRequest;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class EJBContainerRequest extends ContainerRequest {

  /**
   * Session context.
   */
  private final SessionContext context;
  
  /**
   * @param method HTTP method
   * @param requestUri request URI
   * @param baseUri base URI, in this case URI("")
   * @param entityStream data stream
   * @param httpHeaders headers
   * @param context session context
   */
  public EJBContainerRequest(String method,
                             URI requestUri,
                             URI baseUri,
                             InputStream entityStream,
                             MultivaluedMap<String, String> httpHeaders,
                             SessionContext context) {
    super(method, requestUri, baseUri, entityStream, httpHeaders);
    this.context = context;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public Principal getUserPrincipal() {
    if (context != null)
      return context.getCallerPrincipal();
    return null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isSecure() {
    return context != null && context.getCallerPrincipal() != null;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  public boolean isUserInRole(String role) {
    return context != null && context.isCallerInRole(role);
  }

}
