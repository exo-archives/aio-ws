/**
 * Copyright (C) 2003-2008 eXo Platform SAS.
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

package org.exoplatform.ws.rest.ejbconnector21;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.ws.rs.core.MultivaluedMap;

import org.exoplatform.services.rest.ext.transport.SerialInputData;
import org.exoplatform.services.rest.ext.transport.SerialRequest;
import org.exoplatform.services.rest.ext.transport.SerialResponse;
import org.exoplatform.services.rest.impl.MultivaluedMapImpl;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Main {

  /**
   * URL of simple service.
   */
  private static final String SERVICE_URI = "/samples/string";

  /**
   * Bean JNDI name.
   */
  private static final String BEAN_NAME   = "RestEJBConnector";

  /**
   * Sample data.
   */
  private static final String DATA        = "Hello world";

  /**
   * Looking for bean.
   * 
   * @return home interface of bean.
   * @throws Exception if bean can't be found in JNDI context.
   */
  private RestEJBConnector getBean() throws Exception {
    InitialContext ctx = new InitialContext();
    System.out.println("Looking for " + BEAN_NAME + "...");
    Object obj = ctx.lookup(BEAN_NAME);
    RestEJBConnectorHome restEJBConnectorHome = (RestEJBConnectorHome) PortableRemoteObject.narrow(obj,
                                                                                                   RestEJBConnectorHome.class);

    return restEJBConnectorHome.create();
  }

  public static void main(String[] args) throws Exception {
    Main main = new Main();

    RestEJBConnector bean = main.getBean();
    URI serviceURI = new URI(SERVICE_URI + "?test=this%20is%20test%20query");

    MultivaluedMap<String, String> headers = new MultivaluedMapImpl();
    headers.add("content-type", "text/plain");
    headers.add("test", "this is test header ");
    callService("GET", serviceURI, null, null, bean);
    callService("PUT", serviceURI, headers, new SerialInputData(DATA.getBytes()), bean);
    callService("GET", serviceURI, null, null, bean);
    callService("POST",
                serviceURI,
                headers,
                new SerialInputData(new StringBuffer(DATA).reverse().toString().getBytes()),
                bean);
    callService("GET", serviceURI, null, null, bean);
    callService("DELETE", serviceURI, null, null, bean);
  }

  private static void callService(String method,
                                  URI serviceURI,
                                  MultivaluedMap<String, String> headers,
                                  SerialInputData data,
                                  RestEJBConnector bean) throws Exception {
    System.out.println("\t>>> method " + method);
    SerialRequest request = new SerialRequest(method, serviceURI, headers, data);

    SerialResponse response = bean.service(request);
    System.out.println("response status: " + response.getStatus());
    System.out.println("headers: ");
    printHeaders(response.getHeaders());
    if (response.getData() != null)
      printStream(response.getData().getStream());
  }

  /**
   * Print stream data at standard output.
   * 
   * @param in stream.
   * @throws IOException if i/o error occurs.
   */
  private static void printStream(InputStream in) throws IOException {
    int rd = -1;
    while ((rd = in.read()) != -1)
      System.out.print((char) rd);

    System.out.print('\n');
  }
  
  /**
   * @param headers HTTP headers
   */
  private static void printHeaders(MultivaluedMap<String, String> headers) {
    Iterator<Map.Entry<String, List<String>>> iter = headers.entrySet().iterator();
    while (iter.hasNext()) {
      Map.Entry<String, List<String>> e = iter.next();
      System.out.print(e.getKey() + ": ");
      int i = 0;
      for (String v : e.getValue()) {
        if (i > 0)
          System.out.print(',');
        System.out.print(v);
      }
      System.out.print('\n');
    }
  }

}
