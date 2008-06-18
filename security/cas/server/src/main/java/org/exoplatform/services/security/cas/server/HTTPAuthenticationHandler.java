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

package org.exoplatform.services.security.cas.server;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler;
import org.jasig.cas.authentication.principal.UsernamePasswordCredentials;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class HTTPAuthenticationHandler extends
    AbstractUsernamePasswordAuthenticationHandler {

  private String authenticationURL;
  
  /* (non-Javadoc)
   * @see org.jasig.cas.authentication.handler.support.AbstractUsernamePasswordAuthenticationHandler
   * #authenticateUsernamePasswordInternal(org.jasig.cas.authentication.principal.UsernamePasswordCredentials)
   */
  protected boolean authenticateUsernamePasswordInternal(
      UsernamePasswordCredentials credentials) {

    HttpURLConnection conn = null;
    boolean auth = false;
    try {
      String username = credentials.getUsername();
      String password = credentials.getPassword();
      
      StringBuffer sb = new StringBuffer();
      sb.append("username").append('=').append(URLEncoder.encode(username, "UTF-8"))
        .append('&')
        .append("password").append('=').append(URLEncoder.encode(password, "UTF-8"));
      
      URL url = new URL(authenticationURL);
      if (log.isDebugEnabled()) {
        log.debug("authentication URL " + authenticationURL);
      }
      
      conn = (HttpURLConnection) url.openConnection();
      conn.setRequestMethod("POST");
      conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
      conn.setDoInput(true);
      conn.setDoOutput(true);
      OutputStream out = conn.getOutputStream();
      out.write(sb.toString().getBytes("UTF-8"));
      out.flush();
      out.close();
    
      int status = conn.getResponseCode();
      if (status != HttpURLConnection.HTTP_OK) {
        log.error("server return status " + status);
        BufferedInputStream in = new BufferedInputStream(conn.getErrorStream());
        byte[] buff = new byte[0x2000];
        int rd = in.read(buff);
        if (rd > 0) {
          String charset = conn.getContentEncoding() == null ? "UTF-8" : conn.getContentEncoding(); 
          String message = new String(buff, 0, rd, charset);
          log.error(message);
        }
        in.close();
      } else {
        BufferedInputStream in = new BufferedInputStream(conn.getInputStream());
        byte[] buff = new byte[0x2000];
        int rd = in.read(buff);
        if (rd > 0) {
          String charset = conn.getContentEncoding() == null ? "UTF-8" : conn.getContentEncoding(); 
          String message = new String(buff, 0, rd, charset);
          if (log.isDebugEnabled()) {
            log.debug(message);
          }
          
          if (message.startsWith("yes")) {
            if (log.isDebugEnabled()) {
              log.info("authentication success for " + username);
            }
            
            auth = true;
          } else
            log.error("authentication failed, for user " + username);
          
        } else {
          log.error("server does not return response");
        }
        in.close();
      }
    } catch (IOException e) {
      e.printStackTrace();
    } finally {
      if (conn != null)
        conn.disconnect();
    }
    
    return auth;
  }
  
  /*
   * This method is called by spring framework, authenticationURL is passed from configuration.
   */
  public void setAuthenticationURL(String authenticationURL) {
    this.authenticationURL = authenticationURL;
  }
  
  public void setSecurity(boolean security) {
    log.info("................. "+security);
  }

}

