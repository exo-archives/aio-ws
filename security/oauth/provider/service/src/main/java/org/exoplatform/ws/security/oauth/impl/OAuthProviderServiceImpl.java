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

package org.exoplatform.ws.security.oauth.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.Identity;
import org.exoplatform.ws.security.oauth.OAuthProviderService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthProviderServiceImpl implements OAuthProviderService {

  /**
   * Known consumers.
   */
  private final HashMap<String, OAuthConsumer> consumers = new HashMap<String, OAuthConsumer>();

  /**
   * User tokens. Tokens keeps in this Set only before user get access token.
   */
  private final Map<String, OAuthAccessor>     tokens;

  /**
   * Logger.
   */
  private static final Log                     LOG       = ExoLogger.getLogger(OAuthProviderServiceImpl.class.getName());

  /**
   * Constructs instance of OAuthProviderService.
   * 
   * @param params the initialized parameters.
   */
  @SuppressWarnings("unchecked")
  public OAuthProviderServiceImpl(InitParams params) {
    tokens = new ConcurrentHashMap<String, OAuthAccessor>();
    Iterator<PropertiesParam> iterator = params.getPropertiesParamIterator();
    while (iterator.hasNext()) {
      PropertiesParam pp = iterator.next();
      String name = pp.getName();
      String secret = pp.getProperty(CONSUMER_SECRET_PROPERTY);
      String description = pp.getProperty(CONSUMER_SECRET_PROPERTY);
      String callbackURL = pp.getProperty(CONSUMER_CALLBACK_URL_PROPERTY);
      OAuthConsumer consumer = new OAuthConsumer(callbackURL, name, secret, null);
      consumer.setProperty(CONSUMER_NAME_PROPERTY, name);
      consumer.setProperty(CONSUMER_DESCRIPTION_PROPERTY, description);
      consumers.put(name, consumer);
    }
  }

  /**
   * {@inheritDoc}
   */
  public OAuthConsumer getConsumer(String name) throws OAuthProblemException {
    OAuthConsumer consumer = consumers.get(name);
    if (consumer != null)
      return consumer;
    throw new OAuthProblemException("There is no appropriate consumer for " + name);
  }

  /**
   * {@inheritDoc}
   */
  public OAuthAccessor getAccessor(OAuthMessage oauthMessage) throws OAuthProblemException {
    try {
      String token = oauthMessage.getParameter(OAuth.OAUTH_TOKEN);
      String secret = oauthMessage.getParameter(OAuth.OAUTH_TOKEN_SECRET);
      return getAccessor(token, secret);
    } catch (IOException e) {
      e.printStackTrace();
      throw new OAuthProblemException(e.getMessage());
    }
  }

  /**
   * {@inheritDoc}
   */
  public OAuthConsumer getConsumer(OAuthMessage oauthMessage) throws OAuthProblemException {
    String name = null;
    try {
      oauthMessage.requireParameters(OAuth.OAUTH_CONSUMER_KEY);
      name = oauthMessage.getConsumerKey();
      return getConsumer(name);
    } catch (IOException e) {
      throw new OAuthProblemException("There is no appropriate consumer for " + name + ", or "
          + OAuth.OAUTH_CONSUMER_KEY + " is not presents in oauth message.");
    }
  }

  /**
   * {@inheritDoc}
   */
  public void authorize(OAuthAccessor accessor, Identity identity) throws OAuthProblemException {
    if (identity == null)
      throw new OAuthProblemException("Identity is null!");
    tokens.remove(accessor.requestToken);
    accessor.setProperty("oauth_user_id", identity.getUserId());
    accessor.setProperty("oauth_user_roles", identity.getRoles());
    accessor.setProperty("authorized", Boolean.TRUE);
    tokens.put(accessor.requestToken, accessor);
  }

  /**
   * {@inheritDoc}
   */
  public void generateRequestToken(OAuthAccessor accessor) {
    accessor.requestToken = generateToken();
    accessor.tokenSecret = generateSecret(accessor.requestToken);
    accessor.accessToken = null;
    tokens.put(accessor.requestToken, accessor);
  }

  /**
   * {@inheritDoc}
   */
  public void generateAccessToken(OAuthAccessor accessor) throws OAuthProblemException {
    String token = accessor.requestToken;
    String secret = accessor.tokenSecret;
    if (getAccessor(token, secret) != null) {
      // remove it, don't need it any more at provider side
      tokens.remove(token);
      // Add access token to accessor and remove request token from it.
      // Then this accessor will be passed to Consumer and, as described above,
      // will not stored any more in TokenService.
      accessor.accessToken = generateToken();
      accessor.tokenSecret = generateSecret(accessor.accessToken);
      accessor.requestToken = null;
    }

  }

  /**
   * Tokens will be represented as uuid:urn <a
   * href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>.
   * 
   * @return generated access or secret token
   */
  protected String generateToken() {
    return UUID.randomUUID().toString();
  }

  /**
   * Tokens will be represented as uuid:urn <a
   * href="http://www.ietf.org/rfc/rfc4122.txt">rfc4122</a>.
   * 
   * @param token access or request token
   * @return generated secret token
   */
  protected String generateSecret(String token) {
    return UUID.nameUUIDFromBytes(token.getBytes()).toString();
  }

  /**
   * @param token request token
   * @param secret secret token
   * @return accessor
   * @throws OAuthProblemException if accessor not found
   */
  private OAuthAccessor getAccessor(String token, String secret) throws OAuthProblemException {
    OAuthAccessor accessor = tokens.get(token);
    if (accessor != null) {
      if (accessor.tokenSecret != null) {
        if (secret != null && secret.equals(accessor.tokenSecret)) {
          if (LOG.isDebugEnabled())
            LOG.debug("Request token found: " + token);
          return accessor;
        }
      }
    }
    throw new OAuthProblemException("Request token or secret token is invalid.");
  }

}
