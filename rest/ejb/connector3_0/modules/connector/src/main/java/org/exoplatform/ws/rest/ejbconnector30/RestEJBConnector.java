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

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.Map.Entry;

import javax.annotation.Resource;
import javax.annotation.security.DeclareRoles;
//import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.ejb.EJBException;
import javax.ejb.SessionContext;
import javax.ejb.Stateless;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;

import org.apache.commons.logging.Log;
import org.exoplatform.common.transport.SerialInputData;
import org.exoplatform.common.transport.SerialRequest;
import org.exoplatform.common.transport.SerialResponse;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.RootContainer;
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
// (mappedName = "RestEJBConnector")
@TransactionManagement(TransactionManagementType.BEAN)
@DeclareRoles({ "admin", "users" })
public class RestEJBConnector implements RestEJBConnectorRemote, RestEJBConnectorLocal {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger("ws.rest.RestEJBConnector");

  /**
   * SessionContext must be injected by EJB container.
   */
  @Resource
  private SessionContext context;

  /**
   * {@inheritDoc}
   */
//  @PermitAll
  @RolesAllowed({ "admin", "users" })
  public final SerialResponse service(SerialRequest request) throws RemoteException, IOException {
    ResourceDispatcher dispatcher = (ResourceDispatcher) getContainer().getComponentInstanceOfType(
        ResourceDispatcher.class);

    if (LOG.isDebugEnabled()) {
      if (context != null)
        LOG.debug("Caller principal " + context.getCallerPrincipal());
      else
        LOG.debug("Session context is null");
    }

    if (dispatcher == null) {
      LOG.error("ResourceDispatcher not found in container!");
      throw new EJBException("ResourceDispatcher not found in container!");
    }

    try {

      Request restRequest = new Request(request.getData() != null ? request.getData().getStream()
          : null, new ResourceIdentifier(request.getUrl()), request.getMethod(),
          createMultivaluedMetadata(request.getHeaders()), createMultivaluedMetadata(request
              .getQueries()));
      Response restResponse = dispatcher.dispatch(restRequest);

      SerialResponse response = new SerialResponse();

      response.setStatus(restResponse.getStatus());
      response.setHeaders(restResponse.getResponseHeaders().getAll());

      if (restResponse.getEntity() != null) {

        final File file = File.createTempFile("restejb-", null);
        OutputStream out = new FileOutputStream(file);

        restResponse.writeEntity(out);

        /*
         * Can't see any way to use some other then file. Writing is working at
         * the back (see REST engine).
         */
        InputStream in = new FileInputStream(file) {
          private boolean removed = false;

          @Override
          public void close() throws IOException {
            try {
              super.close();
            } finally {
              // file must be removed after using
              removed = file.delete();
            }
          }

          @Override
          protected void finalize() throws IOException {
            try {
              // if file was not removed
              if (!removed)
                file.delete();

            } finally {
              super.finalize();
            }
          }
        };

        response.setData(new SerialInputData(in));
      }

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
      return RootContainer.getInstance().getPortalContainer("portal");

    return container;
  }

  /**
   * Create MultivaluedMetadata.
   * @param map the source map.
   * @return the MultivaluedMetadata.
   */
  protected MultivaluedMetadata createMultivaluedMetadata(HashMap<String, String> map) {
    MultivaluedMetadata multivaluedMetadata = new MultivaluedMetadata();
    if (map != null) {
      for (Entry<String, String> entry : map.entrySet()) {
        String key = entry.getKey();
        StringTokenizer valueIter = new StringTokenizer(entry.getValue(), ",");
        while (valueIter.hasMoreTokens()) {
          String value = valueIter.nextToken().trim();
          multivaluedMetadata.putSingle(key, value);
        }
      }
    }

    return multivaluedMetadata;
  }

}
