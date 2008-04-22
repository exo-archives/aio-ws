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

package org.exoplatform.services.ws.soap.jsr181;

import java.util.Date;

import javax.jws.WebMethod;
import javax.jws.WebParam;
import javax.jws.WebResult;
import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.ws.AbstractWebService;

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@WebService(name = "TicketOrderService",
    serviceName = "TicketOrderService",
    targetNamespace = "http://exoplatform.org/soap/xfire")
public class TicketOrderService implements AbstractWebService {
  
  private static final Log LOGGER = ExoLogger.getLogger("ws.TicketOrderService");
  
  private Ticket ticket;
  
  @WebMethod(operationName = "getTicket", action = "urn:GetTicket")
  @WebResult(name = "Ticket")
  public long getTicket(@WebParam(name = "departing", header = true) String departing,
      @WebParam(name = "arriving", header = true) String arriving,
      @WebParam(name = "departureDate", header = true) Date departureDate,
      @WebParam(name = "passenger", header = true) String passenger) {
    ticket = new Ticket(passenger, departing, arriving, departureDate);
    LOGGER.info(ticket);
    return ticket.getOrder();
  }
  
  @WebMethod(operationName = "confirmation", action = "urn:Confirmation")
  public void confirmation(@WebParam(name = "confirm", header = true) boolean confirmation) {
    LOGGER.info("Confirmation : " + confirmation + " for order '" + ticket.getOrder() + "'.");
  }

}
