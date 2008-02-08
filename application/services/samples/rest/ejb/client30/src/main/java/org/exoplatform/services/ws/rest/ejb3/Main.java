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

package org.exoplatform.services.ws.rest.ejb3;

import static java.lang.System.out;

import javax.ejb.EJBException;
import javax.naming.InitialContext;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Main {

  private static final String URL = "/ejb/simple-service/";
  // only for easybeans container on Jonas
  private static final String BEAN_NAME =
    "org.exoplatform.services.ws.rest.ejb3.RestEJBConnectorBean";
  // for JBoss
//  private static final String BEAN_NAME = "RestEJBConnectorBean/remote";
  private static final String BEAN_REMOTE_SUFFIX = "@Remote";

  public void init() {
    RestEJBConnector bean = getBean();
    if (bean != null) {
      out.println("Response: "
          + bean.service("DELETE", URL));
    }
  }
  
  
  private RestEJBConnector getBean() {
    try {
      InitialContext ctx = new InitialContext();
      out.println("Serching RestEJBServiceBean...");
      RestEJBConnector bean = (RestEJBConnector) ctx.lookup(BEAN_NAME + "_"
          + RestEJBConnectorRemote.class.getName() + BEAN_REMOTE_SUFFIX);
      return bean;
    } catch (Exception e) {
      throw new EJBException("Can't get bean RestEJBConnectorBean!");
    }
  }
  
  public static void main(String[] args) {
    Main m = new Main();
    
    RestEJBConnector bean = m.getBean();

    m.init();

    String data = "test_string_";
    
    out.println("\tPut method (send new data)...");
    for (int i = 0; i < 5; i++) {
      out.println("Response: " + bean.service(data + i, "PUT", URL));
    }
    out.println("\tGet method (get data)...");
    for (int i = 0; i < 5; i++) {
      out.println("Response: " + bean.service("GET", URL + i + "/"));
    }
    out.println("\tPost method (change data)...");
    for (int i = 0; i < 5; i++) {
      out.println("Response: "
          + bean.service(new StringBuffer(data + i).reverse().toString(), "POST", URL + i + "/"));
    }
    out.println("\tGet method (get data)...");
    for (int i = 0; i < 5; i++) {
      out.println("Response: " + bean.service("GET", URL + i + "/"));
    }
    out.println("\tDelete method (remove data)...");
    for (int i = 4; i >= 0; i--) {
      out.println("Response: " + bean.service("DELETE", URL + i));
    }
  }

}
