/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
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

package org.exoplatform.services.rest.servlet;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.Connector;
import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.Response;

/**
 * This servlet is front-end for the REST engine. Servlet get HTTP request then
 * produce REST request with helps by org.exoplatform.services.rest.servlet.RequestFactory. <br/>
 * @see org.exoplatform.services.rest.servlet.RequestFactory<br/>
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RestServlet extends HttpServlet implements Connector {

  private static final long serialVersionUID = 2152962763071591181L;

  private static final Log log = ExoLogger.getLogger("ws.rest.core.RestServlet");

  /*
   * (non-Javadoc)
   * @see javax.servlet.http.HttpServlet#service(javax.servlet.http.HttpServletRequest,
   *      javax.servlet.http.HttpServletResponse)
   */
  @Override
  public void service(HttpServletRequest httpRequest,
      HttpServletResponse httpResponse) throws IOException, ServletException {
    // Current container must be set by filter.
    
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    if (log.isDebugEnabled()) { 
      log.debug("Current Container: " + container);
    }
    ResourceDispatcher dispatcher = (ResourceDispatcher) container
        .getComponentInstanceOfType(ResourceDispatcher.class);
    if (log.isDebugEnabled()) { 
      log.debug("ResourceDispatcher: " + dispatcher);
    }
    if (dispatcher == null) {
      throw new ServletException("ResourceDispatcher is null.");
    }
    try {
      Response response = dispatcher.dispatch(RequestFactory
          .createRequest(httpRequest));
      httpResponse.setStatus(response.getStatus());
      tuneResponse(httpResponse, response.getResponseHeaders(), response.getCookies());
      OutputStream out = httpResponse.getOutputStream();
      response.writeEntity(out);
      out.flush();
      out.close();
    } catch (Exception e) {
      log.error("Dispatch method error!");
      e.printStackTrace();
      httpResponse.sendError(500, "This request can't be serve by service.\n"
          + "Check request parameters and try again.");
    }
  }

  /**
   * Tune HTTP response.
   * @param httpResponse HTTP response.
   * @param responseHeaders HTTP response headers.
   * @param cookies
   */
  private void tuneResponse(HttpServletResponse httpResponse,
      MultivaluedMetadata responseHeaders,
      List<org.exoplatform.services.rest.Cookie> cookies) {
    if (responseHeaders != null) {
      HashMap<String, String> headers = responseHeaders.getAll();
      Set<String> keys = headers.keySet();
      Iterator<String> ikeys = keys.iterator();
      while (ikeys.hasNext()) {
        String key = ikeys.next();
        httpResponse.setHeader(key, headers.get(key));
      }
    }
    if (cookies != null && cookies.size() > 0) {
      for (org.exoplatform.services.rest.Cookie c : cookies) {
        javax.servlet.http.Cookie sc = new javax.servlet.http.Cookie(c.getName(), c.getValue());
        sc.setMaxAge(c.getMaxAge());
        sc.setVersion(c.getVersion());
        if (c.getComment() != null)
          sc.setComment(c.getComment());
        if (c.getDomain() != null)
          sc.setDomain(c.getDomain());
        if (c.getPath() != null)
          sc.setPath(c.getPath());
        httpResponse.addCookie(sc);
      }
    }
  }
}
