/***************************************************************************
 * Copyright 2001-2007 The eXo Platform SARL         All rights reserved.  *
 * Please look at license.txt in info directory for more license detail.   *
 **************************************************************************/
package org.exoplatform.services.rest.ejb21;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.Request;
import org.exoplatform.services.rest.ResourceBinder;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.ResourceIdentifier;
import org.exoplatform.services.rest.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class RestEJBConnectorBean implements SessionBean {

  static final long serialVersionUID = 234765347623L;

  private ExoContainer container;

  private ResourceDispatcher resDispatcher;

  private ResourceBinder resBinder;

  private static final Log LOGGER = ExoLogger
      .getLogger(RestEJBConnectorBean.class);

  SessionContext context;

  /**
   * @param str - data String.
   * @param method - HTTP method.
   * @param url - URL.
   * @param headers - Map of HTTP headers.
   * @param queries - Map of query parameters.
   * @return - result String from REST service.
   */
  public String service(String str, String method, String url,
      HashMap<String, List<String>> headers,
      HashMap<String, List<String>> queries) {

    try {
//      container = ExoContainerContext.getCurrentContainer();
      // >>> temporary
      container = ExoContainerContext.getContainerByName("portal");
    } catch (Exception e) {
      LOGGER.error("Can't get current container!");
      throw new EJBException("Can't get current container!", e);
    }
    resBinder = (ResourceBinder) container
        .getComponentInstanceOfType(ResourceBinder.class);
    resDispatcher = (ResourceDispatcher) container
        .getComponentInstanceOfType(ResourceDispatcher.class);

    if (resBinder == null) {
      LOGGER.error("ResourceBinder not found in container!");
      throw new EJBException("ResourceBinder not found in container!");
    }
    if (resDispatcher == null) {
      LOGGER.error("ResourceDispatcher not found in container!");
      throw new EJBException("ResourceDispatcher not found in container!");
    }
    try {
      // This is simple example. Work only with string.
      InputStream dataStream = null;
      if (str != null) {
        dataStream = new ByteArrayInputStream(str.getBytes());
      }
      Request req = new Request(dataStream, new ResourceIdentifier(url),
          method, new MultivaluedMetadata(headers), new MultivaluedMetadata(
              queries));

      String respString = null;
      Response resp = resDispatcher.dispatch(req);
      if (resp.getEntity() != null) {
        respString = (String) resp.getEntity();
      }
      return respString;

    } catch (Exception e) {
      LOGGER.error("This request cann't be serve by service.\n"
          + "Check request parameters and try again.");
      throw new EJBException("This request can't be serve!", e);
    }
  }

  /**
   * @param str -
   *            data string.
   * @param method -
   *            HTTP method.
   * @param url -
   *            URL
   * @return - result String from REST service.
   */
  public String service(String str, String method, String url) {
    return service(str, method, url, new HashMap<String, List<String>>(),
        new HashMap<String, List<String>>());
  }

  /**
   * @param method -
   *            HTTP method.
   * @param url -
   *            URL
   * @return - result String from REST service.
   */
  public String service(String method, String url) {
    return service(null, method, url, new HashMap<String, List<String>>(),
        new HashMap<String, List<String>>());
  }

  public void ejbPassivate() {
  }

  public void ejbActivate() {
  }

  public void ejbRemove() {
  }

  public void setSessionContext(SessionContext contx) {
    context = contx;
  }

  public void ejbCreate() {
  }
}
