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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.component.BaseComponentPlugin;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.ws.AbstractMultiWebService;
import org.exoplatform.services.ws.AbstractSingletonWebService;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $
 */
public class WebServiceLoader {

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
   * Java classes for services which came from external plugin.
   */
  private final List<Class<?>>              jcs = new ArrayList<Class<?>>();

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
   * Register all available container components in a CXF engine from Servlet.
   */
  @SuppressWarnings("unchecked")
  public void init() {

    // Deploy Single services
    singleservices = (List<AbstractSingletonWebService>) container.getComponentInstancesOfType(AbstractSingletonWebService.class);
    Collections.sort(singleservices, COMPARATOR);
    if (LOG.isDebugEnabled())
      LOG.debug("WebServiceLoader.init() singleservices = " + singleservices);
    for (AbstractSingletonWebService implementor : singleservices) {
      String address = getAddress(implementor);
      if (address != null) {
        ExoDeployCXFUtils.simpleDeployService(address, implementor);
        LOG.info("New singleton WebService '" + address + "' registered.");
      }
    }

    // Deploy Multi services
    multiservices = (List<AbstractMultiWebService>) container.getComponentInstancesOfType(AbstractMultiWebService.class);
    Collections.sort(multiservices, COMPARATOR);
    if (LOG.isDebugEnabled())
      LOG.debug("WebServiceLoader.init() multiservices = " + multiservices);
    for (AbstractMultiWebService implementor : multiservices) {
      String address = getAddress(implementor);
      if (address != null) {
        ExoDeployCXFUtils.complexDeployServiceMultiInstance(address, implementor, null);
        LOG.info("New multi-instance WebService '" + address + "' registered.");
      }
    }

    // Deploy Custom services
    if (LOG.isDebugEnabled())
      LOG.debug("WebServiceLoader.init() customservices = " + jcs);
    Collections.sort(jcs, COMPARATOR);
    for (Class<?> implementor : jcs) {
      try {
        Object implem = implementor.newInstance();
        String address = getAddress(implem);
        if (address != null) {
          ExoDeployCXFUtils.simpleDeployService(address, implem);
          LOG.info("New custom WebService '" + address + "' registered.");
        }
      } catch (Exception e) {
        LOG.error("Error at implementor.newInstance()", e);
      }
    }
  }

  /**
   * Get service address with WebService annotation for implementor.
   * 
   * @param implementor
   * @return
   */
  private String getAddress(Object implementor) {
    String serviceName = implementor.getClass().getAnnotation(WebService.class).serviceName();
    String portName = implementor.getClass().getAnnotation(WebService.class).portName();

    String address = new String();
    if (serviceName != null && serviceName.length() != 0)
      address += "/" + serviceName;

    if (portName != null && portName.length() != 0)
      address += "/" + portName;

    if (LOG.isDebugEnabled()) {
      LOG.debug("loadBus() - address = " + address);
      LOG.debug("loadBus() - implementor = " + implementor);
    }
    return address;
  }

  /**
   * Add plugin component for deploy service.
   * 
   * @param plugin
   */
  public void addPlugin(BaseComponentPlugin plugin) {
    if (plugin instanceof WebServiceLoaderPlugin)
      jcs.addAll(((WebServiceLoaderPlugin) plugin).getJcs());
  }

  /**
   * Comparator for deploy order.
   */
  private static final Comparator<Object> COMPARATOR = new Comparator<Object>() {

    public int compare(Object o1, Object o2) {
      return o1.getClass().getName().compareTo(o2.getClass().getName());
    }
    
  };

}
