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

import java.util.Date;
import java.util.List;

import javax.jws.WebService;

import junit.framework.TestCase;

import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.exoplatform.container.StandaloneContainer;
import org.exoplatform.services.ws.AbstractSingletonWebService;
import org.exoplatform.services.ws.impl.cxf.CXFUtils;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $
 */
public class TicketOrderServiceTest extends TestCase {

  /**
   * Container.
   */
  private StandaloneContainer container;

  /**
   * Service name.
   */
  private final static String SERVICE_NAME = "TicketOrderService";

  /**
   * Address.
   */
  private final static String address      = "http://localhost:8080/" + SERVICE_NAME;

//private final static String address = "local://TicketOrderService";
//private final static String address = "http://localhost:8080/ws-examples/soap/TicketOrderService";

  /**
   * Set up.
   * 
   * @see junit.framework.TestCase#setUp()
   */
  @Override
  public void setUp() throws Exception {
    StandaloneContainer.setConfigurationPath("src/test/java/conf/test-configuration.xml");
    container = StandaloneContainer.getInstance();
  }

  /**
   * Test say hello service.
   * 
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void testTicketService() throws Exception {
    System.out.println(">>> TicketOrderServiceTest.testTicketService()");
    List<AbstractSingletonWebService> l = container.getComponentInstancesOfType(AbstractSingletonWebService.class);
    for (AbstractSingletonWebService sc : l) {
      if (sc.getClass().getAnnotation(WebService.class).serviceName().equals(SERVICE_NAME)) {

        // test starting service
        CXFUtils.simpleDeployService(address, sc);
        try {
          CXFUtils.checkConnectionAndPrint(address);
        } catch (Exception e) {
          System.out.println(">>> TicketOrderServiceTest.checkConnection(): There is no service at '"
              + address + "?wsdl'");
          e.printStackTrace();
          fail(e.getMessage());
        }

        // test started service
        TicketOrderService ticket = getTicketOrderService();
        String ticketOrder = ticket.getTicket("Kyiv", "Paris", new Date(), "Passenger");
        System.out.println(">>> TicketOrderServiceTest.testTicketService() ticketOrder = "
            + ticketOrder);
        assertNotNull(ticketOrder);
        return;
      }
    }
    fail("There is no service with name" + SERVICE_NAME);
  }

  /**
   * Get ticket order service.
   * 
   * @return TicketOrderService
   */
  private TicketOrderService getTicketOrderService() {
    JaxWsProxyFactoryBean client = new JaxWsProxyFactoryBean();
    client.setServiceClass(TicketOrderService.class);
    client.setAddress(address);
    client.getInInterceptors().add(new LoggingInInterceptor());
    client.getOutInterceptors().add(new LoggingOutInterceptor());
    Object obj = client.create();
    return (TicketOrderService) obj;
  }

}
