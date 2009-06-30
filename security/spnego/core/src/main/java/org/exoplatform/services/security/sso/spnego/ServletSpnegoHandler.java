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

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.codec.binary.Base64;
import org.exoplatform.services.log.Log;
import org.exoplatform.services.log.ExoLogger;

/**
 * Servlet specialisation of SpnegoHandler that introduces the Base64 encoding
 * and transportation of <code>WWW-Authenticate</code> and
 * <code>Authroization</code> headers.
 * @author Martin Algesten
 */
public class ServletSpnegoHandler extends SpnegoHandler {

  /**
   * Logger.
   */
  static final Log LOG = ExoLogger.getLogger("core.sso.ServletSpnegoHandler");

  /**
   * The server header (WWW-Authenticate).
   */
  private static final String HEADER_WWW_AUTHENTICATE = "WWW-Authenticate";

  /**
   * The client header (Authoriation).
   */
  private static final String HEADER_AUTHORIZATION = "Authorization";

  /**
   * The prefix used by SPNEGO before the Base64 encoded GSS-API token
   * (Negotiate).
   */
  private static final String NEG_TOKEN = "Negotiate";

  /**
   * Expects "Negotiate &lt;base64 token>" input and returns the same kind of
   * output. It will decode and send the base64 encoded data to
   * {@link #authenticate(byte[])}.
   * @param challenge The challenge string from client such as
   *            <code>"Negotiate ab3qfd32..."</code>
   * @return Null if {@link #authenticate(byte[])} return null or the encoded
   *         result as <code>"Negotiate 8sdf832hdf..."</code>
   */
  public String authenticate(String challenge) {
    if (challenge != null && challenge.startsWith(NEG_TOKEN)) {
      // substring NEG_TOKEN+1 is chopping off "Negotiate "
      String s = challenge.substring(NEG_TOKEN.length() + 1);
      // token is set to incoming token
      byte[] token = Base64.decodeBase64(s.getBytes());
      // token is set to outogoing token.
      token = authenticate(token);
      if (token == null)
        return null;
      
      return NEG_TOKEN + " " + new String(new Base64().encode(token));
    } else {
      if (isComplete()) {
        return null;
      } else {
        // This starts the negotiation (Just returning "Negotiate").
        return NEG_TOKEN;
      }
    }
  }

  /**
   * Method that will in order do:
   * <ol>
   * <li>Inspect the current state of the handler. If {@link #isComplete()} is
   * true it will return imediatelly and do nothing more.
   * <li>Check if the client has supplied any "Authorization" header, and if so
   * and the header starts "Negotiate", it will try to call
   * {@link #authenticate(String)} using the value of the header. Otherwise it
   * will call {@link #authenticate(String)} with a null value to start the
   * authentication.
   * <li>If the {@link #authenticate(String)} method return a non-null result
   * this will be set as a header "WWW-Authenticate" in the response.
   * <li>If {@link #isEstablished()} is not true, it will finally call
   * <code>response.sendError(401)</code>.
   * </ol>
   * @param request the request object to check for headers.
   * @param response the response object ot set headers and perhaps do a
   *            <code>sendError(401)</code> on.
   * @throws IOException from the servlet api.
   */
  public void authenticate(HttpServletRequest request,
      HttpServletResponse response) throws IOException {

    if (isComplete())
      return;
    if (LOG.isDebugEnabled()) {
      LOG.debug("Entering Spnego authentication remote address: " 
          + request.getRemoteAddr());
    }
    String authorization = request.getHeader(HEADER_AUTHORIZATION);

    if (authorization != null && !authorization.startsWith(NEG_TOKEN))
      authorization = null;

    if (authorization != null) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Recevied client token: "
            + authorization);
      }
    }

    String responseToken = authenticate(authorization);

    if (responseToken != null) {
      response.setHeader(HEADER_WWW_AUTHENTICATE, responseToken);
      if (LOG.isDebugEnabled()) {
        LOG.debug("Sending response token: " + responseToken);
      }
    }

    if (!isEstablished()) {
      if (LOG.isDebugEnabled()) {
        LOG.debug("Sending 401 response code.");
      }
      response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
    }

  }

}
