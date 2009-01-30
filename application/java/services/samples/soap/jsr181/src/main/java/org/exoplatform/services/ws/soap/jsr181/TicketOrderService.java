/*
 * Copyright (C) 2003-2007 eXo Platform SAS.
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

import javax.jws.WebService;

import org.exoplatform.services.ws.AbstractSingletonWebService;

/**
 * Created by The eXo Platform SAS .
 * 
 * @author <a href="mailto:alexey.zavizionov@exoplatform.com.ua">Alexey
 *         Zavizionov</a>
 * @version $Id: $ Sep 17, 2008
 */
@WebService
public interface TicketOrderService extends AbstractSingletonWebService {

  /**
   * @param departing departing place.
   * @param arriving arriving place.
   * @param departureDate departure date.
   * @param passenger passenger.
   * @return ticket order.
   */
  public String getTicket(String departing, String arriving, Date departureDate, String passenger);

  /**
   * @param confirmation confirm or not.
   */
  public void confirmation(boolean confirmation);

}
