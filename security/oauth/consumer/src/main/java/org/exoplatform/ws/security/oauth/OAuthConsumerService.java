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

import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface OAuthConsumerService {
  
  /**
   * Get accessor, if accessor not found new one must be created.
   * @param oauthMessage the OAuthMessage. Must have at least two parameters
   * @see net.oauth.OAuth.OAUTH_TOKEN,
   * @see net.oauth.OAuth.OAUTH_TOKEN_SECRET.  
   * @return satisfied accessor. 
   * @throws OAuthProblemException if can't get accessor and can't create new one.
   */
  OAuthAccessor getAccessor(OAuthMessage oauthMessage) throws OAuthProblemException;
  
  /**
   * Remove accessor, can be used for removing accessor when client logout.
   * @param oauthMessage the OAuthMessage. Must have at least two parameters
   * @see net.oauth.OAuth.OAUTH_TOKEN,
   * @see net.oauth.OAuth.OAUTH_TOKEN_SECRET.
   * @throws OAuthProblemException if accessor can't be removed.
   */
  void removeAccessor(OAuthMessage oauthMessage) throws OAuthProblemException;
  
}
