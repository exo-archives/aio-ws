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
  
  private String passenger_;
  private String departing_;
  private String arriving_;
  private Date departureDate_;
  private long order_;
  
  public Ticket(String passenger, String departing,
      String arriving, Date departureDate) {
    passenger_ = passenger;
    departing_ = departing;
    arriving_ = arriving;
    departureDate_ = departureDate;
    order_ = System.currentTimeMillis();
  }
  
  public long getOrder() {
    return order_;
  }
  
  @Override
  public String toString() {
    StringBuffer sb = new StringBuffer();
    sb.append("Ticket {").append(passenger_).append(", ")
      .append(arriving_).append(", ")
      .append(departing_).append(", ")
      .append(departureDate_).append(", ")
      .append(order_).append("}");
    return sb.toString();
  }

}
