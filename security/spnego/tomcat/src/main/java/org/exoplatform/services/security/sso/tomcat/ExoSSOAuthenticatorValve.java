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
import java.security.Principal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.authenticator.AuthenticatorBase;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.Response;
import org.apache.catalina.deploy.LoginConfig;
import org.apache.catalina.realm.GenericPrincipal;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.sso.http.SSOAuthenticationFilter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ExoSSOAuthenticatorValve extends AuthenticatorBase {

  /**
   * Portal Container name. To initialize this parameter add attribute
   * 'portalContainerName' in valve configuration.
   * <pre> 
   * &lt;Valve className="org.exoplatform.services.security.sso.tomcat.ExoSSOAuthenticatorValve" portalContainerName="portal" /&gt;
   * </pre>
   */
  private String portalContainerName;

  /**
   * @return the portalContainerName
   */
  public String getPortalContainerName() {
    return portalContainerName;
  }

  /**
   * @param portalContainerName the portalContainerName to set
   */
  public void setPortalContainerName(String portalContainerName) {
    this.portalContainerName = portalContainerName;
  }

  /**
   * @return actual ExoContainer instance
   */
  protected ExoContainer getExoContainer() {
    ExoContainer exoContainer = ExoContainerContext.getCurrentContainer();
    if (exoContainer instanceof RootContainer) {
      exoContainer = RootContainer.getInstance().getPortalContainer(getPortalContainerName());
    }
    return exoContainer;
  }

  /**
   * {@inheritDoc}
   */
  @Override
  protected boolean authenticate(Request request, Response response, LoginConfig loginConfig) throws IOException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    Principal principal = SSOAuthenticationFilter.authenticate(httpRequest, httpResponse);
    if (principal != null) {
      String username = principal.getName();

      IdentityRegistry identityRegistry = (IdentityRegistry) getExoContainer().getComponentInstanceOfType(IdentityRegistry.class);

      Identity identity = identityRegistry.getIdentity(username);
      if (identity == null) {
        Authenticator authenticator = (Authenticator) getExoContainer().getComponentInstanceOfType(Authenticator.class);

        if (authenticator == null)
          throw new RuntimeException("No Authenticator component found, check your configuration");
        try {
          identity = authenticator.createIdentity(username);
        } catch (Exception e) {
          return false;
        }
        identityRegistry.register(identity);
      }

      /*
       * There is no password. So set password as 'N/P'. NOTE: Here must be used
       * GenericPrincipal we need keep roles for Tomcat.
       */
      List<String> roles = new ArrayList<String>();
      Iterator<String> iter = identity.getRoles().iterator();
      while (iter.hasNext())
        roles.add(iter.next());

      GenericPrincipal serverPrincipal = new GenericPrincipal(null, username, "N/P", roles);
      register(request, response, serverPrincipal, "", username, "N/P");
      return true;
    }
    return false;
  }

}
