/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SAS         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.ejb21;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJBObject;

/**
 * Work with REST service through EJB. Remote interface.
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public interface RestEJBConnector extends EJBObject {

  /**
   * @param str - data String.
   * @param method - HTTP method.
   * @param url - URL.
   * @param headers - Map of HTTP headers.
   * @param queries - Map of query parameters.
   * @return - result String from REST service.
   */
  public String service(String str, String method, String url,
      HashMap < String, List < String > > headers,
      HashMap < String, List < String > > queries) throws RemoteException;

  /**
   * @param str - data string.
   * @param method - HTTP method.
   * @param url - URL
   * @return - result String from REST service.
   */
  public String service(String str,
      String method, String url) throws RemoteException ;

  /**
   * @param method - HTTP method.
   * @param url - URL
   * @return - result String from REST service.
   */
  public String service(String method, String url) throws RemoteException;
}
