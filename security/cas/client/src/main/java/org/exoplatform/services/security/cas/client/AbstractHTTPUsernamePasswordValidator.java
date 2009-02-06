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

package org.exoplatform.services.security.cas.client;

import java.io.IOException;

import javax.security.auth.login.LoginException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.Authenticator;
import org.exoplatform.services.security.Credential;
import org.exoplatform.services.security.PasswordCredential;
import org.exoplatform.services.security.UsernameCredential;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public abstract class AbstractHTTPUsernamePasswordValidator extends HttpServlet {

  /**
   * Logger. 
   */
  protected Log log = ExoLogger.getLogger("ws.security.AbstractHTTPUsernamePasswordValidator");
  
  /**
   *  Validate username/password.
   *  {@inheritDoc}
   */
  @Override
  public final void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException,  IOException {
    
    String username = request.getParameter("username");
    String password = request.getParameter("password");
    
    ExoContainer container = getContainer();
    
    Authenticator authenticator = (Authenticator) container.getComponentInstanceOfType(Authenticator.class);
    try {
      if (authenticator == null) 
        throw new LoginException("authenticator not found, check configuration!");
      
      if (username == null || password == null) 
        throw new LoginException("username or password was not specified!");
      
      Credential[] credentials = new Credential[] {
        new UsernameCredential(username), new PasswordCredential(password) };
      
      authenticator.validateUser(credentials);
      if (log.isDebugEnabled()) {
        log.debug("authentication success for " + username);
      }
      
      sendSuccessMessage(response, username);
      
    } catch (Exception e) {
      log.error("authentication failed for " + username);
      e.printStackTrace();
      sendFailMessage(response, username);
    }
    
  }
  
  /**
   * For customize authentication success message.
   * @param response the HTTPServletResponse.
   * @param principal the principal's name.
   * @throws IOException if i/o error occurs.
   */
  protected abstract void sendSuccessMessage(HttpServletResponse response, String principal) throws IOException;

  /**
   * For customize authentication fail message.
   * @param response the HTTPServletResponse.
   * @param principal the principal's name.
   * @throws IOException if i/o error occurs.
   */
  protected abstract void sendFailMessage(HttpServletResponse response, String principal) throws IOException;
  
  /**
   * @return the actual container.
   */
  protected abstract ExoContainer getContainer();

}
