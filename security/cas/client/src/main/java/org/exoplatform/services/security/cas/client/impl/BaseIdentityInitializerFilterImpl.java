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

package org.exoplatform.services.security.cas.client.impl;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Identity;
import org.exoplatform.services.security.cas.client.AbstractIdentityInitializerFilter;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class BaseIdentityInitializerFilterImpl extends AbstractIdentityInitializerFilter {

  /* (non-Javadoc)
   * @see org.exoplatform.services.security.cas3.AbstractIdentityInitializerFilter#createIdentity(java.lang.String)
   */
  @Override
  protected Identity createIdentity(String userId) throws Exception {
    Authenticator authenticator = (Authenticator) getContainer().getComponentInstanceOfType(
        Authenticator.class);
    if (log.isDebugEnabled()) {
      log.debug("Try create identity for user " + userId);
    }
    
    return authenticator.createIdentity(userId);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.security.cas3.AbstractIdentityInitializerFilter#getContainer()
   */
  @Override
  protected ExoContainer getContainer() {
    return ExoContainerContext.getCurrentContainer();
  }

}
