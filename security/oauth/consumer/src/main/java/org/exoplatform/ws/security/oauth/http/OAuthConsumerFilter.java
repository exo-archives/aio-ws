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

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.jaas.UserPrincipal;
import org.exoplatform.ws.security.oauth.OAuthConsumerService;

import net.oauth.OAuth;
import net.oauth.OAuthAccessor;
import net.oauth.OAuthMessage;
import net.oauth.server.OAuthServlet;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthConsumerFilter implements Filter {
  
  private String consumerName;
  
  private final static Log log = ExoLogger.getLogger("ws.security.OAuthConsumerFilter");  

  /* (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy() {
  }

  /* (non-Javadoc)
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse,
   * javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;
    
    // If token is not passed as parameter try to find it in cookie. 
    OAuthMessage oauthMessage = OAuthServlet.getMessage(httpRequest, null);
    Cookie[] cookies = httpRequest.getCookies();
    if (oauthMessage.getParameter(OAuth.OAUTH_TOKEN) == null) {
      Cookie c = CookieUtils.findCookie(consumerName + '.' + OAuth.OAUTH_TOKEN, cookies);
      if (c != null)
        oauthMessage.addParameter(OAuth.OAUTH_TOKEN, c.getValue());
    }
    // Do the same for secret token.
    if (oauthMessage.getParameter(OAuth.OAUTH_TOKEN_SECRET) == null) {
      Cookie c = CookieUtils.findCookie(consumerName + '.' + OAuth.OAUTH_TOKEN_SECRET, cookies);
      if (c != null)
        oauthMessage.addParameter(OAuth.OAUTH_TOKEN_SECRET, c.getValue());
    }
      
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    OAuthConsumerService consumerService = (OAuthConsumerService) container
        .getComponentInstanceOfType(OAuthConsumerService.class);

    try {
      oauthMessage.addParameter(OAuth.OAUTH_CONSUMER_KEY, consumerName);
      
      OAuthAccessor accessor = consumerService.getAccessor(oauthMessage);
      if (accessor.accessToken != null) {
        if (log.isDebugEnabled()) {
          log.debug("Access grant, oauth_token " + accessor.accessToken
              + ", oauth_secret_token " + accessor.tokenSecret);
        }
        if (accessor.getProperty("userId") != null) {
          if (log.isDebugEnabled()) {
            log.debug("userId " + accessor.getProperty("userId"));
          }
          
          UserPrincipal principal = new UserPrincipal((String) accessor.getProperty("userId"));
          request.setAttribute("oauth_principal", principal);
        }
        chain.doFilter(request, response);
        return;
      }
      if (accessor.requestToken != null) {
        String authorizationURL = accessor.consumer.serviceProvider.userAuthorizationURL;
        if (log.isDebugEnabled()) { 
          log.debug("Request token generated, request will be redirected to URL '"
              + authorizationURL + "' for authorization." );
        }
        
        httpResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
        // Keep full destination URL.
        String url = httpRequest.getRequestURL().toString();
        String query = httpRequest.getQueryString();
        if (query != null)
          url += '?' + query;
        httpResponse.setHeader("Location", 
            OAuth.addParameters(authorizationURL,
                OAuth.OAUTH_TOKEN, accessor.requestToken,
                OAuth.OAUTH_TOKEN_SECRET, accessor.tokenSecret,
                "returnTo", OAuth.percentEncode(url))
            );
      }
    } catch (Exception e) {
      e.printStackTrace();
      throw new ServletException(e);
    }
  }
  

  /* (non-Javadoc)
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig config) throws ServletException {
    consumerName = config.getInitParameter("consumer");
    if (consumerName == null) 
      throw new ServletException("Consumer name is not found in filter init parameters!");
  }

}

