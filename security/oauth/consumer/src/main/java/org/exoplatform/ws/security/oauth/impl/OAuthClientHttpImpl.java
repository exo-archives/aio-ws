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

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;

import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.common.http.HTTPStatus;
import org.exoplatform.common.http.client.HTTPConnection;
import org.exoplatform.common.http.client.HTTPResponse;
import org.exoplatform.common.http.client.NVPair;
import org.exoplatform.ws.security.oauth.ExoOAuthClient;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthClientHttpImpl implements ExoOAuthClient {

  /* (non-Javadoc)
   * @see org.exoplatform.ws.security.oauth.ExoOAuthClient#getAccessToken(net.oauth.OAuthAccessor)
   */
  public void getAccessToken(OAuthAccessor accessor) throws Exception {
    List<OAuth.Parameter> p1 = new ArrayList<OAuth.Parameter>();
    p1.add(new OAuth.Parameter(OAuth.OAUTH_TOKEN, accessor.requestToken));
    p1.add(new OAuth.Parameter(OAuth.OAUTH_TOKEN_SECRET, accessor.tokenSecret));
    OAuthMessage request = accessor.newRequestMessage(HTTPMethods.POST,
        accessor.consumer.serviceProvider.accessTokenURL, p1);
    
    URL url = new URL(request.URL);
    // NOTE: This list of parameters is not the same as previous one.
    // New list contains all parameters needed for checking signature
    // on Provider side.
    List<Map.Entry<String, String>> p2 = request.getParameters();
    NVPair[] nvp = new NVPair[p2.size()];

    int i = 0;
    for (Map.Entry<String, String> e : p2)
      nvp[i++] = new NVPair(e.getKey(), e.getValue());

    HTTPConnection conn = new HTTPConnection(url);
    HTTPResponse response = conn.Post(url.getPath(), nvp);
    if (response.getStatusCode() != HTTPStatus.OK) {
      throw new OAuthProblemException("Provider status code "
          + response.getStatusCode() + ".");
    }
    
    // Process response
    List<OAuth.Parameter> tokens = OAuth.decodeForm(response.getText());
    for (OAuth.Parameter token : tokens) {
      String key = token.getKey();
      String value = token.getValue();
      if (OAuth.OAUTH_TOKEN.equalsIgnoreCase(key))
        accessor.accessToken = value;
      else if (OAuth.OAUTH_TOKEN_SECRET.equalsIgnoreCase(key))
        accessor.tokenSecret = value;
      else
        // Keep all external parameter as is.
        accessor.setProperty(key, value);
    }
    // request token will not be used any more.
    accessor.requestToken = null;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.ws.security.oauth.ExoOAuthClient#getRequestToken(net.oauth.OAuthAccessor)
   */
  public void getRequestToken(OAuthAccessor accessor) throws Exception {
    OAuthMessage request = accessor.newRequestMessage(HTTPMethods.POST,
        accessor.consumer.serviceProvider.requestTokenURL, null);
    List<Map.Entry<String, String>> p = request.getParameters();

    URL url = new URL(request.URL);
    NVPair[] p1 = new NVPair[p.size()];
    for (int i = 0; i < p1.length; i++)
      p1[i] = new NVPair(p.get(i).getKey(), p.get(i).getValue());
    
    HTTPConnection conn = new HTTPConnection(url);
    HTTPResponse response = conn.Post(url.getPath(), p1);
    if (response.getStatusCode() != HTTPStatus.OK) {
      throw new OAuthProblemException("Provider status code "
          + response.getStatusCode() + ".");
    }
    // Process response
    List<OAuth.Parameter> tokens = OAuth.decodeForm(response.getText());
    for (OAuth.Parameter token : tokens) {
      String key = token.getKey();
      String value = token.getValue();
      if (OAuth.OAUTH_TOKEN.equalsIgnoreCase(key))
        accessor.requestToken = value;
      else if (OAuth.OAUTH_TOKEN_SECRET.equalsIgnoreCase(key))
        accessor.tokenSecret = value;
      else
        // Keep all external parameter as is.
        accessor.setProperty(key, value);
    }
    accessor.accessToken = null;
  }

}

