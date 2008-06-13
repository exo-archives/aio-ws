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

package org.exoplatform.ws.security.oauth.http;

import java.io.IOException;
import java.io.OutputStream;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.security.oauth.OAuthProviderService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthAccessTokenServlet extends HttpServlet {

  private static final long serialVersionUID = 2950381189568117176L;
  
  private final static Log log = ExoLogger.getLogger("ws.security.OAuthAccessTokenServlet");

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
    OAuthAccessor accessor;
    
    try {
      accessor = providerService.getAccessor(oauthMessage);
      if (log.isDebugEnabled()) {
        log.debug("Accessor: " + accessor);
      }
      
    } catch (OAuthProblemException e) {
      e.printStackTrace();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "Request token is invalid!");
      return;
    }
    
    try {
      oauthMessage.validateSignature(accessor);
    } catch (Exception e) {
      e.printStackTrace();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "Signature validation failed!");
      return;
    }
    if (!Boolean.TRUE.equals(accessor.getProperty("authorized"))
        || accessor.getProperty("userId") == null) {
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "Request token is not authorized!");
      return;
    }
    
    try {
      providerService.generateAccessToken(accessor);
    } catch (OAuthProblemException e) {
      e.printStackTrace();
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED,
          "Can't generate access token!");
      return;
    }
    
    if (log.isDebugEnabled()) {
      log.debug(accessor.accessToken + "/" + accessor.tokenSecret);
    }
    
    httpResponse.setContentType("text/plain");
    OutputStream out = httpResponse.getOutputStream();
    OAuth.formEncode(OAuth.newList(OAuth.OAUTH_TOKEN, accessor.accessToken,
        OAuth.OAUTH_TOKEN_SECRET, accessor.tokenSecret,
        "userId", (String) accessor.getProperty("userId")), out);
    out.flush();
    out.close();
  }
  
}

