/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.ws.examples.jsr181;

import java.io.FileOutputStream;
import java.util.List;

import junit.framework.TestCase;

import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.ws.AbstractWebService;

import org.codehaus.xfire.XFireFactory;
import org.codehaus.xfire.XFire;
import org.codehaus.xfire.service.Service;
import org.codehaus.xfire.annotations.AnnotationServiceFactory;
import javax.jws.WebService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class SayHelloWebServiceTest extends TestCase {
  
  private StandaloneContainer container;
  private final static String SERVICE_NAME = "SayHelloService";
  
  public void setUp() throws Exception {
    StandaloneContainer.setConfigurationPath("src/test/java/conf/test-configuration.xml");
    container = StandaloneContainer.getInstance();
  }
  
  public void testSayHelloService() throws Exception {
    List < AbstractWebService > l =
      container.getComponentInstancesOfType(AbstractWebService.class);
    for (AbstractWebService sc : l) {
      if (sc.getClass().getAnnotation(WebService.class).serviceName().equals(SERVICE_NAME)) {
        XFire xfire = XFireFactory.newInstance().getXFire();
        assertNotNull(xfire);
        AnnotationServiceFactory annotationServiceFactory = new AnnotationServiceFactory();
        Service s = annotationServiceFactory.create(sc.getClass());
        xfire.getServiceRegistry().register(s);
        assertTrue(xfire.getServiceRegistry().hasService(SERVICE_NAME));
        System.out.println("SayHelloService, name: "
            + xfire.getServiceRegistry().getService(SERVICE_NAME).getName());
        xfire.generateWSDL(s.getSimpleName(), new FileOutputStream("target/SayHelloService.wsdl"));
      }
    }
  }

}
