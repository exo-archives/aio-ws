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
import java.util.HashSet;
import java.util.Iterator;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.apache.commons.logging.Log;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.security.oauth.OAuthProviderService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class OAuthProviderServiceImpl implements OAuthProviderService {

  /**
   * Known consumers.
   */
  protected final HashMap<String, OAuthConsumer> consumers =
        new HashMap<String, OAuthConsumer>();
  
  /**
   * User tokens. Tokens keeps in this Set only before user get access token.
   */
  protected final HashSet<OAuthAccessor> tokens = new HashSet<OAuthAccessor>();

  /**
   * Logger.
   */
  protected static final Log LOG = ExoLogger.getLogger("ws.security.OAuthProviderServiceImpl");
  
  /**
   * Constructs instance of OAuthProviderService.
   * @param params the initialized parameters.
   */
  public OAuthProviderServiceImpl(InitParams params) {
    Iterator<PropertiesParam> iterator = params.getPropertiesParamIterator();
    while (iterator.hasNext()) {
      PropertiesParam pp = iterator.next();
      String name = pp.getName();
      String secret = pp.getProperty("secret");
      String description = pp.getProperty("description");
      String callbackURL = pp.getProperty("callbackURL");
      OAuthConsumer consumer = new OAuthConsumer(callbackURL, name, secret, null);
      consumer.setProperty("name", name);
      consumer.setProperty("description", description);
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
    throw new OAuthProblemException("There is no appropriate consumer for '"
        + name + "'.");
  }
  
  /**
   * {@inheritDoc}
   */
  public OAuthAccessor getAccessor(OAuthMessage oauthMessage)
      throws OAuthProblemException {
    try {
      String token = oauthMessage.getParameter(OAuth.OAUTH_TOKEN);
      String secret = oauthMessage.getParameter(OAuth.OAUTH_TOKEN_SECRET);
      for (OAuthAccessor a : tokens) {
        if (a.requestToken != null && a.requestToken.equals(token)
            && a.tokenSecret != null && a.tokenSecret.equals(secret)) {
          if (LOG.isDebugEnabled()) {
            LOG.debug("Request token found: " + token);
          }
          return a;
        }
      }
      throw new OAuthProblemException(
          "Request token or secret token is invalid.");
    } catch (IOException e) {
      e.printStackTrace();
      throw new OAuthProblemException(e.getMessage());
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public OAuthConsumer getConsumer(OAuthMessage oauthMessage)
      throws OAuthProblemException {
    String name = null;
    try {
      oauthMessage.requireParameters(OAuth.OAUTH_CONSUMER_KEY);
      name = oauthMessage.getConsumerKey();
      return getConsumer(name);
    } catch (IOException e) {
      throw new OAuthProblemException("There is no appropriate consumer for '"
          + name + "', or '" + OAuth.OAUTH_CONSUMER_KEY + "' is not presents in oauth message.");
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void authorize(OAuthAccessor accessor, String userId)
      throws OAuthProblemException {
    if (userId == null || userId.length() == 0)
      throw new OAuthProblemException("User ID can't be null!");
    tokens.remove(accessor);
    accessor.setProperty("userId", userId);
    accessor.setProperty("authorized", Boolean.TRUE);
    tokens.add(accessor);
  }

  /**
   * {@inheritDoc}
   */
  public void generateRequestToken(OAuthAccessor accessor) {
    accessor.requestToken = generateToken();
    accessor.tokenSecret = generateSecret();
    accessor.accessToken = null;
    tokens.add(accessor);    
  }
  
  /**
   * {@inheritDoc}
   */
  public void generateAccessToken(OAuthAccessor accessor) throws OAuthProblemException {
    String requestToken = accessor.requestToken;
    String secretToken = accessor.tokenSecret;
    Iterator<OAuthAccessor> iter = tokens.iterator();
    boolean valid = false;
    while (iter.hasNext()) {
      OAuthAccessor a = iter.next();
      if (a.requestToken != null && a.requestToken.equals(requestToken)
          && a.tokenSecret != null && a.tokenSecret.equals(secretToken)) {
        
        if (LOG.isDebugEnabled()) {
          LOG.debug("Validation ok, for user '" + a.getProperty("userId")
              + "' access token will be generated.");
        }
        
        // We don't need this tokens any more.
        // Generated access and secret token will be kept by Consumer.
        iter.remove();
        valid = true;
        break;
      }
    }

    if (!valid)
      throw new OAuthProblemException("Request token or secret token is invalid.");

    // Add access token to accessor and remove request token from it.
    // Then this accessor will be passed to Consumer and, as described above,
    // will not stored any more in TokenService.
    accessor.accessToken = generateToken();
    accessor.tokenSecret = generateSecret();
    accessor.requestToken = null;
  }

  
  /**
   * Generate token.
   * @return the token.
   */
  protected abstract String generateToken();
  
  /**
   * Generate secret token.
   * @return the created secret token.
   */
  protected abstract String generateSecret();

}
