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
import org.exoplatform.services.rest.OutputTransformer;
import org.exoplatform.services.rest.Response;
import org.exoplatform.services.rest.URIParam;
import org.exoplatform.services.rest.URITemplate;
import org.exoplatform.services.rest.container.ResourceContainer;
import org.exoplatform.services.rest.transformer.StringOutputTransformer;
import org.exoplatform.ws.frameworks.cometd.transport.ContinuationServiceDelegate;
import org.exoplatform.ws.frameworks.cometd.transport.DlegateMessage;
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
  
//  private List<String> messges;
  
  
  
  
/*  @HTTPMethod(HTTPMethods.GET)
  @URITemplate("/ext/gettoken/{exoid}/")
  @OutputTransformer(StringOutputTransformer.class)
  public Response getToken(@URIParam("exoid") String exoid){
    System.out.println("TransferMessages.getToken() " + exoid);
    ContinuationServiceDelegate transport = getCometdTransport();
    String token = transport.getUserToken(exoid);
    log.info("send tokken " + token);
    return Response.Builder.ok(token).build();
  }
 */ 
  
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/ext/sendprivatemessage/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response sendMessage(DlegateMessage transportData){
    System.out.println("TransferMessages.sendMessage() channel : " + transportData.getChannel() + " exoid " + transportData.getExoId() + " : " + transportData.getMessage());
    ContinuationServiceDelegate transport = getCometdTransport();
    transport.sendMessage(transportData.getChannel(), transportData.getExoId(), transportData.getMessage(),transportData.getId());
    log.info("send message " + transportData.getMessage());
    return Response.Builder.ok().build();
  }
  
  @HTTPMethod(HTTPMethods.POST)
  @URITemplate("/ext/sendbroadcastmessage/")
  @InputTransformer(Json2BeanInputTransformer.class)
  public Response sendBroadcastMessage(DlegateMessage data){
    ContinuationServiceDelegate transport = getCometdTransport();
    transport.sendBroadcastMessage(data.getChannel(), data.getMessage(),data.getId());
    log.info("send broadcast message " + data.getMessage());
    return Response.Builder.ok().build();
  }
  
  private ContinuationServiceDelegate getCometdTransport(){
    ExoContainer container = ExoContainerContext.getCurrentContainer();
    ContinuationServiceDelegate transport = (ContinuationServiceDelegate) container.getComponentInstanceOfType(ContinuationServiceDelegate.class);
    return transport;
  }
  
  
}
