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
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
import org.exoplatform.services.organization.Membership;
import org.exoplatform.services.organization.OrganizationService;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.MembershipEntry;

/**
 * NOTE must be configured after SSOAuthenticationFilter. Get groups for user
 * and create and register Identity for him.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OrganizationIdentityInitalizerFilter implements Filter {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("ws.security.JndiIdentityInitalizerFilter");

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

    IdentityRegistry identityRegistry = (IdentityRegistry) getContainer().getComponentInstanceOfType(IdentityRegistry.class);

    if (userId != null) {

      if (identityRegistry.getIdentity(userId) == null) {

        Identity identity = null;
        try {
          Set<MembershipEntry> entries = new HashSet<MembershipEntry>();
          OrganizationService orgService = (OrganizationService) getContainer().getComponentInstanceOfType(OrganizationService.class);
          @SuppressWarnings("unchecked")
          Collection<Membership> memberships = orgService.getMembershipHandler()
                                                         .findMembershipsByUser(userId);
          for (Membership membership : memberships) {
            entries.add(new MembershipEntry(membership.getGroupId(), membership.getMembershipType()));
          }
          identity = new Identity(userId, entries);
        } catch (Exception e) {
          LOG.error("Can't get groups for " + userId + " from AD!");
          // create identity without memberships
          identity = new Identity(userId);
        }

        identityRegistry.register(identity);

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
