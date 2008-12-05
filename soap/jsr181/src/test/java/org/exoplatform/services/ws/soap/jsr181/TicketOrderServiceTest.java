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

import java.net.URL;
import java.util.Date;
import java.util.List;

import javax.jws.WebService;
import javax.xml.ws.Endpoint;

import org.apache.cxf.endpoint.Client;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.exoplatform.services.ws.AbstractMultiWebService;
import org.exoplatform.services.ws.AbstractSingletonWebService;
import org.exoplatform.services.ws.impl.cxf.CXFUtils;
import org.exoplatform.services.ws.soap.jsr181.custom.TicketOrderServiceImpl;
import org.exoplatform.services.ws.soap.jsr181.singleton.TicketOrderService;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $
 */
public class TicketOrderServiceTest extends BaseTest {

  /**
   * Singleton service name.
   */
  private final static String SERVICE_NAME_SINGLETON        = "TicketOrderServiceSingleton";

  /**
   * Multiinstance service name.
   */
  private final static String SERVICE_NAME_MULTIINSTANCE    = "TicketOrderServiceMultiinstance";

  /**
   * Custom service name.
   */
  private final static String SERVICE_NAME_CUSTOM           = "TicketOrderServiceCustom";

  /**
   * Address for Singleton.
   */
  private final static String SERVICE_ADDRESS_SINGLETON     = "http://localhost:8080/"
                                                                + SERVICE_NAME_SINGLETON;

  /**
   * Address for Multiinstance.
   */
  private final static String SERVICE_ADDRESS_MULTIINSTANCE = "http://localhost:8080/"
                                                                + SERVICE_NAME_MULTIINSTANCE;

  /**
   * Address for Custom.
   */
  private final static String SERVICE_ADDRESS_CUSTOM        = "http://localhost:8080/"
                                                                + SERVICE_NAME_CUSTOM;

//private final static String address = "local://TicketOrderService";
//private final static String address = "http://localhost:8080/ws-examples/soap/TicketOrderService";

  @Override
  public void setUp() throws Exception {
    System.out.println(">>> TicketOrderServiceTest.setUp() = entered ======================= ");
    super.setUp();
  }

  /**
   * Test say hello service.
   * 
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void testTicketSingletonService() throws Exception {

    System.out.println(">>> TicketOrderServiceTest.testTicketSingletonService()");
    List<AbstractSingletonWebService> l = container.getComponentInstancesOfType(AbstractSingletonWebService.class);
    for (AbstractSingletonWebService sc : l) {
      if (sc.getClass()
            .getAnnotation(WebService.class)
            .serviceName()
            .equals(SERVICE_NAME_SINGLETON)) {

        // test starting service
        Endpoint endpoint = CXFUtils.simpleDeployService(SERVICE_ADDRESS_SINGLETON, sc);
        try {
          CXFUtils.checkConnectionAndPrint(SERVICE_ADDRESS_SINGLETON);
        } catch (Exception e) {
          System.out.println(">>> TicketOrderServiceTest.testTicketSingletonService() checkConnection: There is no service at '"
              + SERVICE_ADDRESS_SINGLETON + "?wsdl'");
          e.printStackTrace();
          fail(e.getMessage());
        }

        // test started service
        TicketOrderService ticket = getTicketOrderService(SERVICE_ADDRESS_SINGLETON);
        String ticketOrder = ticket.getTicket("Kyiv", "Paris", new Date(), "Passenger");
        System.out.println(">>> TicketOrderServiceTest.testTicketSingletonService() ticketOrder = "
            + ticketOrder);
        assertNotNull(ticketOrder);

        endpoint.stop();
        return;
      }
    }
    fail("There is no service with name" + SERVICE_NAME_SINGLETON);
  }

  /**
   * Test say hello service.
   * 
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void testTicketMultiinstanceService() throws Exception {

    System.out.println(">>> TicketOrderServiceTest.testTicketMultiinstanceService()");
    List<AbstractMultiWebService> l = container.getComponentInstancesOfType(AbstractMultiWebService.class);
    for (AbstractMultiWebService sc : l) {
      if (sc.getClass()
            .getAnnotation(WebService.class)
            .serviceName()
            .equals(SERVICE_NAME_MULTIINSTANCE)) {

        // test starting service
        Server server = CXFUtils.complexDeployServiceMultiInstance(SERVICE_ADDRESS_MULTIINSTANCE,
                                                                   sc,
                                                                   null);
        try {
          CXFUtils.checkConnectionAndPrint(SERVICE_ADDRESS_MULTIINSTANCE);
        } catch (Exception e) {
          System.out.println(">>> TicketOrderServiceTest.testTicketMultiinstanceService() checkConnection: There is no service at '"
              + SERVICE_ADDRESS_MULTIINSTANCE + "?wsdl'");
          e.printStackTrace();
          fail(e.getMessage());
        }

        // test started service
        TicketOrderService ticket = getTicketOrderService(SERVICE_ADDRESS_MULTIINSTANCE);
        String ticketOrder = ticket.getTicket("Kyiv", "Paris", new Date(), "Passenger");
        System.out.println(">>> TicketOrderServiceTest.testTicketMultiinstanceService() ticketOrder = "
            + ticketOrder);
        assertNotNull(ticketOrder);

        server.stop();
        return;
      }
    }
    fail("There is no service with name" + SERVICE_NAME_MULTIINSTANCE);
  }

  /**
   * Get ticket order service.
   * 
   * @return TicketOrderService
   */
  private TicketOrderService getTicketOrderService(String address) {
    JaxWsProxyFactoryBean client = new JaxWsProxyFactoryBean();
    client.setServiceClass(TicketOrderService.class);
    client.setAddress(address);
    client.getInInterceptors().add(new LoggingInInterceptor());
    client.getOutInterceptors().add(new LoggingOutInterceptor());
    Object obj = client.create();
    return (TicketOrderService) obj;
  }

