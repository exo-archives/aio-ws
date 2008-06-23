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

package org.exoplatform.services.security.sso.spnego;

import java.security.Principal;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.security.jaas.UserPrincipal;
import org.exoplatform.services.security.sso.SSOAuthenticator;
import org.ietf.jgss.GSSException;
import org.ietf.jgss.GSSName;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SpnegoAuthenticator implements SSOAuthenticator {

  private SpnegoHandler handler;
  
  private byte[] sendBackToken;
  private GSSName name;
  private String username;
  private Principal principal;

  private static final Log log = ExoLogger.getLogger("ws.security.SpnegoAuthenticator");
  
  public SpnegoAuthenticator() {
    handler = new SpnegoHandler();
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.SSOAuthenticator#authenticate(byte[])
   */
  public void doAuthenticate(byte[] token) throws Exception {
    sendBackToken = handler.authenticate(token);
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.SSOAuthenticator#getPrincipal()
   */
  public Principal getPrincipal() {
    if (principal != null)
      return principal;
    
    String name = getUser();
    if (name != null)
      return principal = new UserPrincipal(name);
    
    return null;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.SSOAuthenticator#getSendBackTokens()
   */
  public byte[] getSendBackToken() {
    return sendBackToken;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.SSOAuthenticator#getUser()
   */
  public String getUser() {
    if (username != null)
      return username;
    
    if (name == null) {
      try {
        name = handler.getGSSContext().getSrcName();
      } catch (GSSException e) {
        log.error("GSSContext is not established!", e);
      }
    }
    
    if (name != null) {
      // Name returned as user@DOMAIN
      String n = name.toString();
      int d = n.indexOf('@');
      return username = n.substring(0, d);
    }
    
    return null;
  }

  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.SSOAuthenticator#isComplete()
   */
  public boolean isComplete() {
    return handler.isComplete();
  }
  
  /* (non-Javadoc)
   * @see org.exoplatform.services.organization.auth.sso.SSOAuthenticator#isSuccess()
   */
  public boolean isSuccess() {
    return handler.isEstablished();
  }

}

