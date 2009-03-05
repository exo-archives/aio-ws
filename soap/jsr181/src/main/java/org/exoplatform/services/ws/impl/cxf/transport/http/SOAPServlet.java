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
package org.exoplatform.services.ws.impl.cxf.transport.http;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.commons.logging.Log;
import org.apache.cxf.Bus;
import org.apache.cxf.BusFactory;
import org.apache.cxf.transport.servlet.CXFNonSpringServlet;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.ws.impl.cxf.WebServiceLoader;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $ Sep 17, 2008
 */
public class SOAPServlet extends CXFNonSpringServlet {

  /**
   * ExoContainer.
   */
  private ExoContainer     container;

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(SOAPServlet.class);

  /**
   * Load bus.
   * 
   * @see org.apache.cxf.transport.servlet.CXFNonSpringServlet#loadBus(javax.servlet.ServletConfig)
   */
  @Override
  public void loadBus(ServletConfig servletConfig) throws ServletException {
    super.loadBus(servletConfig);
    if (LOG.isDebugEnabled())
      LOG.debug("loadBus method entering");

    container = ExoContainerContext.getCurrentContainer();
    if (LOG.isDebugEnabled())
      LOG.debug("SOAPServlet.loadBus() container = " + container);

    WebServiceLoader loader = (WebServiceLoader) container.getComponentInstance(WebServiceLoader.class);
    if (LOG.isDebugEnabled())
      LOG.debug("SOAPServlet.loadBus() loader = " + loader);
    loader.init();

    Bus bus = getBus();
    BusFactory.setDefaultBus(bus);
  }

}
