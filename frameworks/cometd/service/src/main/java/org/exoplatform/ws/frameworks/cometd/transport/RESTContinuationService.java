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
package org.exoplatform.ws.frameworks.cometd.transport;

import org.apache.commons.logging.Log;
import org.exoplatform.common.http.HTTPMethods;
import org.exoplatform.container.ExoContainer;
import org.exoplatform.container.ExoContainerContext;
import org.exoplatform.container.RootContainer;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.rest.HTTPMethod;
import org.exoplatform.services.rest.InputTransformer;
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.QueryParam;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;
import org.exoplatform.ws.frameworks.cometd.ContinuationService;
import org.exoplatform.ws.frameworks.json.transformer.Json2BeanInputTransformer;

/**
 * Created by The eXo Platform SAS.
 * 
 * @author <a href="mailto:vitaly.parfonov@gmail.com">Vitaly Parfonov</a>
 * @version $Id: $
 */

public class RESTContinuationService implements ResourceContainer {
  /**
   * Class logger.
   */
  private final Log log = ExoLogger.getLogger("ws.RestServiceForCometdTransport");

  /**
   * @param exoID the id of client. 
   * @return userToken for user 
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/gettoken/{exoID}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response getToken(@URIParam("exoID") String exoID) {
    ContinuationService continuation = getContinuationService();
    String token = continuation.getUserToken(exoID);
    if (log.isInfoEnabled())
      log.info("Client with exoId " + exoID + " get token " + token);
    return Response.Builder.ok(token, "text/txt").build();
  }

  /**
   * @param exoID the id of client.
   * @param channel the id of channel
   * @return true if client subscribed on channel else false
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/issubscribed/{exoID}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response isSubscribed(@URIParam("exoID") String exoID,
                               @QueryParam("channel") String channel) {
    ContinuationService continuation = getContinuationService();
    Boolean b = continuation.isSubscribe(exoID, channel);
    if (log.isInfoEnabled())
      log.info("Is subcribed client " + exoID + " on channel " + channel + " " + b);
    return Response.Builder.ok(b.toString(), "text/txt").build();
  }

  /**
   * @param channel the id of channel
   * @return true if channel exist else false
   */
  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/haschannel/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response hasChannel(@QueryParam("channel") String channel) {
    ContinuationService continuation = getContinuationService();
    Boolean b = continuation.hasChannel(channel);
    if (log.isInfoEnabled())
      log.info("Has channel " + channel + " " + b);
    return Response.Builder.ok(b.toString(), "text/txt").build();
  }

  /**
   * @param data content message, clientId, channel. 
   * @return Response with status 
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/sendprivatemessage/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response sendMessage(DlegateMessage data) {
    ContinuationService continuation = getContinuationService();
    continuation.sendMessage(data.getExoId(), data.getChannel(), data.getMessage(), data.getId());
    if (log.isInfoEnabled())
     log.info("Send private message " + data.getMessage() + " on channel " + data.getChannel() + " to client " + data.getExoId());
    return Response.Builder.ok().build();
  }

  /**
   * @param data content message, clientId, channel.
   * @return Response with status
   */
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/sendbroadcastmessage/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response sendBroadcastMessage(DlegateMessage data) {
    ContinuationService continuation = getContinuationService();
    continuation.sendBroadcastMessage(data.getChannel(), data.getMessage(), data.getId());
    if (log.isInfoEnabled())
      log.info("Send broadcast message " + data.getMessage() + " on channel " + data.getChannel());
    return Response.Builder.ok().build();
  }

  /**
   * @return Continuation service.
   */
  private ContinuationService getContinuationService() {
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    if (container == null) {
      container = ExoContainerContext.getContainerByName("portal");
    }
    if (container instanceof RootContainer) {
      container = RootContainer.getInstance().getPortalContainer("portal");
    }
    ContinuationService continuation = (ContinuationService) container.getComponentInstanceOfType(ContinuationService.class);
    return continuation;
  }

}
