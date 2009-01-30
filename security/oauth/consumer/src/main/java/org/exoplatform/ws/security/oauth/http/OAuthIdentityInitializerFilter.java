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
 * This filter is useful when on consumer side present eXo organization service.
 * The even user not authenticated at consumer side Identity (membership, etc)
 * can be created and registered at consumer side. NOTE this filter must be
 * configured after OAuthConsumerFilter and after OAuthRequestWrapperFilter.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthIdentityInitializerFilter implements Filter {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(OAuthIdentityInitializerFilter.class.getName());

  /**
   * {@inheritDoc}
   */
  public void destroy() {
    // nothing to do
  }

  /**
   * Create {@link Identity} for user. {@inheritDoc}
   */
  public void doFilter(ServletRequest servletRequest,
                       ServletResponse servletResponse,
                       FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) servletRequest;
    String userId = httpRequest.getRemoteUser();
    IdentityRegistry identityRegistry = (IdentityRegistry) getContainer().getComponentInstanceOfType(IdentityRegistry.class);
    Identity identity = identityRegistry.getIdentity(userId);

    if (identity == null) {
      if (LOG.isDebugEnabled())
        LOG.debug("Identity is null for " + userId);

      try {
        identity = createIdentity(userId);
        identityRegistry.register(identity);
      } catch (Exception e) {
        if (LOG.isDebugEnabled())
          e.printStackTrace();
        throw new ServletException(e);
      }
    }
    chain.doFilter(servletRequest, servletResponse);

  }

  /**
   * Create Identity for given userId.
   * 
   * @param userId the user ID.
   * @return identity.
   * @throws Exception if any error occurs.
   */
  protected Identity createIdentity(String userId) throws Exception {
    Authenticator authenticator = (Authenticator) getContainer().getComponentInstanceOfType(Authenticator.class);
    if (LOG.isDebugEnabled())
      LOG.debug("Try create identity for user " + userId);

    return authenticator.createIdentity(userId);
  }

  /**
   * @return actual ExoContainer.
   */
  protected ExoContainer getContainer() {
    return ExoContainerContext.getCurrentContainer();
  }

  /**
   * {@inheritDoc}
   */
  public void init(FilterConfig config) throws ServletException {
    // nothing to do
  }

}
