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

package org.exoplatform.services.security.sso.http;

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
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.sso.SSOAuthenticationException;
import org.exoplatform.services.security.sso.SSOAuthenticator;
import org.exoplatform.services.security.sso.SSOAuthenticatorFactory;
import org.exoplatform.services.security.sso.config.Config;
import org.exoplatform.services.security.sso.ntlm.NTLMAuthenticator;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SSOAuthenticationFilter implements Filter {

  /*
   * NOTE: Some logs about error will be shown only in debug mode. Just one
   * reason for this, if client is out of domain it can be authenticate in some
   * other way (not NTLM or SPNEGO).
   */
  private static final Log log = ExoLogger.getLogger("ws.security.SSOAuthenticationFilter");

  /*
   * URL for alternative authentication. If user can't authenticate by this
   * filter.
   */
  private static String redirectOnError;

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#destroy()
   */
  public void destroy() {
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest,
   *      javax.servlet.ServletResponse, javax.servlet.FilterChain)
   */
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException,
      ServletException {

    HttpServletResponse httpResponse = (HttpServletResponse) response;
    HttpServletRequest httpRequest = (HttpServletRequest) request;

    Principal principal = authenticate(httpRequest, httpResponse);
    if (principal != null)
      chain.doFilter(new SSOHttpServletRequestWrapper(httpRequest, principal), httpResponse);
    else {
      // if principal is null response must be already sent
      if (!httpResponse.isCommitted())
        httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
  }

  /*
   * (non-Javadoc)
   * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
   */
  public void init(FilterConfig filterConfig) throws ServletException {
    redirectOnError = Config.getInstance().getRedirectOnError();
  }

  /**
   * Do all work about authentication.
   * @param request the request object to check for headers.
   * @param response the response object to set headers and
   *            <code>sendError(401)</code>.
   * @return Principal id authentication complete and success and null
   *         otherwise.
   * @throws IOException from the servlet API.
   */
  public final static Principal authenticate(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
      throws IOException {
    /*
     * Request is authenticated in some other way. Usually must not be happen.
     */
    Principal principal = httpRequest.getUserPrincipal();
    if (principal != null) {
      if (log.isDebugEnabled()) {
        log.debug("User " + principal + " already authenticated.");
      }

      return principal;
    }

    // Try get authenticator from session first.
    SSOAuthenticator auth = null;
    HttpSession session = httpRequest.getSession(false);
    if (session != null)
      auth = (SSOAuthenticator) session.getAttribute(Config.SSOAUTHENTICATOR_KEY);

    // Authenticator found in session
    if (auth != null) {
      if (log.isDebugEnabled()) {
        log.debug("Get authenticator from HTTP session." + " principal : " + auth.getPrincipal() +
            " authentication complete: " + auth.isComplete() + " authentication success: " + auth.isSuccess());
      }

      // Authentication complete with success.
      if (auth.isComplete() && auth.isSuccess())
        return auth.getPrincipal();

      // Authentication complete with error.
      if (auth.isComplete() && !auth.isSuccess()) {
        doFailed(httpResponse, new SSOAuthenticationException("Authentication failed!"));
        return null;
      }

    }
    // Continue if authentication not finished yet (or not started).
    String authHeader = httpRequest.getHeader("Authorization");

    if (authHeader == null) {
      // Authentication process is not started yet.
      if (log.isDebugEnabled()) {
        log.debug("No authorization headers, send WWW-Authenticate header.");
      }

      /*
       * Few WWW-Authenticate headers. WWW-Authenticate: Negotiate
       * WWW-Authenticate: NTLM NTLM should be used only if Negotiate is not
       * supported.
       */
      for (String mech : Config.getSupportedAuthenticationMechanisms())
        httpResponse.addHeader("WWW-Authenticate", mech);

      // return HTTP status 401
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }

    // authentication header presents, so process started
    int endSignature = authHeader.indexOf(' ');
    // We are waiting for NTLM or Negotiate.
    String authMechanism = authHeader.substring(0, endSignature);
    byte[] token = Base64.decodeBase64(authHeader.substring(endSignature + 1).getBytes());

    /*
     * Workstation can send NTLMSSP token with different prefix. Prefix can be
     * NTLM or Negotiate. If prefix is NTLM we can get authenticator from
     * factory by mechanism name. If not authenticator will be created here.
     */
    if (Config.HTTP_NEGOTIATE.equalsIgnoreCase(authMechanism) && NTLMAuthenticator.isNTLM(token))
      auth = SSOAuthenticatorFactory.getInstance().newAuthenticator(Config.HTTP_NTLM);

    // If authenticator not initialized yet.
    if (auth == null)
      auth = SSOAuthenticatorFactory.getInstance().newAuthenticator(authMechanism);

    // Do authentication here.
    if (auth != null) {
      try {
        auth.doAuthenticate(token);
      } catch (Exception e) {
        doFailed(httpResponse, e);
        return null;
      }
    } else {
      /*
       * Authenticator is null. Appropriated authenticator can't be created by
       * SSOAuthenticatorFactory. It can be happen if client send HTTP header
       * 'Authorize' other then NTLM or Negotiate.
       */
      doFailed(httpResponse, new SSOAuthenticationException(
          "Can't create appropriate authenticator for authenication mechanism: " + authMechanism));
      return null;
    }

    /*
     * Authentication (or one step of it) successful. Save authenticator in HTTP
     * session. Session can be null. Above we tried to get is as
     * httpRequest.getSession(false).
     */
    session = httpRequest.getSession();
    session.setAttribute(Config.SSOAUTHENTICATOR_KEY, auth);

    byte[] backToken = auth.getSendBackToken();
    /*
     * If one step of authentication successful but authentication not finished
     * yet. NTLM works in two steps. Return HTTP status 401.
     */
    if (backToken != null && !auth.isComplete()) {
      httpResponse.setHeader("WWW-Authenticate", authMechanism + " " + new String(Base64.encodeBase64(backToken)));
      httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
      return null;
    }

    return auth.getPrincipal();

  }
  
  /*
   * Check if it possible to use some alternative mechanism of authentication.
   * If it is disable send FORBIDDEN (403) status.
   */
  private static void doFailed(HttpServletResponse httpResponse, Exception e) throws IOException {
    if (redirectOnError == null) {
      // If alternative authentication is not supported show errors.
      e.printStackTrace();
      httpResponse.sendError(HttpServletResponse.SC_FORBIDDEN);
    } else {
      // If have alternative mechanism then hide errors.
      if (log.isDebugEnabled()) {
        log.debug("Thrown exception " + e.getMessage());
        e.printStackTrace();
      }
      httpResponse.sendRedirect(redirectOnError);
    }
  }
  

  final class SSOHttpServletRequestWrapper extends HttpServletRequestWrapper {

    private Principal principal;
    
    public SSOHttpServletRequestWrapper(final HttpServletRequest request, final Principal principal) {
      super(request);
      this.principal = principal;
    }
    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getRemoteUser()
     */
    @Override
    public String getRemoteUser() {
      return getUserPrincipal().getName();
    }

    /* (non-Javadoc)
     * @see javax.servlet.http.HttpServletRequestWrapper#getUserPrincipal()
     */
    @Override
    public Principal getUserPrincipal() {
      return this.principal;
    }
    
  }
  
}
