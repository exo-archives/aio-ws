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

package org.exoplatform.services.ws.rest.ejb3;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import javax.ejb.EJBException;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.MultivaluedMetadata;
import org.exoplatform.services.rest.Request;
import org.exoplatform.services.rest.ResourceDispatcher;
import org.exoplatform.services.rest.ResourceIdentifier;
import org.exoplatform.services.rest.Response;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Stateless
public class RestEJBConnectorBean implements RestEJBConnectorRemote,
    RestEJBConnectorLocal {

  private ExoContainer container;

  private ResourceDispatcher resDispatcher;

  private static final Log LOGGER = ExoLogger.getLogger(RestEJBConnectorBean.class);

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.ejb3.RestEJBConnector#service(java.lang.String,
   *      java.lang.String, java.lang.String, java.util.HashMap, java.util.HashMap)
   */
  public String service(String str, String method, String url,
      HashMap < String, List < String >> headers, HashMap < String, List < String >> queries) {

    try {
      // =========== temporary ============
      // TODO: change to get actual container
//      container = ExoContainerContext.getCurrentContainer();
      container = ExoContainerContext.getContainerByName("portal");
    } catch (Exception e) {
      LOGGER.error("Can't get current container!");
      throw new EJBException("Can't get current container!", e);
    }
    resDispatcher = (ResourceDispatcher) container
        .getComponentInstanceOfType(ResourceDispatcher.class);
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
      Request req = new Request(dataStream, new ResourceIdentifier(url), method,
          new MultivaluedMetadata(headers), new MultivaluedMetadata(queries));

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

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.ejb3.RestEJBConnector#service
   * (java.lang.String, java.lang.String, java.lang.String)
   */
  public String service(String str, String method, String url) {
    return service(str, method, url, new HashMap < String, List < String > >(),
        new HashMap < String, List < String > >());
  }

  /*
   * (non-Javadoc)
   * @see org.exoplatform.services.rest.ejb3.RestEJBConnector#service(java.lang.String, java.lang.String)
   */
  public String service(String method, String url) {
    return service(null, method, url, new HashMap < String, List < String > >(),
        new HashMap < String, List < String > >());
  }

}
