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

package org.exoplatform.ws.rest.ejbconnector30;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.rmi.RemoteException;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
//import javax.ejb.Local;
//import javax.ejb.Remote;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.exoplatform.services.log.Log;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.GenericContainerRequest;
import org.exoplatform.services.rest.GenericContainerResponse;
import org.exoplatform.services.rest.RequestHandler;
import org.exoplatform.services.rest.ext.transport.SerialRequest;
import org.exoplatform.services.rest.ext.transport.SerialResponse;
import org.exoplatform.services.rest.impl.ContainerResponse;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Stateless
//(mappedName="RestEJBConnector")
@TransactionManagement(TransactionManagementType.BEAN)
@DeclareRoles({ "admin", "users" })
//@Local(RestEJBConnectorLocal.class)
//@Remote(RestEJBConnectorRemote.class)
public class RestEJBConnector implements RestEJBConnectorRemote, RestEJBConnectorLocal {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("ws.rest.RestEJBConnector");

  /**
   * SessionContext must be injected by EJB container.
   */
  @Resource
  private SessionContext   context;

  /**
   * Container name.
   */
  @Resource(name = "exo.container.name")
  private String           containerName;

  /**
   * {@inheritDoc}
   */
  // @PermitAll
  @RolesAllowed({ "admin", "users" })
  public final SerialResponse service(final SerialRequest request) throws RemoteException,
                                                                  IOException {
    RequestHandler handler = (RequestHandler) getContainer().getComponentInstanceOfType(RequestHandler.class);
    if (handler == null) {
      LOG.error("RequestHandler not found in container!");
      throw new EJBException("RequestHandler not found in container!");
    }

    if (LOG.isDebugEnabled()) {
      if (context != null)
        LOG.debug("Caller principal " + context.getCallerPrincipal());
      else
        LOG.debug("Session context is null");
    }

    if (handler == null) {
      LOG.error("RequestHandler not found in container!");
      throw new EJBException("RequestHandler not found in container!");
    }

    try {

      InputStream data = request.getData() != null ? request.getData().getStream() : null;

      GenericContainerRequest restRequest = new EJBContainerRequest(request.getMethod(),
                                                             request.getUri(),
                                                             new URI(""),
                                                             data,
                                                             request.getHeaders(),
                                                             context);
      SerialResponse response = new SerialResponse();

      EJBContainerResponseWriter writer = new EJBContainerResponseWriter(response);
      GenericContainerResponse restResponse = new ContainerResponse(writer);

      handler.handleRequest(restRequest, restResponse);

      return response;

    } catch (Exception e) {
      LOG.error("This request can't be serve by service. Check request parameters and try again.");
      throw new EJBException("This request can't be serve!", e);
    }
  }

  /**
   * @return actual ExoContainer.
   */
  protected ExoContainer getContainer() {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    if (container instanceof RootContainer)
      return RootContainer.getInstance().getPortalContainer(containerName);

    return container;
  }

}
