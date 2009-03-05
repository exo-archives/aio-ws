/**
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

package org.exoplatform.ws.rest.ejbconnector30;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.naming.Context;
import javax.naming.InitialContext;

import org.exoplatform.common.transport.SerialInputData;
import org.exoplatform.common.transport.SerialRequest;
import org.exoplatform.common.transport.SerialResponse;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Main {

  /**
   * URL of simple service.
   */
  private static final String URL = "/simple-service/";

  /**
   * Sample data.
   */
  private static final String DATA = "Hello world";

  /**
   * Default name of bean in easybeans container on Jonas.
   */
  private static final String JNDI_NAME = "org.exoplatform.ws.rest.ejbconnector30.RestEJBConnector" 
    + "_" + RestEJBConnectorRemote.class.getName() + "@Remote";

  /**
   * Looking for bean.
   * @return the home interface of bean.
   * @throws Exception if bean can't be founf.
   */
  private RestEJBConnectorRemote getBean() throws Exception {
    Properties env = new Properties();
//    env.put(Context.INITIAL_CONTEXT_FACTORY, "org.objectweb.carol.jndi.spi.MultiOrbInitialContextFactory");
//    env.put("java.naming.provider.url", "rmi://localhost:1099");
//    env.put("java.naming.factory.url.pkgs", "org.objectweb.carol.jndi.spi");
    env.put(Context.INITIAL_CONTEXT_FACTORY, "org.ow2.easybeans.component.smartclient.spi.SmartContextFactory");
    env.put("java.naming.provider.url", "rmi://localhost:2503");

    InitialContext ctx = new InitialContext(env);
    
    System.out.println("Looking for " + JNDI_NAME + "...");
    RestEJBConnectorRemote bean = (RestEJBConnectorRemote) ctx.lookup(JNDI_NAME);
    return bean;
  }

  public static void main(String[] args) throws Exception {
    Main m = new Main();
    SerialRequest request = new SerialRequest();

    RestEJBConnectorRemote bean = m.getBean();

    System.out.println("GET method (get data)...");
    request.setMethod("GET");
    request.setUrl(URL);
    SerialResponse response = bean.service(request);
    System.out.println(response.getStatus());
    if (response.getData() != null)
      printStream(response.getData().getStream());

    System.out.println("PUT method (new data)...");
    request.setMethod("PUT");
    request.setUrl(URL);
    request.setData(new SerialInputData(DATA.getBytes()));
    response = bean.service(request);
    System.out.println(response.getStatus());
    if (response.getData() != null)
      printStream(response.getData().getStream());

    System.out.println("GET method (get data)...");
    request.setMethod("GET");
    request.setUrl(URL);
    response = bean.service(request);
    System.out.println(response.getStatus());
    if (response.getData() != null)
      printStream(response.getData().getStream());

    System.out.println("POS method (change data)...");
    request.setMethod("POST");
    request.setUrl(URL);
    request.setData(new SerialInputData(new StringBuffer(DATA).reverse().toString().getBytes()));
    response = bean.service(request);
    System.out.println(response.getStatus());
    if (response.getData() != null)
      printStream(response.getData().getStream());

    System.out.println("GET method (get data)...");
    request.setMethod("GET");
    request.setUrl(URL);
    response = bean.service(request);
    System.out.println(response.getStatus());
    if (response.getData() != null)
      printStream(response.getData().getStream());

    System.out.println("DELETE method (get data)...");
    request.setMethod("DELETE");
    request.setUrl(URL);
    response = bean.service(request);
    System.out.println(response.getStatus());
    if (response.getData() != null)
      printStream(response.getData().getStream());
  }

  /**
   * Print content of stream to standard output.
   * @param in the source stream.
   * @throws IOException if i/o error occurs.
   */
  private static void printStream(InputStream in) throws IOException {
    int rd = -1;
    while ((rd = in.read()) != -1)
      System.out.print((char) rd);

    System.out.print('\n');
  }

}
