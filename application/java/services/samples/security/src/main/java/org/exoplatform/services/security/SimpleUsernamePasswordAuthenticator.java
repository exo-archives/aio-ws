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

package org.exoplatform.services.security;

import javax.security.auth.login.LoginException;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SimpleUsernamePasswordAuthenticator implements Authenticator {

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.security.Authenticator#createIdentity(java.lang.String)
   */
  public Identity createIdentity(String arg0) throws Exception {
    return new Identity(arg0);
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.security.Authenticator#validateUser(org.exoplatform.services.security.Credential[])
   */
  public final String validateUser(Credential[] credentials) throws LoginException, Exception {
    String user = null;
    String password = null;
    for (Credential cred : credentials) {
      if (cred instanceof UsernameCredential)
        user = ((UsernameCredential) cred).getUsername();
      if (cred instanceof PasswordCredential)
        password = ((PasswordCredential) cred).getPassword();
    }
    if (user == null || password == null)
      throw new LoginException("Username or Password is not defined");

    if (user.equalsIgnoreCase(password))
      return user;

    throw new LoginException("Authentication for " + user + " failed!");
  }

}
