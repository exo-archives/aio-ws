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

import javax.servlet.ServletConfig;
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
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Credential;
//import org.exoplatform.services.security.IdentityRegistry;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;
import org.exoplatform.ws.security.oauth.OAuthProviderService;

/**
 * NOTE This servlet MUST be under <security-constraint>.
 * Configure it in web.xml.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthAuthenticationServlet extends HttpServlet {

  private static final long serialVersionUID = -876006996763499606L;
  
  private final static Log log = ExoLogger.getLogger("ws.security.OAuthAuthorizationServlet");
  
  /**
   * Login page name. Should be describe in web.xml, as context-param with name login-page.
   */
  protected String loginPage; 

  /* (non-Javadoc)
   * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    this.loginPage = config.getServletContext().getInitParameter("login-page");
    if (loginPage == null)
      throw new ServletException("Login page name not found in web.xml, "
      		+ "must be set with parameter name 'login-page'.");
  }
  
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
    try {
      OAuthAccessor accessor = providerService.getAccessor(oauthMessage);
      // Accessor can has only request token and secret token.
      // If current accessor was marked as authorized in some other way.
      if (Boolean.TRUE.equals(accessor.getProperty("authorized"))) {
        if (log.isDebugEnabled()) {
          log.debug("Already authorized, request token: " + accessor.requestToken 
              + ", secret token: " + accessor.tokenSecret);
        }
        
        sendBack(httpRequest, httpResponse, accessor);
        return;
      }

      // do authentication
      String username = httpRequest.getParameter("username");
      String password = httpRequest.getParameter("password");
      if (username == null || username.length() == 0
          || password == null || password.length() == 0) {
        httpRequest.getRequestDispatcher(loginPage).forward(httpRequest, httpResponse);
        return;
      }
      
      String userId = null;
      Authenticator authenticator = (Authenticator) container
          .getComponentInstanceOfType(Authenticator.class);
//      IdentityRegistry identityRegistry = (IdentityRegistry) container
//          .getComponentInstanceOfType(IdentityRegistry.class);
      Credential[] credentials = new Credential[]{ new UsernameCredential(username),
          new PasswordCredential(password) };
      
      try {
        userId = authenticator.validateUser(credentials);
        // Identity will be not created here. Instead this must be used filter on consumer side.
//        identityRegistry.register(authenticator.createIdentity(userId));
      } catch (Exception e) {
        e.printStackTrace();
        httpRequest.getRequestDispatcher(loginPage + "?usename=" + username).forward(
            httpRequest, httpResponse);
        return;
      }
      
      // authentication success, authorize token 
      providerService.authorize(accessor, userId);
      if (log.isDebugEnabled()) {
        log.debug("Authorized, request token: " + accessor.requestToken 
            + ", secret token: " + accessor.tokenSecret + ", userId: " + userId);
      }
      
      sendBack(httpRequest, httpResponse, accessor);
    } catch (OAuthProblemException e) {
      throw new ServletException(e);
    } catch (IOException e) {
      throw new ServletException(e);
    }
  }

  private void sendBack(HttpServletRequest httpRequest, HttpServletResponse httpResponse,
      OAuthAccessor accessor) throws IOException {
    if (log.isDebugEnabled()) {
      log.debug("Redirect to callback URL: " + accessor.consumer.callbackURL);
    }
    
//    httpResponse.setStatus(HttpServletResponse.SC_TEMPORARY_REDIRECT);
    httpResponse.setStatus(HttpServletResponse.SC_MOVED_TEMPORARILY);
    httpResponse.setHeader("Location",
        OAuth.addParameters(accessor.consumer.callbackURL,
            OAuth.OAUTH_TOKEN, accessor.requestToken,
            OAuth.OAUTH_TOKEN_SECRET, accessor.tokenSecret,
            OAuth.OAUTH_CONSUMER_KEY, accessor.consumer.consumerKey,
            "returnTo", httpRequest.getParameter("returnTo")) );
  }
  
}

