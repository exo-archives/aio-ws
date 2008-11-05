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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import org.apache.cxf.Bus;
import org.apache.cxf.BusException;
import org.apache.cxf.BusFactory;
import org.apache.cxf.resource.ResourceManager;
import org.apache.cxf.transport.DestinationFactory;
import org.apache.cxf.transport.DestinationFactoryManager;
import org.apache.cxf.transport.servlet.ServletContextResourceResolver;
import org.apache.cxf.transport.servlet.ServletTransportFactory;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.ws.impl.cxf.WebServiceLoader;
import org.exoplatform.test.mocks.servlet.MockServletContext;

/**
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $
 */
public class WebServiceLoaderTest extends BaseTest {

  @Override
  public void setUp() throws Exception {
    System.out.println(">>> WebServiceLoaderTest.setUp() = entered ======================= ");
    super.setUp();
  }
  
  /**
   * Test say hello service.
   * 
   * @throws Exception
   */
  @SuppressWarnings("unchecked")
  public void testWebServiceLoader() throws Exception {
    System.out.println(">>> WebServiceLoaderTest.testWebServiceLoader()");
    try {
      ExoContainerContext containerContext = new ExoContainerContext(container);
      WebServiceLoader webServiceLoader = new WebServiceLoader(containerContext);
      loadBusNoConfig(null);
      webServiceLoader.init();
      bus.shutdown(true);
    } catch (Exception e) {
      e.printStackTrace();
      fail("There is an exception in the WebServiceLoaderTest.testWebServiceLoader():"
          + e.getMessage());
    }
  }

  /*
   * From CXFNonSpringServlet and AbstractCXFServlet.
   */

  /**
   * Bus.
   */
  protected Bus                     bus;

  /**
   * Servlet transport factory.
   */
  protected ServletTransportFactory servletTransportFactory;

  /**
   * Loading bus with no config.
   * 
   * @param servletConfig
   * @throws ServletException
   */
  private void loadBusNoConfig(ServletConfig servletConfig) throws ServletException {
    if (bus == null) {
      bus = BusFactory.newInstance().createBus();
    }
    ResourceManager resourceManager = bus.getExtension(ResourceManager.class);
    resourceManager.addResourceResolver(new ServletContextResourceResolver(new MockServletContext()));
    replaceDestinationFactory();
  }

  /**
   * Replace destination factory.
   */
  protected void replaceDestinationFactory() {
    DestinationFactoryManager dfm = bus.getExtension(DestinationFactoryManager.class);
    try {
      DestinationFactory df = dfm.getDestinationFactory("http://cxf.apache.org/transports/http/configuration");
      if (df instanceof ServletTransportFactory) {
        servletTransportFactory = (ServletTransportFactory) df;
        return;
      }
    } catch (BusException e) {
    }
    DestinationFactory factory = createServletTransportFactory();
    for (String s : factory.getTransportIds()) {
      registerTransport(factory, s);
    }
  }

  /**
   * Registering transport.
   * 
   * @param factory
   * @param namespace
   */
  private void registerTransport(DestinationFactory factory, String namespace) {
    bus.getExtension(DestinationFactoryManager.class)
       .registerDestinationFactory(namespace, factory);
  }

  /**
   * Create servlet transport factory.
   * 
   * @return
   */
  protected DestinationFactory createServletTransportFactory() {
    if (servletTransportFactory == null) {
      servletTransportFactory = new ServletTransportFactory(bus);
    }
    return servletTransportFactory;
  }

}
