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

package org.exoplatform.services.security.sso.tomcat;

import java.io.IOException;
import java.rmi.ServerException;
import java.security.Principal;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.realm.GenericPrincipal;
import org.exoplatform.services.security.sso.http.SSOAuthenticationFilter;
import org.exoplatform.services.security.sso.jndi.JndiAction;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SSOAuthenticatorValve extends AuthenticatorBase {

  /**
   * {@inheritDoc} 
   */
  @Override
  protected boolean authenticate(Request request, Response response, LoginConfig loginConfig) throws IOException,
      ServerException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    Principal principal = SSOAuthenticationFilter.authenticate(httpRequest, httpResponse);
    if (principal != null) {
      String username = principal.getName();
      
      /* Get list of groups from DC. Roles list must be filed.
       * Roles required by servlet container.
       */
      List<String> roles = null;
      try {
        roles = JndiAction.getGroups(username);
      } catch (javax.security.auth.login.LoginException e) {
        throw new ServerException("Can't get groups for " + username + " form AD.");
      }
      // dummy roles (for test)
//      roles = new ArrayList<String>();
//      roles.add("users");
      
      /* There is no password. So set password as 'N/P'.
       * NOTE: Here must be used GenericPrincipal we need keep roles for Tomcat.
       */
      GenericPrincipal serverPrincipal = new GenericPrincipal(null, username, "N/P", roles);
      register(request, response, serverPrincipal, "", username, "N/P");
      return true;
    }
    return false;
  }

}
