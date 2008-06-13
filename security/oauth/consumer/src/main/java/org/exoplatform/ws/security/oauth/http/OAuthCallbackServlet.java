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

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.security.oauth.OAuthConsumerService;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthCallbackServlet extends HttpServlet {

  private static final long serialVersionUID = -3941492315142762805L;
  
  private final static Log log = ExoLogger.getLogger("ws.security.OAuthCallbackServlet");
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void service(HttpServletRequest httpRequest, HttpServletResponse httpResponse) 
      throws ServletException, IOException {
    // request message
    OAuthMessage oauthMessage = OAuthServlet.getMessage(httpRequest, null);
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    OAuthConsumerService consumerService = (OAuthConsumerService) container.getComponentInstanceOfType(
        OAuthConsumerService.class);
    try {
      // Get fresh accessor.
      OAuthAccessor accessor = consumerService.getAccessor(oauthMessage);
      if (accessor.accessToken != null) {
        String returnTo = OAuth.decodePercent(httpRequest.getParameter("returnTo"));
        if (returnTo == null)
          returnTo = httpRequest.getContextPath();
        if(log.isDebugEnabled()) {
          log.debug("Get access token from Provider, client will be redirect to '"
              + returnTo + "'.");
        }
        
        httpResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
        httpResponse.setHeader("Location", returnTo);
        // Add cookie.
        httpResponse.addCookie(new Cookie(accessor.consumer.consumerKey + '.' + OAuth.OAUTH_TOKEN,
            accessor.accessToken));
        httpResponse.addCookie(new Cookie(accessor.consumer.consumerKey + '.' + OAuth.OAUTH_TOKEN_SECRET,
            accessor.tokenSecret));
      } else {
        // If access token not presents.
        httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN,
            "You have not permission for requested resource!");
      }
      
    } catch(Exception e) {
      throw new ServletException(e);
    }
    
  }

}

