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
package org.exoplatform.services.ws.impl.cxf;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.URL;

import javax.xml.ws.Endpoint;

import org.apache.commons.logging.Log;
import org.apache.cxf.bus.CXFBusFactory;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.interceptor.LoggingInInterceptor;
import org.apache.cxf.interceptor.LoggingOutInterceptor;
import org.apache.cxf.jaxb.JAXBDataBinding;
import org.apache.cxf.jaxws.JAXWSMethodInvoker;
import org.apache.cxf.jaxws.JaxWsServerFactoryBean;
import org.apache.cxf.service.Service;
import org.apache.cxf.service.invoker.BeanInvoker;
import org.apache.cxf.service.invoker.Factory;
import org.apache.cxf.service.invoker.PerRequestFactory;
import org.apache.cxf.service.invoker.PooledFactory;
import org.exoplatform.services.log.ExoLogger;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $ Sep 29, 2008
 */
public class CXFUtils {

  /**
   * Logger.
   */
  private static Log log = ExoLogger.getLogger(CXFUtils.class);

  /**
   * Check connection at the <code>address</code> and print.
   * 
   * @param address url of a service
   * @throws java.net.ConnectException
   * @throws Exception
   */
  public static void checkConnectionAndPrint(String address) throws java.net.ConnectException,
                                                            Exception {
    if (log.isDebugEnabled())
      log.debug("Check service at the address = " + address);

    URL url = new URL(address + "?wsdl");
    InputStream inputStream = url.openStream();
    Reader isr = new InputStreamReader(inputStream);
    Reader in = new BufferedReader(isr);
    int c;
    if (log.isDebugEnabled()) {
      System.out.println(">>> CXFUtils.checkConnection() = \n");
      System.out.println(">>> ========================================== ");
      while ((c = in.read()) != -1) {
        System.out.print((char) c);
      }
      in.close();
      System.out.println("\n>>> ========================================== ");
    }
  }

  /**
   * Complex deploy service.
   * 
   * @param address
   * @param object
   */
  public static Server complexDeployService(String address, Object object) {
    if (log.isDebugEnabled())
      log.debug("Starting Service: object = " + object + " at the address = " + address);

    JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
//serverFactory.setBindingFactory(new HttpBindingInfoFactoryBean());
    serverFactory.getServiceFactory().setDataBinding(new JAXBDataBinding());
    serverFactory.setServiceClass(object.getClass());
    serverFactory.setAddress(address);
    serverFactory.setBus(CXFBusFactory.getDefaultBus());
    Server server = serverFactory.create();
    serverFactory.getServiceFactory()
                 .getService()
                 .getInInterceptors()
                 .add(new LoggingInInterceptor());
    serverFactory.getServiceFactory()
                 .getService()
                 .getOutInterceptors()
                 .add(new LoggingOutInterceptor());
    Service service = server.getEndpoint().getService();
    service.setInvoker(new BeanInvoker(object));
    server.start();
    return server;
  }

  /**
   * Complex deploy multi-instance service.
   * 
   * @param address
   * @param object
   */
  public static Server complexDeployServiceMultiInstance(String address,
                                                         Object object,
                                                         Integer poolSize) {
    if (log.isDebugEnabled())
      log.debug("Starting Service: object = " + object + " at the address = " + address
          + " with pool size is '" + poolSize + "'");

    Factory factory = new PerRequestFactory(object.getClass());

    JaxWsServerFactoryBean serverFactory = new JaxWsServerFactoryBean();
//serverFactory.setBindingFactory(new HttpBindingInfoFactoryBean());
    serverFactory.getServiceFactory().setDataBinding(new JAXBDataBinding());
    serverFactory.setServiceClass(object.getClass());
    serverFactory.setAddress(address);
    //  If the purpose is to make sure a single request enters the instance at a time
    //  (not thread safe), you can do: 
    factory = new PooledFactory(factory, poolSize != null ? poolSize.intValue() : 4);
    JAXWSMethodInvoker invoker = new JAXWSMethodInvoker(factory);
    serverFactory.setInvoker(invoker);
    Server server = serverFactory.create();
    serverFactory.getServiceFactory()
                 .getService()
                 .getInInterceptors()
                 .add(new LoggingInInterceptor());
    serverFactory.getServiceFactory()
                 .getService()
                 .getOutInterceptors()
                 .add(new LoggingOutInterceptor());
    Service service = server.getEndpoint().getService();
    service.setInvoker(new BeanInvoker(object));
    server.start();
    return server;
  }

  /**
   * Simple deploy service.
   * 
   * @param address
   * @param object
   */
  public static Endpoint simpleDeployService(String address, Object object) {
    if (log.isDebugEnabled())
      log.debug("Starting Service: object = " + object + " at the address = " + address);
    Endpoint endpoint = Endpoint.publish(address, object);
    if (endpoint.isPublished())
      log.info("The WebService '" + address + "' has been published!");
    return endpoint;
  }

}
