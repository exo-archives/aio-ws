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
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface OAuthProviderService {

  /**
   * Look up accessor by given OAuthMessage.
   * @param oauthMessage the message.
   * @return OAuthAccessor.
   * @throws OAuthProblemException if accessor not found.
   */
  OAuthAccessor getAccessor(OAuthMessage oauthMessage) throws OAuthProblemException;
  
  /**
   * @param oauthMessage the message.
   * @return consumer.
   * @throws OAuthProblemException if consumer not found.
   */
  OAuthConsumer getConsumer(OAuthMessage oauthMessage) throws OAuthProblemException;
  
  /**
   * @param name the consumer name.
   * @return consumer.
   * @throws OAuthProblemException if consumer not found.
   */
  OAuthConsumer getConsumer(String name) throws OAuthProblemException;
  
  /**
   * @param accessor the accessor.
   * @param userId the user name or other identifier.
   * @throws OAuthProblemException
   */
  void authorize(OAuthAccessor accessor, String userId) throws OAuthProblemException;
  
  /**
   * Generate request token for given accessor and put it in collection.
   * After that it will be used for getting access token.
   * @param accessor the accessor.
   */
  void generateRequestToken(OAuthAccessor accessor);
  
  /**
   * Generate access token for given accessor and remove it from collection.
   * This accessor must be kept by Consumer.
   * @param accessor the accessor.
   * @throws OAuthProblemException
   */
  void generateAccessToken(OAuthAccessor acessor) throws OAuthProblemException;
  
}

