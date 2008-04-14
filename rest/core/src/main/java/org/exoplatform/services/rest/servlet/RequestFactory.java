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
import java.io.InputStream;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.Request;
import org.exoplatform.services.rest.ResourceIdentifier;

/**
 * RequestFactory helps create REST request from HttpServletRequest.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RequestFactory {

  /*
   * Class has only static methods.
   */
  private RequestFactory() {
  }
  
  private static final Log log = ExoLogger.getLogger("ws.rest.core.RequestFactory");

  /**
   * Create REST request.
   * @param httpRequest HTTP request.
   * @return REST request REST request.
   * @throws IOException Input/Output Exception.
   */
  public static Request createRequest(HttpServletRequest httpRequest)
      throws IOException {
    String pathInfo = httpRequest.getPathInfo();
    String method = httpRequest.getMethod();
    MultivaluedMetadata headerParams = parseHttpHeaders(httpRequest);
    MultivaluedMetadata queryParams = parseQueryParams(httpRequest);
    Map<String, org.exoplatform.services.rest.Cookie> cookies = getCookies(httpRequest);

    InputStream in = httpRequest.getInputStream();
    String port = (httpRequest.getServerPort() == 80) ? "" : ":" + httpRequest.getServerPort();

    String baseURI = httpRequest.getScheme() + "://" +
        httpRequest.getServerName() + port + httpRequest.getContextPath() +
        httpRequest.getServletPath();

    ResourceIdentifier identifier = new ResourceIdentifier(httpRequest
        .getServerName(), baseURI, pathInfo);
    return new Request(in, identifier, method, headerParams, queryParams, cookies);
  }

  /* Parse headers from HTTP request.
   * @param httpRequest HttpServletRequest.
   * @return Map provide HTTP header in structure Map &lt; String, Enumeration
   *         &lt; String &gt; &gt;.
   */
  private static MultivaluedMetadata parseHttpHeaders(
      HttpServletRequest httpRequest) {
    MultivaluedMetadata headerParams = new MultivaluedMetadata();
    Enumeration<?> temp = httpRequest.getHeaderNames();
    while (temp.hasMoreElements()) {
      String k = (String) temp.nextElement();
      Enumeration<?> e = httpRequest.getHeaders(k);
      while (e.hasMoreElements()) {
        headerParams.putSingle(k, (String) e.nextElement());
        if (log.isDebugEnabled()) {
          log.debug(k + " = " + headerParams.get(k));
        }
      }
    }
    return headerParams;
  }

  /* Parse query parameters from HTTP request.
   * @param httpRequest HttpServletRequest.
   * @return Map provide HTTP query parameters in structure Map &gt; String,
   *         String[] &gt;.
   */
  private static MultivaluedMetadata parseQueryParams(
      HttpServletRequest httpRequest) {
    MultivaluedMetadata queryParams = new MultivaluedMetadata();
    Enumeration<?> temp = httpRequest.getParameterNames();
    while (temp.hasMoreElements()) {
      String k = (String) temp.nextElement();
      String[] params = httpRequest.getParameterValues(k);
      for (int i = 0; i < params.length; i++) {
        queryParams.putSingle(k, params[i]);
        if (log.isDebugEnabled()) {
          log.debug(k + " = " + params[i]);
        }
      }
    }
    return queryParams;
  }
  
  private static Map<String, org.exoplatform.services.rest.Cookie> getCookies(
      HttpServletRequest httpRequest) {
    javax.servlet.http.Cookie[] sc = httpRequest.getCookies();
    if (sc == null)
      return null;
    Map<String, org.exoplatform.services.rest.Cookie> cookies =
      new HashMap<String, org.exoplatform.services.rest.Cookie>();
    for (javax.servlet.http.Cookie c : sc) {
      org.exoplatform.services.rest.Cookie cookie =
        new org.exoplatform.services.rest.Cookie(c.getName(), c.getValue(),
            c.getPath(), c.getDomain(), c.getVersion());
      if (c.getComment() != null)
        cookie.setComment(c.getComment());
      if (log.isDebugEnabled()) {
        log.debug("Cookie found in request: " + c);
      }
      cookies.put(cookie.getName().toLowerCase(), cookie);
    }
    return cookies;
  }

}
