/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
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

  private ExoContainer container;
  private ExoContainerContext containerContext;
  private XFire xfire;
  private AnnotationServiceFactory serviceFactory;
  private List < AbstractWebService > services;
  
  private final static Log logger = ExoLogger.getLogger(WebServiceLoader.class);

  
  public WebServiceLoader(ExoContainerContext context) {
    containerContext = context;
    container = containerContext.getContainer();
    createXFire();
    createFactory();
  }
  
  private void createXFire() {
    if (xfire == null) {
      this.xfire = XFireFactory.newInstance().getXFire();
    }
  }
  
  private void createFactory() {
    if (serviceFactory == null) {
      this.serviceFactory = new AnnotationServiceFactory();
    }
  }
  
  private ServiceRegistry getRegisrty() {
    return xfire.getServiceRegistry();
  }
  
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
      getRegisrty().register(ws);
      logger.info("New WebService " + ws.getName() + " registered.");
    }
  }
  
  public void stop() {
  }

}
