/**
 * Copyright (C) 2003-2007 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */

package org.exoplatform.services.ws.impl.cxf;

import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.ws.AbstractMultiWebService;
import org.exoplatform.services.ws.AbstractSingletonWebService;
import org.picocontainer.Startable;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $
 */
public class WebServiceLoader implements Startable {

  /**
   * ExoContainer.
   */
  private ExoContainer                      container;

  /**
   * Contains list of container component which implements.
   * {@link AbstractSingletonWebService} .
   */
  private List<AbstractSingletonWebService> singleservices;

  /**
   * Contains list of container component which implements.
   * {@link AbstractMultiWebService} .
   */
  private List<AbstractMultiWebService>     multiservices;

  /**
   * Logger.
   */
  private static final Log                  LOG = ExoLogger.getLogger(WebServiceLoader.class);

  /**
   * Constructs instance of WebServiceLoader.
   * 
   * @param containerContext the ExoContainer context.
   */
  public WebServiceLoader(ExoContainerContext containerContext) {
    container = containerContext.getContainer();
    if (LOG.isDebugEnabled())
      LOG.debug("WebServiceLoader.WebServiceLoader() container = " + container);
  }

  /**
   * Register all available container components in a CXF engine.
   */
  public void init() {

    singleservices = (List<AbstractSingletonWebService>) container.getComponentInstancesOfType(AbstractSingletonWebService.class);
    if (LOG.isDebugEnabled())
      LOG.debug("WebServiceLoader.init() singleservices = " + singleservices);
    for (AbstractSingletonWebService implementor : singleservices) {
      String address = "/" + implementor.getClass().getAnnotation(WebService.class).serviceName();
      if (LOG.isDebugEnabled()) {
        LOG.debug("loadBus() - single address = " + address);
        LOG.debug("loadBus() - single implementor = " + implementor);
      }
      CXFUtils.simpleDeployService(address, implementor);
      LOG.info("New singleton WebService '" + address + "' registered.");
    }

    multiservices = (List<AbstractMultiWebService>) container.getComponentInstancesOfType(AbstractMultiWebService.class);
    if (LOG.isDebugEnabled())
      LOG.debug("WebServiceLoader.init() multiservices = " + multiservices);
    for (AbstractMultiWebService implementor : multiservices) {
      String address = "/" + implementor.getClass().getAnnotation(WebService.class).serviceName();
      if (LOG.isDebugEnabled()) {
        LOG.debug("loadBus() - multi address = " + address);
        LOG.debug("loadBus() - multi implementor = " + implementor);
      }
      CXFUtils.complexDeployServiceMultiInstance(address, implementor, null);
      LOG.info("New multi-instance WebService '" + address + "' registered.");
    }

  }

  /**
   * Start. {@inheritDoc}
   */
  public void start() {
    if (LOG.isDebugEnabled())
      LOG.debug("WebServiceLoader.start() entering.");
  }

  /**
   * Stop. {@inheritDoc}
   */
  public void stop() {
    if (LOG.isDebugEnabled())
      LOG.debug("WebServiceLoader.stop() entering.");
  }

}
