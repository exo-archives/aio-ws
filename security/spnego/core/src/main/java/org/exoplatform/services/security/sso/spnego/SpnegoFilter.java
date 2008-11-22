/* Copyright 2006 Taglab Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License
 */
package org.exoplatform.services.security.sso.spnego;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;

/**
 * Filter that stores a ServletSpnegoHandler in session attribute
 * <code>com.taglab.support.spnego.SpnegoFilter</code>. It then invokes
 * {@link ServletSpnegoHandler#authenticate(HttpServletRequest, HttpServletResponse)}
 * to perform any necessary authorization. If the authorization is successful,
 * the chain is processed. If the authorization fails, a 403 Forbidden reponse
 * code is sent.
 * <p>
 * The filter does by default catch exceptions to not display stack traces in
 * the browser. Wether or not to catch them can be controlled by the init
 * parameter <code>catchExceptions</code>. If an exception is caught, a 500
 * response is sent to the client and the stack trace dumped to stdout.
 * {@link #setCatchExceptions(boolean)}.
 * <p>
 * Upon successfully established context, the filter can instead of letting the
 * request through the chain, optionally redirect to another url. This is set
 * with init parameter <code>redirectOnEstablished</code>. The path is webapp
 * relative. {@link #setRedirectOnEstablished(String)}.
 * <p>
 * If the context is fails, the filter can instead of doing a 403 Forbidden
 * redirect to another url. This is set with init parameter
 * <code>redirectOnFailed</code>. The path is webapp relative.
 * {@link #setRedirectOnFailed(String)}.
 * @author Martin Algesten
 * 
 * @deprecated In eXo implementation SSOAuthenticationFilter must be used instead this.
 */
public class SpnegoFilter implements Filter {

  /**
   * Logger.
   */
  static final Log LOG = ExoLogger.getLogger("core.sso.SpnegoFilter");

  /**
   * The name of the session attribute we bind the handler to.
   * "spnego.ServletSpnegoHandler".
   */
  public static final String HANDLER_ATTRIBUTE_KEY = "spnego.ServletSpnegoHandler";

  /**
   * A webapp relative resource to redirect to on successful authentication.
   * This setting shortcuts the default where the request is just let through if
   * the context is established.
   */
  private String redirectOnEstablished = null;

  /**
   * A webapp relative resource to redirect to on failed authentication.
   */
  private String redirectOnFailed = null;

  /**
   * Flag that indicates if exceptions should be caught and not displayed in the
   * browser. This is turned on by default since the end user should not see
   * exception stack traces in a live environment. This can be overridden
   * setting the filter init parameter <code>catchExceptions</code> to false.
   */
  private boolean catchExceptions = true;

  /**
   * {@inheritDoc}
   */
  public void destroy() {
    // nothing to do.
  }

  /**
   * {@inheritDoc}
   */
  public void doFilter(ServletRequest inrequest, ServletResponse inresponse,
      FilterChain chain) throws IOException, ServletException {

    HttpServletRequest request = (HttpServletRequest) inrequest;
    HttpServletResponse response = (HttpServletResponse) inresponse;

    ServletSpnegoHandler handler = extractServletSpnegoHandlerFromSession(request);

    try {
      if (handler == null) {
        if (LOG.isDebugEnabled()) {
          LOG.debug("Binding new ServletSpnegoHandler to session");
        }
        handler = bindNewServletSpnegoHandlerInSession(request);
      }
      handler.authenticate(request, response);
    } catch (SpnegoException spne) {
      if (!catchExceptions)
        throw spne;
      LOG.error("Caught exception", spne);
      if (!response.isCommitted())
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    if (handler.isEstablished()) {
      if (redirectOnEstablished != null) {
        response.sendRedirect(redirectOnEstablished);
      } else {
        chain.doFilter(request, response);
      }
    } else if (!response.isCommitted()) {
      if (redirectOnFailed != null) {
        response.sendRedirect(redirectOnFailed);
      } else {
        response.sendError(HttpServletResponse.SC_FORBIDDEN);
      }
    }
  }

  /**
   * Utility method for obtaining the {@link ServletSpnegoHandler} bound to the session.
   * @param request HttpServletRequest.
   * @return ServletSpnegoHandler.
   */
  public static ServletSpnegoHandler extractServletSpnegoHandlerFromSession(
      HttpServletRequest request) {
    HttpSession session = request.getSession(false);
    if (session == null)
      return null;
    return (ServletSpnegoHandler) session.getAttribute(HANDLER_ATTRIBUTE_KEY);
  }

  /**
   * Utility method for setting a new {@link ServletSpnegoHandler} in the session.
   * @param request HttpServletRequest.
   * @return ServletSpnegoHandler.
   */
  public static ServletSpnegoHandler bindNewServletSpnegoHandlerInSession(
      HttpServletRequest request) {
    HttpSession session = request.getSession();
    ServletSpnegoHandler handler = new ServletSpnegoHandler();
    session.setAttribute(HANDLER_ATTRIBUTE_KEY, handler);
    return handler;
  }

  /**
   * {@inheritDoc}
   */
  public void init(FilterConfig config) throws ServletException {
    catchExceptions = !"false".equals(config
        .getInitParameter("catchExceptions"));
    redirectOnEstablished = config.getInitParameter("redirectOnEstablished");
    redirectOnFailed = config.getInitParameter("redirectOnFailed");
  }

  /**
   * @param catchExceptions the catchExceptions to set
   */
  public void setCatchExceptions(boolean catchExceptions) {
    this.catchExceptions = catchExceptions;
  }

  /**
   * @param redirectOnEstablished the redirectOnEstablished to set
   */
  public void setRedirectOnEstablished(String redirectOnEstablished) {
    this.redirectOnEstablished = redirectOnEstablished;
  }

  /**
   * @param redirectOnFailed the redirectOnFailed to set
   */
  public void setRedirectOnFailed(String redirectOnFailed) {
    this.redirectOnFailed = redirectOnFailed;
  }

}
