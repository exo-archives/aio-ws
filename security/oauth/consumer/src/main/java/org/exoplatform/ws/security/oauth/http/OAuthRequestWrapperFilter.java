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
import java.security.Principal;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class OAuthRequestWrapperFilter implements Filter {

  private final static Log log = ExoLogger.getLogger("ws.security.OAuthRequestWrapperFilter");  

  /* (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy() {
    // nothing to do
  }

  /* (non-Javadoc)
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
   * javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest request, ServletResponse response,
      FilterChain chain) throws IOException, ServletException {
    
    HttpServletRequest httpRequest = (HttpServletRequest) request;
    HttpSession session = httpRequest.getSession(false);
    Principal principal = null;
    if (session != null && session.getAttribute("oauth_principal") != null)
      principal = (Principal) session.getAttribute("oauth_principal");
    else 
      principal = (Principal) httpRequest.getAttribute("oauth_principal");
    
    if (log.isDebugEnabled()) {
      log.debug("user principal: " + principal);
    }
    
    OAuthHttpServletRequestWrapper requestWrapper = new OAuthHttpServletRequestWrapper(
        httpRequest, principal);
    chain.doFilter(requestWrapper, response);
  }

  /* (non-Javadoc)
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig arg0) throws ServletException {
    // nothing to do
  }
  
  
  final class OAuthHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private final Principal principal;

    OAuthHttpServletRequestWrapper(final HttpServletRequest request, final Principal principal) {
        super(request);
        this.principal = principal;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
        return this.principal;
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {
        return getUserPrincipal().getName();
    }
  }

}

