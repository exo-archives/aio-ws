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

import org.exoplatform.services.log.Log;
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

  /**
   * SpnegoHandler.
   */
  private SpnegoHandler handler;
  
  /**
   * Response to the client. Can be null if server has nothing to say.
   */
  private byte[] sendBackToken;

  /**
   * @see org.ietf.jgss.GSSName .
   */
  private GSSName name;
  
  /**
   * User name.
   */
  private String username;
  
  /**
   * User principal. 
   */
  private Principal principal;

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("ws.security.SpnegoAuthenticator");
  
  /**
   * Constructs instance of SpnegoAuthenticator.
   */
  public SpnegoAuthenticator() {
    handler = new SpnegoHandler();
  }
  
  /**
   * {@inheritDoc}
   */
  public void doAuthenticate(byte[] token) throws Exception {
    sendBackToken = handler.authenticate(token);
  }

  /**
   * {@inheritDoc}
   */
  public Principal getPrincipal() {
    if (principal != null)
      return principal;
    
    String name = getUser();
    if (name != null)
      return principal = new UserPrincipal(name);
    
    return null;
  }

  /**
   * {@inheritDoc}
   */
  public byte[] getSendBackToken() {
    return sendBackToken;
  }

  /**
   * {@inheritDoc}
   */
  public String getUser() {
    if (username != null)
      return username;
    
    if (name == null) {
      try {
        name = handler.getGSSContext().getSrcName();
      } catch (GSSException e) {
        LOG.error("GSSContext is not established!", e);
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

  /**
   * {@inheritDoc}
   */
  public boolean isComplete() {
    return handler.isComplete();
  }
  
  /**
   * {@inheritDoc}
   */
  public boolean isSuccess() {
    return handler.isEstablished();
  }

}

