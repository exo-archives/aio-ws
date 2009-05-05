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

package org.exoplatform.services.security.sso.config;

import org.exoplatform.container.xml.InitParams;
import org.exoplatform.container.xml.PropertiesParam;
import org.picocontainer.Startable;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SSOConfigurator implements Startable {
  
  /**
   * @param params initialized parameters.
   */
  public SSOConfigurator(InitParams params) {
    PropertiesParam pparams = params.getPropertiesParam("sso-properties");
    if (pparams == null)
      throw new IllegalArgumentException("Properties parameters 'sso-properties' is null.");
    Config config = Config.getInstance();
    config.setCharset(pparams.getProperty("charset"));
    config.setDomain(pparams.getProperty("domain"));
    config.setJaasContext(pparams.getProperty("jaas-context"));
    config.setLdapServer(pparams.getProperty("ldap-server"));
    config.setRedirectOnError(pparams.getProperty("redirect-on-error"));
    
    String crossDomain = pparams.getProperty("cross-domain");
    if (crossDomain != null && "true".equalsIgnoreCase(crossDomain))
      config.setCrossDomain(true);
    String userIdAttr = pparams.getProperty("user-id-attribute");
    if (userIdAttr != null)
      config.setUserIdAttr(userIdAttr);
    String userURL = pparams.getProperty("user-url");
    if (userURL != null)
      config.setUserURL(userURL);
  }

  /**
   * {@inheritDoc}
   */
  public void start() {
  }

  /**
   * {@inheritDoc}
   */
  public void stop() {
  }
  
}

