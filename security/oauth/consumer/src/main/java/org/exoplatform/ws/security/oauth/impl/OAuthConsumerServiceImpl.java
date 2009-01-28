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

import org.apache.commons.logging.Log;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.security.oauth.ExoOAuthClient;
import org.exoplatform.ws.security.oauth.OAuthConsumerService;
import org.exoplatform.ws.security.oauth.OAuthTokenCleaner;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.OAuthServiceProvider;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public final class OAuthConsumerServiceImpl implements OAuthConsumerService {
  
  /**
   * Default token alive time is 5 hours.
   */
  public static final long DEFAULT_TOKEN_ALIVE_TIME = 60000 * 60 * 5;

  /**
   * Tokens.
   */
  private final HashSet<OAuthAccessor> tokens = new HashSet<OAuthAccessor>();
  
  /**
   * Known consumers.
   */
  private final HashMap<String, OAuthConsumer> consumers =
      new HashMap<String, OAuthConsumer>();
  
  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("ws.security.OAuthConsumerServiceImpl");
  
  /**
   * Actual ExoOAuthClient.
   */
  private final ExoOAuthClient oauthClient;
  
  /**
   * Actual token alive time.
   */
  private final long tokenAliveTime;
  
  /**
   * Without token cleaner. Tokens never expired.
   * @param oauthClient the ExoOAuthClient instance.
   * @param params initialized parameters.
   */
  public OAuthConsumerServiceImpl(ExoOAuthClient oauthClient, InitParams params) {
    this(oauthClient, null, params);
  }
  
  /**
   * With token cleaner. Token will be removed after timeout.
   * @param oauthClient the ExoOAuthClient instance.
   * @param tokenCleaner the TokenCleaner.  
   * @param params initialized parameters.
   */
  public OAuthConsumerServiceImpl(ExoOAuthClient oauthClient,
      OAuthTokenCleaner tokenCleaner, InitParams params) {
    this.oauthClient = oauthClient;
    Iterator<PropertiesParam> iterator = params.getPropertiesParamIterator();

    ValueParam t = params.getValueParam("tokenAliveTime");
    tokenAliveTime = t != null ? Long.parseLong(t.getValue()) * 60 * 1000
        : DEFAULT_TOKEN_ALIVE_TIME;
    LOG.info("Token alive time is : " + tokenAliveTime + " ms.");

    // Create consumers
    while (iterator.hasNext()) {
      PropertiesParam pp = iterator.next();
      String name = pp.getName();
      String secret = pp.getProperty("secret");
      String description = pp.getProperty("description");
      
      String providerTokenRequestURL = pp.getProperty("provider.tokenRequestURL");
      String providerAuthorizationURL = pp.getProperty("provider.authorizationURL");
      String providerAccessTokenURL = pp.getProperty("provider.accessTokenURL");

      // Callback URL is passed as null, it must be configured on the Provider side.
      OAuthConsumer consumer = new OAuthConsumer(null, name, secret,
          new OAuthServiceProvider(providerTokenRequestURL, providerAuthorizationURL,
              providerAccessTokenURL)
          );
      consumer.setProperty("name", name);
      consumer.setProperty("description", description);
      consumers.put(name, consumer);
    }
    
    // Pass Collection of OAuthAccessor to OAuthTokenCleaner.
    if (tokenCleaner != null) {
      tokenCleaner.setTokens(tokens);
    } else {
      LOG.warn("Running without token cleaner. All tokens will be never expired!");
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public OAuthAccessor getAccessor(OAuthMessage oauthMessage) throws OAuthProblemException {
    try {
      oauthMessage.requireParameters(OAuth.OAUTH_CONSUMER_KEY);
      
      String token = oauthMessage.getParameter(OAuth.OAUTH_TOKEN);
      String secret = oauthMessage.getParameter(OAuth.OAUTH_TOKEN_SECRET);
      
      Iterator<OAuthAccessor> iter = tokens.iterator();
      OAuthAccessor accessor = null;
      if (token != null && secret != null) {
      
        while (iter.hasNext()) {
          OAuthAccessor a = iter.next();
  
          if (a.accessToken != null && a.accessToken.equals(token)
              && a.tokenSecret != null && a.tokenSecret.equals(secret)) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Access token found: " + token);
            }
            // Nothing to do here, token is valid.
            return a;
          }
          
          if (a.requestToken != null && a.requestToken.equals(token)
              && a.tokenSecret != null && a.tokenSecret.equals(secret)) {
            
            if (LOG.isDebugEnabled()) {
              LOG.debug("Request token found: " + token);
            }
            // Remove old one.
            iter.remove();
            accessor = a;
            break;
          }
        }
      
      }
      
      if (accessor != null) {
        // Get modified accessor with access token and new secret token.
        oauthClient.getAccessToken(accessor);
        // Set life time for token.
        accessor.setProperty("expired", System.currentTimeMillis() + tokenAliveTime);
      } else {
        OAuthConsumer consumer = getConsumer(oauthMessage.getConsumerKey());
        // Create new accessor if no one found.
        accessor = new OAuthAccessor(consumer);
        // Get request token for it.
        oauthClient.getRequestToken(accessor);
        
        if (LOG.isDebugEnabled()) {
          LOG.debug("Request and access token not found or not valid, "
              + "generate a new request token " + accessor.requestToken);
        }
      }
      
      // Save new token.
      tokens.add(accessor);
      return accessor;
    } catch (Exception e) {
      e.printStackTrace();
      throw new OAuthProblemException(e.getMessage());
    }
  }
  
  /**
   * {@inheritDoc}
   */
  public void removeAccessor(OAuthMessage oauthMessage) throws OAuthProblemException {
    try {
      String token = oauthMessage.getParameter(OAuth.OAUTH_TOKEN);
      String secret = oauthMessage.getParameter(OAuth.OAUTH_TOKEN_SECRET);

      Iterator<OAuthAccessor> iter = tokens.iterator();
      if (token != null && secret != null) {

        while (iter.hasNext()) {
          OAuthAccessor a = iter.next();

          if (a.accessToken != null && a.accessToken.equals(token)
              && a.tokenSecret != null && a.tokenSecret.equals(secret)) {
            if (LOG.isDebugEnabled()) {
              LOG.debug("Remove access token found: " + token);
            }

            iter.remove();
            break;
          }
        }
      }
    } catch (IOException e) {
      LOG.error("Can't remove access token.");
    }
  }
 
  /**
   * Get consumer with specified name.
   * @param name the consumer's name.
   * @return consumer.
   * @throws OAuthProblemException if consumer with specified name not found.
   */
  private OAuthConsumer getConsumer(String name) throws OAuthProblemException {
    OAuthConsumer consumer = consumers.get(name);
    if (consumer != null)
      return consumer;
    throw new OAuthProblemException("There is no appropriate consumer for '"
        + name + "'.");
  }
  
}
