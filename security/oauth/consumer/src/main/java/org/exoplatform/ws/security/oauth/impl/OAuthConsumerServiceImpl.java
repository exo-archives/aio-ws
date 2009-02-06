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
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.logging.Log;
import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.ObjectParameter;
import org.exoplatform.container.xml.PropertiesParam;
import org.exoplatform.container.xml.ValueParam;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.security.oauth.ExoOAuthClient;
import org.exoplatform.ws.security.oauth.OAuthConsumerService;
import org.exoplatform.ws.security.oauth.storage.GrantedAccessorStorage;
import org.exoplatform.ws.security.oauth.storage.GrantedAccessorStorageProperties;

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
   * Logger.
   */
  private static final Log                 LOG = ExoLogger.getLogger("ws.security.OAuthConsumerServiceImpl");

  /**
   * Temporary storage for request tokens.
   */
  private final Map<String, OAuthAccessor>  requestAccessors;

  /**
   * See {@link GrantedAccessorStorage},
   * {@link GrantedAccessorStorageProperties}.
   */
  private final GrantedAccessorStorage     grantedAccessors;

  /**
   * Known consumers.
   */
  private final Map<String, OAuthConsumer> consumers;

  /**
   * Actual ExoOAuthClient.
   */
  private final ExoOAuthClient             oauthClient;

  /**
   * @param params initialized parameters
   * @throws Exception if any errors occurs
   */
  @SuppressWarnings("unchecked")
  public OAuthConsumerServiceImpl(InitParams params) throws Exception {
    requestAccessors = new ConcurrentHashMap<String, OAuthAccessor>();
    consumers = new HashMap<String, OAuthConsumer>();

    ValueParam t = params.getValueParam(CONSUMER_CLIENT_PROPERTY);
    if (t == null)
      throw new RuntimeException("oAuth token storage is not configured, check configuration.");
    oauthClient = (ExoOAuthClient) Class.forName(t.getValue()).newInstance();

    t = params.getValueParam(CONSUMER_STORAGE_PROPERTY);
    if (t == null)
      throw new RuntimeException("oAuth token storage is not configured, check configuration.");
    grantedAccessors = (GrantedAccessorStorage) Class.forName(t.getValue()).newInstance();

    ObjectParameter tokenStorageProperties = params.getObjectParam(CONSUMER_STORAGE_PROPERTIES_PROPERTY);
    if (tokenStorageProperties != null)
      grantedAccessors.init((GrantedAccessorStorageProperties) tokenStorageProperties.getObject());

    // Create consumers
    Iterator<PropertiesParam> iterator = params.getPropertiesParamIterator();
    while (iterator.hasNext()) {
      PropertiesParam p = iterator.next();
      String name = p.getName();
      String secret = p.getProperty(CONSUMER_SECRET_KEY_PROPERTY);
      String description = p.getProperty(CONSUMER_DESCRIPTION_PROPERTY);

      String providerTokenRequestURL = p.getProperty(PROVIDER_TOKEN_REQUEST_URL_PROPERTY);
      String providerAuthorizationURL = p.getProperty(PROVIDER_AUTHORIZATION_URL_PROPERTY);
      String providerAccessTokenURL = p.getProperty(PROVIDER_TOKEN_ACCESS_URL_PROPERTY);

      // Callback URL is passed as null, it must be configured on the Provider side
      OAuthConsumer consumer = new OAuthConsumer(null,
                                                 name,
                                                 secret,
                                                 new OAuthServiceProvider(providerTokenRequestURL,
                                                                          providerAuthorizationURL,
                                                                          providerAccessTokenURL));
      consumer.setProperty(CONSUMER_NAME_PROPERTY, name);
      consumer.setProperty(CONSUMER_DESCRIPTION_PROPERTY, description);
      consumer.setProperty(REMMEMBER_MAX_AGE_PROPERTY, p.getProperty(REMMEMBER_MAX_AGE_PROPERTY));
      consumers.put(name, consumer);
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

      OAuthAccessor accessor = null;
      if (token != null && secret != null) {
        // Try to find in access tokens
        accessor = grantedAccessors.getAccessor(token, secret);
        
        if (LOG.isDebugEnabled())
          LOG.debug("Access token found: " + token);

        // Nothing to do here, token is valid.
        if (accessor != null)
          return accessor;

        // Try to find in request tokens
        accessor = requestAccessors.remove(token);
        if (accessor != null) {
          if (accessor.tokenSecret != null) {
            
            if (secret == null || !secret.equals(accessor.tokenSecret)) 
              throw new OAuthProblemException("Secret token is invalid or null.");

            if (LOG.isDebugEnabled())
              LOG.debug("Request token found: " + token);
            
          }
        }

      }

      if (accessor != null) { // accessor with request tokens
        // Get modified accessor with access token and new secret token.
        oauthClient.getAccessToken(accessor);
        grantedAccessors.addAccessor(accessor);
      } else { // no accessor, it is first request
        OAuthConsumer consumer = getConsumer(oauthMessage.getConsumerKey());
        // Create new accessor if no one found.
        accessor = new OAuthAccessor(consumer);
        // Get request token for it.
        oauthClient.getRequestToken(accessor);
        requestAccessors.put(accessor.requestToken, accessor);
        if (LOG.isDebugEnabled())
          LOG.debug("Request and access token not found or not valid, "
              + "generate a new request token " + accessor.requestToken);

      }
      return accessor;
    } catch (Exception e) {
      if (LOG.isDebugEnabled())
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

      grantedAccessors.removeAccessor(token);
    } catch (IOException e) {
      if (LOG.isDebugEnabled())
        e.printStackTrace();
    }
  }

  /**
   * Get consumer with specified name.
   * 
   * @param name the consumer's name.
   * @return consumer.
   * @throws OAuthProblemException if consumer with specified name not found.
   */
  private OAuthConsumer getConsumer(String name) throws OAuthProblemException {
    OAuthConsumer consumer = consumers.get(name);
    if (consumer != null)
      return consumer;
    throw new OAuthProblemException("There is no appropriate consumer for " + name);
  }

}
