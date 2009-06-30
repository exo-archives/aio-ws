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

import net.oauth.OAuth;
import net.oauth.OAuthMessage;
import net.oauth.OAuthProblemException;
import net.oauth.server.OAuthServlet;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.ws.security.oauth.OAuthConsumerService;

/**
 * Can be used in logout process, client MUST pass parameter query parameter
 * logout=yes. In filter init parameters must present consumer name as it done
 * for OAuthConsumerFilter, otherwise exception will be thrown. Usually can be
 * configured last in web.xml, for sure after OAuthConsumerFilter.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthLogoutFilter implements Filter {

  /**
   * Consumer name.
   */
  private String           consumerName;

  /**
   * URL for redirect after logout.
   */
  private String           redirectToUrl;

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(OAuthLogoutFilter.class.getName());

  /**
   * {@inheritDoc}
   */
  public void destroy() {
    // nothing to do
  }

  /**
   * {@inheritDoc}
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
                                                                                           ServletException {

    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpServletResponse httpResponse = (HttpServletResponse) response;

    String logout = request.getParameter("logout");

    if (logout != null && "yes".equals(logout.toLowerCase())) {
      Cookie tokenCookie = null;
      Cookie secretCookie = null;
      // If token is not passed as parameter try to find it in cookie.
      // If cookie is present then they will be removed on client side.
      OAuthMessage oauthMessage = OAuthServlet.getMessage(httpRequest, null);
      Cookie[] cookies = httpRequest.getCookies();
      if (oauthMessage.getParameter(OAuth.OAUTH_TOKEN) == null) {
        Cookie c = CookieUtils.findCookie(consumerName + "." + OAuth.OAUTH_TOKEN, cookies);
        if (c != null) {
          oauthMessage.addParameter(OAuth.OAUTH_TOKEN, c.getValue());
          // token cookie presents
          tokenCookie = c;
        }
      }
      // Do the same for secret token.
      if (oauthMessage.getParameter(OAuth.OAUTH_TOKEN_SECRET) == null) {
        Cookie c = CookieUtils.findCookie(consumerName + "." + OAuth.OAUTH_TOKEN_SECRET, cookies);
        if (c != null) {
          oauthMessage.addParameter(OAuth.OAUTH_TOKEN_SECRET, c.getValue());
          // secret cookie presents
          secretCookie = c;
        }
      }

      ExoContainer container = ExoContainerContext.getCurrentContainer();
      OAuthConsumerService consumerService = (OAuthConsumerService) container.getComponentInstanceOfType(OAuthConsumerService.class);

      oauthMessage.addParameter(OAuth.OAUTH_CONSUMER_KEY, consumerName);

      try {
        // remove accessor if token and secret token passes with parameters or
        // in cookies
        consumerService.removeAccessor(oauthMessage);
      } catch (OAuthProblemException e) {
        LOG.error("Can't remove accessor.");
      }

      // remove token cookie
      if (tokenCookie != null)
        httpResponse.addCookie(CookieUtils.deleteCookie(tokenCookie));
      // remove secret cookie
      if (secretCookie != null)
        httpResponse.addCookie(CookieUtils.deleteCookie(secretCookie));

      if (redirectToUrl != null) {
        httpResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
        httpResponse.setHeader("Location", redirectToUrl);
        return;
      }
    }
    chain.doFilter(httpRequest, httpResponse);
  }

  /**
   * {@inheritDoc}
   */
  public void init(FilterConfig config) throws ServletException {
    consumerName = config.getInitParameter("consumer");
    if (consumerName == null)
      throw new ServletException("Consumer name is not found in filter init parameters!");
    // optinal, can be null. If not set user will not be redirected after logout
    redirectToUrl = config.getInitParameter("redirectToUrl");
  }

}
