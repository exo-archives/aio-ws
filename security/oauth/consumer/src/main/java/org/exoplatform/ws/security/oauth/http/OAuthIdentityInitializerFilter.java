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

package org.exoplatform.ws.security.oauth.http;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthIdentityInitializerFilter implements Filter {

  private final static Log log = ExoLogger.getLogger("ws.security.OAuthIdentityInitializerFilter");

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy() {
    // nothing to do
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
   *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain chain)
      throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
    String userId = httpRequest.getRemoteUser();
    IdentityRegistry identityRegistry = (IdentityRegistry) getContainer().getComponentInstanceOfType(
        IdentityRegistry.class);
    Identity identity = identityRegistry.getIdentity(userId);

    if (identity == null) {
      if (log.isDebugEnabled()) {
        log.debug("Identity is null for " + userId);
      }

      try {
        identity = createIdentity(userId);
        identityRegistry.register(identity);
      } catch (Exception e) {
        e.printStackTrace();
        throw new ServletException(e);
      }
    }
    chain.doFilter(servletRequest, servletResponse);

  }

  protected Identity createIdentity(String userId) throws Exception {
    Authenticator authenticator = (Authenticator) getContainer().getComponentInstanceOfType(Authenticator.class);
    if (log.isDebugEnabled()) {
      log.debug("Try create identity for user " + userId);
    }

    return authenticator.createIdentity(userId);
  }

  protected ExoContainer getContainer() {
    return ExoContainerContext.getCurrentContainer();
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig arg0) throws ServletException {
    // nothing to do
  }

}
