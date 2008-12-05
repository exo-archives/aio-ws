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

package org.exoplatform.services.ws.soap.jsr181.custom;

import java.util.Date;

import javax.jws.WebService;

import org.apache.commons.logging.Log;
import org.exoplatform.services.log.ExoLogger;
import org.exoplatform.services.ws.soap.jsr181.Ticket;

/**
 * Simple ticket service for SOAP demo.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@WebService(serviceName = "TicketOrderServiceCustom", 
            portName = "TicketOrderServiceCustomPort", 
            targetNamespace = "http://exoplatform.org/soap/cxf")
public class TicketOrderServiceImpl implements TicketOrderService {

  /**
   * Logger.
   */
  private static final Log LOG = ExoLogger.getLogger(TicketOrderServiceImpl.class);

  /**
   * Ticket.
   */
  private Ticket           ticket;

  /**
   * @param departing departing place.
   * @param arriving arriving place.
   * @param departureDate departure date.
   * @param passenger passenger.
   * @return ticket order.
   */
  public String getTicket(String departing, String arriving, Date departureDate, String passenger) {
    ticket = new Ticket(passenger, departing, arriving, departureDate);
    LOG.info(ticket);
    try {
      System.out.println(">>> TicketOrderServiceImpl.getTicket() sleeping for 2 sec ");
      Thread.currentThread().sleep(1000);
    } catch (Exception e) {
    }
    return String.valueOf(ticket.getOrder());
  }

  /**
   * @param confirmation confirm or not.
   */
  public void confirmation(boolean confirmation) {
    LOG.info("Confirmation : " + confirmation + " for order '" + ticket.getOrder() + "'.");
  }

}
