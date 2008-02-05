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

package org.exoplatform.services.ws.soap.jsr181;

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
  
  @Override
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
