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

/**
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
public class Ticket {
  
  /**
   * Passenger.
   */
  private String passenger;
  
  /**
   * Departing place. 
   */
  private String departing;
  
  /**
   * Arriving place.
   */
  private String arriving;
  
  /**
   * Departure date. 
   */
  private Date departureDate;
  
  /**
   * Order. 
   */
  private long order;
  
  /**
   * Construct new instance of Ticket.
   * @param passenger the passenger.
   * @param departing the departing place.
   * @param arriving the arriving place.
   * @param departureDate the departure date.
   */
  public Ticket(String passenger, String departing,
      String arriving, Date departureDate) {
    this.passenger = passenger;
    this.departing = departing;
    this.arriving = arriving;
    this.departureDate = departureDate;
    this.order = System.currentTimeMillis();
  }
  
  /**
   * @return ticket order.
   */
  public long getOrder() {
    return order;
  }
  
  /**
   * {@inheritDoc}
   */
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Ticket {").append(passenger).append(", ")
      .append(arriving).append(", ")
      .append(departing).append(", ")
      .append(departureDate).append(", ")
      .append(order).append("}");
    return sb.toString();
  }

}
