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

package org.exoplatform.ws.security.oauth;

import java.util.Set;

import net.oauth.OAuthAccessor;

/**
 * Must check collection of tokens and remove all expired tokens from it.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface OAuthTokenCleaner {
  
  /**
   * Looking for expired tokens and remove it.
   */
  void clean();
  
  /**
   * @param tokens the Collection of tokens which must be under control.
   */
  void setTokens(Set<OAuthAccessor> tokens);
  
}
