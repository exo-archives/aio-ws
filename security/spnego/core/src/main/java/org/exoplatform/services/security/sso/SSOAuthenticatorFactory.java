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

package org.exoplatform.services.security.sso;

import org.exoplatform.services.security.sso.config.Config;
import org.exoplatform.services.security.sso.ntlm.NTLMAuthenticator;
import org.exoplatform.services.security.sso.spnego.SpnegoAuthenticator;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SSOAuthenticatorFactory {
  
  private static SSOAuthenticatorFactory authFactory;
  
  /*
   * Only one instance of factory. 
   */
  private SSOAuthenticatorFactory() {
  }
  
  public static SSOAuthenticatorFactory getInstance() {
    return authFactory == null ? authFactory = new SSOAuthenticatorFactory() : authFactory;
  }
  
  /**
   * @param mechanism the mechanism name
   * @return authenticator for given mechanism or null if mechanism is not supported.
   */
  public SSOAuthenticator newAuthenticator(String mechanism) {
    if (mechanism.equalsIgnoreCase(Config.HTTP_NEGOTIATE))
      return new SpnegoAuthenticator(); 
    if (mechanism.equalsIgnoreCase(Config.HTTP_NTLM))
      return new NTLMAuthenticator(); 
    return null;
  }
  
}

