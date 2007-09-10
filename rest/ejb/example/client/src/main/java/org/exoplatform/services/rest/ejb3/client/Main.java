/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.ejb3.client;

import static java.lang.System.out;


import javax.ejb.EJBAccessException;
import javax.naming.InitialContext;

import org.exoplatform.services.rest.ejb3.SimpleRestEJBConnector;
import org.exoplatform.services.rest.ejb3.SimpleRestEJBConnectorRemote;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Main {

  private static final String URL = "/ejb/simple-service/";
  // only for easybeans container on Jonas
  private static final String BEAN_NAME =
    "org.exoplatform.services.rest.ejb3.SimpleRestEJBConnectorBean";
  // for JBoss
//  private static final String BEAN_NAME = "SimpleRestEJBConnectorBean/remote";
  private static final String BEAN_REMOTE_SUFFIX = "@Remote";

  
  public void doGet(int id) {
    SimpleRestEJBConnector bean = getBean();
    if (bean != null) {
      out.println("Response: "
          + bean.service("GET", URL + id + "/"));
    }
  }
  
  public void doPost(String data, int id) {
    SimpleRestEJBConnector bean = getBean();
    if (bean != null) {
      out.println("Response: "
          + bean.service(data, "POST", URL + id + "/"));
    }
  }

  public void doDelete(int id) {
    SimpleRestEJBConnector bean = getBean();
    if (bean != null) {
      out.println("Response: "
          + bean.service("DELETE", URL + id + "/"));
    }
  }

  public void doPut(String data) {
    SimpleRestEJBConnector bean = getBean();
    if (bean != null) {
      out.println("Response: "
          + bean.service(data, "PUT", URL));
    }
  }

  public void init() {
    SimpleRestEJBConnector bean = getBean();
    if (bean != null) {
      out.println("Response: "
          + bean.service("DELETE", URL));
    }
  }
  
  
  private SimpleRestEJBConnector getBean() {
    try {
      InitialContext ctx = new InitialContext();
      out.println("Serching RestEJBServiceBean...");
      SimpleRestEJBConnector bean = (SimpleRestEJBConnector) ctx.lookup(BEAN_NAME + "_"
          + SimpleRestEJBConnectorRemote.class.getName() + BEAN_REMOTE_SUFFIX);
      return bean;
    } catch (Exception e) {
      throw new EJBAccessException("Bean SimpleRestEJBConnectorBean not found through JNDI!");
    }
  }
  
  /**
   * @param args
   */
  public static void main(String[] args) {
    Main m = new Main();
    m.init();
    out.println("\tPut method (send new data)...");
    for (int i = 0; i < 5; i++) {
      m.doPut("test_string_" + i);
    }
    out.println("\tGet method (get data)...");
    for (int i = 0; i < 5; i++) {
      m.doGet(i);
    }
    out.println("\tPost method (change data)...");
    for (int i = 0; i < 5; i++) {
      m.doPost(("test_string_" + i).toUpperCase(), i);
    }
    out.println("\tGet method (get data)...");
    for (int i = 0; i < 5; i++) {
      m.doGet(i);
    }
    out.println("\tDelete method (remove data)...");
    for (int i = 4; i >= 0; i--) {
      m.doDelete(i);
    }
  }

}
