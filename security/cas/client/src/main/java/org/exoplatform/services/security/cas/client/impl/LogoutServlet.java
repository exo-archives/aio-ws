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

package org.exoplatform.services.security.cas.client.impl;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Logout from CAS if redirect URL specified add it as parameter.
 * After logout CAS will redirect user to specified URL.
 *  
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class LogoutServlet extends HttpServlet {

  private static final long serialVersionUID = -7711893422989356640L;

  private String redirectToUrl;
  
  private String casServerLogoutUrl;
  
  /* (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   * javax.servlet.http.HttpServletResponse)
   */
  @Override
  protected void service(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    
    String url = casServerLogoutUrl;
    if (redirectToUrl != null && redirectToUrl.length() > 0)
      url += "?service=" + redirectToUrl;
    
    response.sendRedirect(url);
  }

  /* (non-Javadoc)
   * @see javax.servlet.GenericServlet#init(javax.servlet.ServletConfig)
   */
  @Override
  public void init(ServletConfig config) throws ServletException {
    // must be set
    casServerLogoutUrl = config.getInitParameter("casServerLogoutUrl");
    if (casServerLogoutUrl == null)
      throw new ServletException("Init parameter casServerLogoutUrl is not set!");
    
    // optinal, can be null. If not set CAS will not redirect user after logout.
    redirectToUrl = config.getInitParameter("redirectToUrl");
    if (redirectToUrl != null) {
      try {
        redirectToUrl = URLEncoder.encode(redirectToUrl, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        e.printStackTrace();
        throw new ServletException(e.getMessage());
      }
    }
  }
  
}
