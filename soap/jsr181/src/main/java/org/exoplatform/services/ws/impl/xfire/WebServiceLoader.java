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

package org.exoplatform.services.ws.impl.xfire;

import java.util.List;

import org.picocontainer.Startable;
import org.picocontainer.defaults.SimpleReference;

import org.apache.commons.logging.Log;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.service.ServiceRegistry;
import org.codehaus.xfire.service.invoker.FactoryInvoker;
import org.codehaus.xfire.service.invoker.RequestScopePolicy;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import org.codehaus.xfire.picocontainer.util.PicoFactory;

import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.ExoContainer;

import org.exoplatform.services.ws.AbstractWebService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class WebServiceLoader implements Startable {

  /**
   * ExoContainer.
   */
  private ExoContainer container;
  
  /**
   * XFire engine.
   */
  private XFire xfire;
  
  /**
   * @see {@link AnnotationServiceFactory}
   */
  private AnnotationServiceFactory serviceFactory;
  
  /**
   * Contains list of container component which implements {@link AbstractWebService} .
   */
  private List <AbstractWebService> services;
  
  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(WebServiceLoader.class);

  
  /**
   * Constructs instance of WebServiceLoader.
   * @param containerContext the ExoContainer context.
   */
  public WebServiceLoader(ExoContainerContext containerContext) {
    container = containerContext.getContainer();
    createXFire();
    createFactory();
  }
  
  /**
   * Create new XFire instance.
   */
  private void createXFire() {
    if (xfire == null) {
      this.xfire = XFireFactory.newInstance().getXFire();
    }
  }
  
  /**
   * Create service factory.
   */
  private void createFactory() {
    if (serviceFactory == null) {
      this.serviceFactory = new AnnotationServiceFactory();
    }
  }
  
  /**
   * @return the service registry.
   */
  private ServiceRegistry getRegistry() {
    return xfire.getServiceRegistry();
  }
  
  /**
   * Register all available container components as services in XFire engine.
   * {@inheritDoc} 
   */
  public void start() {
    services = container.getComponentInstancesOfType(AbstractWebService.class);
    for (AbstractWebService as : services) {
      Service ws = serviceFactory.create(as.getClass());
      SimpleReference simpleReference = new SimpleReference();
      simpleReference.set(container);
      // New instance of service for each request.
      // Also add <multi-instance>true</multi-instance> in configuration.xml
      ws.setInvoker(new FactoryInvoker(
            new PicoFactory(simpleReference, ws.getServiceInfo().getServiceClass()),
            RequestScopePolicy.instance())
      );
      getRegistry().register(ws);
      LOG.info("New WebService " + ws.getName() + " registered.");
    }
  }
  
  /**
   * {@inheritDoc} 
   */
  public void stop() {
  }

}
