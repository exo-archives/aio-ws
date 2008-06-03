/*
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
package org.exoplatform.ws.frameworks.servlet;

import java.net.MalformedURLException;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.naming.InitialContextInitializer;

/**
 * Created by The eXo Platform SAS        .<br/>
 * Servlet context initializer that initializes standalone container at the context startup time.
 * To activate this your web.xml have to be configured like:
 * <listener>
 * <listener-class>org.exoplatform.frameworks.web.common.StandaloneContainerInitializedListener</listener-class>
 * </listener>
 * You may also specify an URL to the configuration.xml stored the configuration for StandaloneContainer
 * as servlet's init parameter called 'org.exoplatform.container.standalone.config'
 * @author <a href="mailto:gennady.azarenkov@exoplatform.com">Gennady Azarenkov</a>
 * @version $Id: StandaloneContainerInitializedListener.java 6739 2006-07-04 14:34:49Z gavrikvetal $
 */

public class StandaloneContainerInitializedListener implements
    ServletContextListener {

  /**
   * org.exoplatform.container.standalone.config
   */
  private static final String CONF_URL_PARAMETER = "org.exoplatform.container.standalone.config";
  
  //private final static String CONTAINER_CONFIG = "conf/exo-configuration.xml";

  private StandaloneContainer container;

  /* (non-Javadoc)
   * @see javax.servlet.ServletContextListener#contextInitialized(javax.servlet.ServletContextEvent)
   */

  public void contextInitialized(ServletContextEvent event) {
    String configurationURL = event.getServletContext().getInitParameter(CONF_URL_PARAMETER);
    /*if(configurationURL == null) {
      configurationURL = Thread.currentThread().getContextClassLoader().getResource(
          CONTAINER_CONFIG).toString();
    }*/
    try {
      StandaloneContainer.addConfigurationURL(configurationURL);
      //if configurationURL is still == null StandaloneContainer will search
      // "exo-configuration.xml" in root of AS, then "conf/exo-configuration.xml"
      //in current classpath, then "conf/standalone/configuration.xml" in current classpath 
    } catch (MalformedURLException e1) {
    }

    try {
      container = StandaloneContainer.getInstance(Thread.currentThread()
          .getContextClassLoader());


      // Patch for tomcat InitialContext
      InitialContextInitializer ic = (InitialContextInitializer) container
          .getComponentInstanceOfType(InitialContextInitializer.class);
      if (ic != null)
        ic.recall();
      // ////////////////////////////////

      event.getServletContext().setAttribute("org.exoplatform.frameworks.web.eXoContainer",
          container);
    } catch (Exception e) {
      e.printStackTrace();
    }

  }

  /*
   * (non-Javadoc)
   *
   * @see javax.servlet.ServletContextListener#contextDestroyed(javax.servlet.ServletContextEvent)
   */
  public void contextDestroyed(ServletContextEvent event) {
    //container.stop();
  }
}
