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

  public static final String REMMEMBER_MAX_AGE_PROPERTY           = "remmemberMaxAge";

  public static final String CONSUMER_SECRET_KEY_PROPERTY         = "consumer.secret";

  public static final String CONSUMER_NAME_PROPERTY               = "consumer.name";

  public static final String CONSUMER_DESCRIPTION_PROPERTY        = "consumer.description";

  public static final String CONSUMER_CLIENT_PROPERTY             = "consumer.client";

  public static final String CONSUMER_STORAGE_PROPERTY            = "consumer.storage";

  public static final String CONSUMER_STORAGE_PROPERTIES_PROPERTY = "consumer.storage.properties";

  public static final String PROVIDER_TOKEN_REQUEST_URL_PROPERTY  = "provider.tokenRequestURL";

  public static final String PROVIDER_AUTHORIZATION_URL_PROPERTY  = "provider.authorizationURL";

  public static final String PROVIDER_TOKEN_ACCESS_URL_PROPERTY   = "provider.accessTokenURL";

  /**
   * Get accessor, if accessor not found new one must be created.
   * 
   * @param oauthMessage the OAuthMessage, must have at least two parameters
   * @return satisfied accessor
   * @throws OAuthProblemException if can't get accessor and can't create new
   *           one
   * @see net.oauth.OAuth.OAUTH_TOKEN
   * @see net.oauth.OAuth.OAUTH_TOKEN_SECRET
   */
  OAuthAccessor getAccessor(OAuthMessage oauthMessage) throws OAuthProblemException;

  /**
   * Remove accessor, can be used for removing accessor when client logout.
   * 
   * @param oauthMessage the OAuthMessage, must have at least two parameters
   * @throws OAuthProblemException if accessor can't be removed.
   * @see net.oauth.OAuth.OAUTH_TOKEN
   * @see net.oauth.OAuth.OAUTH_TOKEN_SECRET
   */
  void removeAccessor(OAuthMessage oauthMessage) throws OAuthProblemException;

}
