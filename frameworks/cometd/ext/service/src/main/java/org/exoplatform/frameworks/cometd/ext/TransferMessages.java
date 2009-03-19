/*
 * Copyright (C) 2003-2008 eXo Platform SAS.
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Affero General Public License
 * as published by the Free Software Foundation; either version 3
 * of the License, or (at your option) any later version.

 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.

 * You should have received a copy of the GNU General Public License
 * along with this program; if not, see<http://www.gnu.org/licenses/>.
 */
package org.exoplatform.frameworks.cometd.ext;


import org.apache.commons.logging.Log;
import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.ws.frameworks.cometd.transport.ContinuationServiceDelegate;
import org.exoplatform.ws.frameworks.cometd.transport.DelegateMessage;
import org.exoplatform.ws.frameworks.json.transformer.Json2BeanInputTransformer;


/**
 * Created by The eXo Platform SAS.
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

 
public class TransferMessages implements ResourceContainer {
  /**
   * Class logger.
   */
  private final Log log = ExoLogger.getLogger("ws.CometdTestSendMessage");
  
 
  

  
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/ext/sendprivatemessage/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response sendMessage(DelegateMessage transportData) {
    ContinuationServiceDelegate transport = getCometdTransport();
    transport.sendMessage(transportData.getExoId(), transportData.getChannel(), transportData.getMessage(), transportData.getId());
    return Response.Builder.ok().build();
  }
  
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/ext/sendbroadcastmessage/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response sendBroadcastMessage(DelegateMessage data) {
    ContinuationServiceDelegate transport = getCometdTransport();
    transport.sendBroadcastMessage(data.getChannel(), data.getMessage(), data.getId());
    return Response.Builder.ok().build();
  }
  
  private ContinuationServiceDelegate getCometdTransport() {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    ContinuationServiceDelegate transport = (ContinuationServiceDelegate) container.getComponentInstanceOfType(ContinuationServiceDelegate.class);
    return transport;
  }
  
  
}
