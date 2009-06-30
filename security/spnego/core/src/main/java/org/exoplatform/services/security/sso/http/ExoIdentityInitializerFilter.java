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

package org.exoplatform.services.security.sso.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;

/**
 * NOTE must be configured after SSOAuthenticationFilter. Get groups for user
 * and create and register Identity for him.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class ExoIdentityInitializerFilter implements Filter {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("ws.security.ExoIdentityInitializerFilter");

  /**
   * {@inheritDoc}
   */
  public void destroy() {
    // nothing to do
  }

  /**
   * {@inheritDoc}
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                           ServletException {
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    String userId = httpRequest.getRemoteUser();

    if (userId != null) {
      IdentityRegistry identityRegistry = (IdentityRegistry) getContainer().getComponentInstanceOfType(IdentityRegistry.class);
      if (identityRegistry.getIdentity(userId) == null) {
        Authenticator authenticator = (Authenticator) getContainer().getComponentInstanceOfType(Authenticator.class);

        if (authenticator == null)
          throw new RuntimeException("No Authenticator component found, check your configuration");
        try {
          Identity identity = authenticator.createIdentity(userId);
          identityRegistry.register(identity);
        } catch (Exception e) {
          LOG.error("Unable create identity for user " + userId, e);
        }
      } else {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Identity for " + userId + " already registered.");
        }
      }
    } else {
      LOG.error("Username is null, can't create identity.");
    }
    chain.doFilter(request, response);
  }

  /**
   * @return actual exo container.
   */
  private static ExoContainer getContainer() {
    return ExoContainerContext.getCurrentContainer();
  }

  /**
   * {@inheritDoc}
   */
  public void init(FilterConfig arg0) throws ServletException {
    // nothing to do
  }

}
