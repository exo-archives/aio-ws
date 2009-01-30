/**
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

package org.exoplatform.services.ws.rest.samples;

import java.net.URI;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.HeaderParam;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

import org.exoplatform.services.rest.resource.ResourceContainer;

/**
 * This service does not do real work, it useful to check rest-ejb connector.
 */
@Path("/samples/string")
public class SimpleService implements ResourceContainer {

  /**
   * Keeps data.
   */
  private String data;

  /**
   * Get actual data.
   * 
   * @return response with actual data as body or response with status 204 if
   *         data is null.
   */
  @GET
  @Produces( { "text/plain" })
  public String get() {
    return data;
  }

  /**
   * Set data.
   * 
   * @param data the data.
   * @return response with status 201.
   * @throws Exception
   */
  @POST
  @Consumes( { "text/plain" })
  public Response post(String data,
                       @HeaderParam("test") String header,
                       @QueryParam("test") String query) throws Exception {
    this.data = data;
    return Response.created(new URI("/simple-service/"))
                   .header("test_header", header)
                   .header("test_query", query)
                   .build();
  }

  /**
   * Set data.
   * 
   * @param data the data.
   * @return response with status 201.
   */
  @PUT
  @Consumes( { "text/plain" })
  public Response put(String data,
                      @HeaderParam("test") String header,
                      @QueryParam("test") String query) throws Exception {
    this.data = data;
    return Response.created(new URI("/simple-service/"))
                   .header("test_header", header)
                   .header("test_query", query)
                   .build();
  }

  /**
   * Delete data.
   * 
   * @return response with status 204.
   */
  @DELETE
  public void delete() {
    this.data = null;
  }

}
