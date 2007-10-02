/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.ejb21.client;

import static java.lang.System.out;

import javax.ejb.EJBException;
import javax.naming.InitialContext;
import javax.rmi.PortableRemoteObject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.exoplatform.services.rest.ejb21.RestEJBConnector;
import org.exoplatform.services.rest.ejb21.RestEJBConnectorHome;

import org.objectweb.jonas.security.auth.callback.LoginCallbackHandler;


/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Main {

  private static final String URL = "/ejb/simple-service/";
  private static final String BEAN_NAME = "RestEJBConnector";

  private RestEJBConnector getBean() {
    try {
      InitialContext ctx = new InitialContext();
      out.println("Serching RestEJBServiceBean...");
      Object obj = ctx.lookup(BEAN_NAME);
      RestEJBConnectorHome restEJBConnectorHome =
        (RestEJBConnectorHome)PortableRemoteObject.narrow(obj, RestEJBConnectorHome.class);
      return restEJBConnectorHome.create();
    } catch (Exception e) {
      e.printStackTrace();
      throw new EJBException("Can't get bean RestEJBConnectorBean!");
    }
  }
  
  public static void main(String[] args) throws Exception {
    Main m = new Main();
    
//    CallbackHandler handler = new LoginCallbackHandler();
//    
//    LoginContext login = new LoginContext("ask_remote", handler);
//    
//    try {
//      login.login();
//      System.out.println("Loggin ok");
//    } catch(LoginException le) {
//      le.printStackTrace();
//    }
    
    RestEJBConnector bean = m.getBean();

    out.println("Response: " + bean.service("DELETE", URL));

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
