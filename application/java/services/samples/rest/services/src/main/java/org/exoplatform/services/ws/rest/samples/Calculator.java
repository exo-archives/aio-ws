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

package org.exoplatform.services.ws.rest.samples;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * Simple service for REST demo.
 * 
 * @author <a href="mailto:andrew00x@gmail.com">Andrey Parfonov</a>
 * @version $Id: $
 */
@Path("/samples/calculator")
public class Calculator implements ResourceContainer {

  /**
   * Add two items.
   * 
   * @param firstItem the first item.
   * @param secondItem the second item.
   * @return result.
   */
  @GET
  @Path("/add/{1}/{2}")
  @Produces( { MediaType.TEXT_PLAIN })
  public String add(@PathParam("1") Integer firstItem, @PathParam("2") Integer secondItem) {
    return firstItem + secondItem + "";
  }

  /**
   * Subtract second item from first one.
   * 
   * @param firstItem the first item.
   * @param secondItem the second item.
   * @return result.
   */
  @GET
  @Path("/subtract/{1}/{2}")
  @Produces( { MediaType.TEXT_PLAIN })
  public String subtract(@PathParam("1") Integer firstItem, @PathParam("2") Integer secondItem) {
    return firstItem - secondItem + "";
  }

  /**
   * Multiply two items.
   * 
   * @param firstItem the first item.
   * @param secondItem the second item.
   * @return result.
   */
  @GET
  @Path("/multiply/{1}/{2}")
  @Produces( { MediaType.TEXT_PLAIN })
  public String multiply(@PathParam("1") Integer firstItem, @PathParam("2") Integer secondItem) {
    return firstItem * secondItem + "";
  }

  /**
   * Divide two items.
   * 
   * @param firstItem the first item.
   * @param secondItem the second item.
   * @return result.
   */
  @GET
  @Path("/divide/{1}/{2}")
  @Produces( { MediaType.TEXT_PLAIN })
  public String divide(@PathParam("1") Integer firstItem, @PathParam("2") Integer secondItem) {
    return firstItem / secondItem + "";
  }
}
