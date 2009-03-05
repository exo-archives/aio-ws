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

package org.exoplatform.services.organization.auth.sso;

import java.util.List;

import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.security.sso.jndi.JndiAction;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class AuthenticatorTest extends TestCase {
  
  ExoContainer container;
  
  /* (non-Javadoc)
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    StandaloneContainer.setConfigurationPath(Thread.currentThread()
        .getContextClassLoader().getResource("conf/portal/configuration.xml").getPath());
    container = StandaloneContainer.getInstance();
  }
  
  public void testJndiAction() throws Exception {
    String jaasconf = Thread.currentThread().getContextClassLoader()
        .getResource("conf/portal/jaas.conf").getPath();
    // edit this for your configuration.
    System.setProperty("java.security.auth.login.config", jaasconf);
    System.setProperty("java.security.krb5.kdc", "test01-srv.exoua-int");
    System.setProperty("java.security.krb5.realm", "EXOUA-INT");
    
    List<String> groups = JndiAction.getGroups("exo");
    System.out.println(groups);
  }

}

