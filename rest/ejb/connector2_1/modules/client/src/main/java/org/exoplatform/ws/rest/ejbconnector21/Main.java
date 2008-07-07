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

import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;

import org.exoplatform.common.transport.SerialInputData;
import org.exoplatform.common.transport.SerialRequest;
import org.exoplatform.common.transport.SerialResponse;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Main {

  private static final String URL = "/simple-service/";
  private static final String BEAN_NAME = "RestEJBConnector";

  private static final String data = "Hello world";

  private RestEJBConnector getBean() throws Exception {
      InitialContext ctx = new InitialContext();
      System.out.println("Looking for " + BEAN_NAME + "...");
      Object obj = ctx.lookup(BEAN_NAME);
      RestEJBConnectorHome restEJBConnectorHome = (RestEJBConnectorHome) PortableRemoteObject
          .narrow(obj, RestEJBConnectorHome.class);

      return restEJBConnectorHome.create();
  }

  public static void main(String[] args) throws Exception {
    Main main = new Main();
    SerialRequest request = new SerialRequest();

    RestEJBConnector bean = main.getBean();

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
    request.setData(new SerialInputData(data.getBytes()));
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
    request.setData(new SerialInputData(new StringBuffer(data).reverse().toString().getBytes()));
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

  private static void printStream(InputStream in) throws IOException {
    int rd = -1;
    while ((rd = in.read()) != -1)
      System.out.print((char) rd);

    System.out.print('\n');
  }

}
