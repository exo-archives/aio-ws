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

/**
 * 
 */
package org.exoplatform.ws.security.oauth.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthConsumer;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.ws.security.oauth.OAuthProviderService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthRequestTokenServlet extends HttpServlet {

  private static final long serialVersionUID = -6887490497735057738L;

  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
      throws ServletException, IOException {
    OAuthMessage oauthMessage = OAuthServlet.getMessage(httpRequest, null);
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    OAuthProviderService providerService = (OAuthProviderService) container
        .getComponentInstanceOfType(OAuthProviderService.class);
    OAuthConsumer consumer;
    try {
      consumer = providerService.getConsumer(oauthMessage);
    } catch (OAuthProblemException e) {
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "Consumer key is invalid!");
      return;
    }
    OAuthAccessor accessor = new OAuthAccessor(consumer);
    try {
      oauthMessage.validateSignature(accessor);
    } catch (Exception e) {
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "Signature validation failed!");
      return;
    }
    providerService.generateRequestToken(accessor);
    httpResponse.setContentType("text/plain");
    OutputStream out = httpResponse.getOutputStream();
    OAuth.formEncode(OAuth.newList(OAuth.OAUTH_TOKEN, accessor.requestToken,
        OAuth.OAUTH_TOKEN_SECRET, accessor.tokenSecret), out);
    out.flush();
    out.close();
  }
  
}