  /**
   * Test say hello service.
   * 
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void testTicketServiceServiceWithTimeOut() throws Exception {

    System.out.println(">>> TicketOrderServiceTest.testTicketServiceService()");

    // test starting service
    Endpoint endpoint = CXFUtils.simpleDeployService(SERVICE_ADDRESS_CUSTOM,
                                                     new TicketOrderServiceImpl());
    try {
      CXFUtils.checkConnectionAndPrint(SERVICE_ADDRESS_CUSTOM);
    } catch (Exception e) {
      System.out.println(">>> TicketOrderServiceTest.testTicketServiceService() checkConnection: There is no service at '"
          + SERVICE_ADDRESS_CUSTOM + "?wsdl'");
      e.printStackTrace();
      fail(e.getMessage());
    }

    // test started service
    URL wsdl = new URL(SERVICE_ADDRESS_CUSTOM + "?wsdl");
    assertNotNull(wsdl);

    TicketOrderServiceService service = new TicketOrderServiceService(wsdl);
    org.exoplatform.services.ws.soap.jsr181.custom.TicketOrderService ticket = service.getTicketOrderServicePort();

    Client client = ClientProxy.getClient(ticket);
    HTTPConduit http = (HTTPConduit) client.getConduit();
    HTTPClientPolicy httpClientPolicy = new HTTPClientPolicy();
    httpClientPolicy.setConnectionTimeout(100);
    httpClientPolicy.setAllowChunking(false);
    httpClientPolicy.setReceiveTimeout(100);
    http.setClient(httpClientPolicy);

    // test started service
    String ticketOrder = null;
    try {
      ticketOrder = ticket.getTicket("Kyiv", "Paris", new Date(), "Passenger");
      System.out.println(">>> TicketOrderServiceTest.testTicketSingletonService() ticketOrder = "
          + ticketOrder);
      fail();
    } catch (Exception e) {
    }

    client = ClientProxy.getClient(ticket);
    http = (HTTPConduit) client.getConduit();
    httpClientPolicy = new HTTPClientPolicy();
    httpClientPolicy.setConnectionTimeout(10000);
    httpClientPolicy.setAllowChunking(false);
    httpClientPolicy.setReceiveTimeout(10000);
    http.setClient(httpClientPolicy);

    // test started service
    ticketOrder = null;
    try {
      ticketOrder = ticket.getTicket("Kyiv", "Paris", new Date(), "Passenger");
      System.out.println(">>> TicketOrderServiceTest.testTicketSingletonService() ticketOrder = "
          + ticketOrder);
    } catch (Exception e) {
      fail();
    }

    assertNotNull(ticketOrder);

    endpoint.stop();
    return;

  }

}
